package ai.regain.app.service

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import ai.regain.app.MainActivity
import ai.regain.app.R

class BlockerOverlayManager(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var reelsOverlayView: View? = null
    private var appBlockOverlayView: View? = null

    fun showReelsBlockedOverlay(appName: String, featureName: String = "Reels", onDismiss: () -> Unit) {
        if (reelsOverlayView != null) return

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
            y = 0
        }

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.overlay_reels_blocked, null)

        view.findViewById<TextView>(R.id.tv_reels_title)?.text = "$appName $featureName is blocked!"
        view.findViewById<View>(R.id.btn_close_reels)?.setOnClickListener {
            dismissReelsOverlay()
            onDismiss()
        }
        view.findViewById<View>(R.id.btn_reels_settings)?.setOnClickListener {
            dismissReelsOverlay()
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(intent)
        }

        try {
            windowManager.addView(view, layoutParams)
            reelsOverlayView = view
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dismissReelsOverlay() {
        reelsOverlayView?.let {
            try {
                windowManager.removeView(it)
            } catch (e: Exception) {}
            reelsOverlayView = null
        }
    }

    fun showAppBlockedOverlay(appName: String) {
        if (appBlockOverlayView != null) return

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.overlay_app_blocked, null)

        view.findViewById<TextView>(R.id.tv_blocked_app_name)?.text = "$appName is Blocked"
        view.findViewById<Button>(R.id.btn_return_focus)?.setOnClickListener {
            dismissAppBlockedOverlay()
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(intent)
        }

        try {
            windowManager.addView(view, layoutParams)
            appBlockOverlayView = view
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dismissAppBlockedOverlay() {
        appBlockOverlayView?.let {
            try {
                windowManager.removeView(it)
            } catch (e: Exception) {}
            appBlockOverlayView = null
        }
    }
}
