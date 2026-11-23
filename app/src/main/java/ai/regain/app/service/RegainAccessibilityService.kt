package ai.regain.app.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import ai.regain.app.data.PreferencesManager

class RegainAccessibilityService : AccessibilityService() {

    private lateinit var prefsManager: PreferencesManager
    private lateinit var overlayManager: BlockerOverlayManager
    private val mainHandler = Handler(Looper.getMainLooper())
    private var lastInterventionTime = 0L

    override fun onCreate() {
        super.onCreate()
        prefsManager = PreferencesManager(this)
        overlayManager = BlockerOverlayManager(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val pkg = event.packageName?.toString() ?: return

        // Ignore our own package
        if (pkg == packageName) return

        val now = System.currentTimeMillis()
        if (now - lastInterventionTime < 1500L) {
            // Debounce rapid continuous node changes
            return
        }

        val isSessionActive = FocusTimerService.focusState.value.isRunning
        val isStrict = FocusTimerService.focusState.value.isStrict
        val blockedApps = prefsManager.getBlockedApps()

        val matchedRule = blockedApps.firstOrNull { it.packageName == pkg && it.isBlocked }

        // If matched app is restricted during focus session or always
        if (matchedRule != null && (isSessionActive || isStrict)) {
            if (matchedRule.blockReelsOnly && prefsManager.isBlockReelsAndShortsEnabled) {
                // Check if user is on Reels / Shorts sub-feed
                if (isReelsOrShortsActive(pkg, rootInActiveWindow)) {
                    lastInterventionTime = now
                    performGlobalAction(GLOBAL_ACTION_BACK)
                    mainHandler.post {
                        val featureName = if (pkg.contains("youtube")) "Shorts" else "Reels"
                        overlayManager.showReelsBlockedOverlay(matchedRule.appName, featureName) {
                            // Back action already triggered
                        }
                    }
                }
            } else {
                // Whole app is blocked
                lastInterventionTime = now
                performGlobalAction(GLOBAL_ACTION_HOME)
                mainHandler.post {
                    overlayManager.showAppBlockedOverlay(matchedRule.appName)
                }
            }
        }
    }

    private fun isReelsOrShortsActive(packageName: String, rootNode: AccessibilityNodeInfo?): Boolean {
        if (rootNode == null) return false

        return try {
            when {
                packageName.contains("instagram") -> {
                    // Check for reels tabs / clips
                    hasNodeMatching(rootNode) { id, text, desc ->
                        id.contains("reel") || id.contains("clips") ||
                                desc.equals("Reels", ignoreCase = true) ||
                                text.equals("Reels", ignoreCase = true)
                    }
                }
                packageName.contains("youtube") -> {
                    // Check for shorts container / player
                    hasNodeMatching(rootNode) { id, text, desc ->
                        id.contains("shorts") ||
                                desc.equals("Shorts", ignoreCase = true) ||
                                text.equals("Shorts", ignoreCase = true)
                    }
                }
                packageName.contains("snapchat") -> {
                    hasNodeMatching(rootNode) { id, text, desc ->
                        id.contains("spotlight") || desc.contains("Spotlight", ignoreCase = true)
                    }
                }
                packageName.contains("facebook") -> {
                    hasNodeMatching(rootNode) { id, text, desc ->
                        id.contains("reel") || desc.contains("Reels", ignoreCase = true)
                    }
                }
                else -> false
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun hasNodeMatching(
        node: AccessibilityNodeInfo,
        predicate: (id: String, text: String, desc: String) -> Boolean
    ): Boolean {
        val id = node.viewIdResourceName?.toString() ?: ""
        val text = node.text?.toString() ?: ""
        val desc = node.contentDescription?.toString() ?: ""

        if (predicate(id, text, desc)) return true

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            if (hasNodeMatching(child, predicate)) {
                child.recycle()
                return true
            }
            child.recycle()
        }
        return false
    }

    override fun onInterrupt() {
        overlayManager.dismissReelsOverlay()
        overlayManager.dismissAppBlockedOverlay()
    }
}
