package ai.regain.app.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ai.regain.app.data.FocusTag
import ai.regain.app.data.PreferencesManager
import ai.regain.app.data.RegainTheme
import ai.regain.app.data.TimerMode
import ai.regain.app.service.FocusTimerService
import ai.regain.app.ui.screens.AnalyticsScreen
import ai.regain.app.ui.screens.AppBlockerScreen
import ai.regain.app.ui.screens.FocusScreen
import ai.regain.app.ui.screens.LeaderboardScreen
import ai.regain.app.ui.screens.MultiplayerScreen
import ai.regain.app.ui.screens.ScheduleScreen
import ai.regain.app.ui.screens.SettingsScreen
import ai.regain.app.ui.screens.YouTubeStudyScreen
import kotlinx.coroutines.launch

enum class Screen(val title: String, val icon: ImageVector) {
    FOCUS("Focus Timer", Icons.Default.HourglassTop),
    MULTIPLAYER("Multiplayer Live", Icons.Default.Group),
    LEADERBOARD("Leaderboard", Icons.Default.EmojiEvents),
    SCHEDULE("Daily Schedule", Icons.Default.CalendarMonth),
    ANALYTICS("Study Analytics", Icons.Default.Insights),
    APP_BLOCKER("App & Reels Blocker", Icons.Default.Block),
    YOUTUBE_STUDY("YouTube Study Mode", Icons.Default.PlayCircle),
    SETTINGS("Settings & Theme", Icons.Default.Settings)
}

@Composable
fun MainApp() {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var currentScreen by remember { mutableStateOf(Screen.FOCUS) }
    var currentTheme by remember { mutableStateOf(prefs.selectedTheme) }

    var tags by remember { mutableStateOf(prefs.getTags()) }
    var schedules by remember { mutableStateOf(prefs.getSchedules()) }
    var blockedApps by remember { mutableStateOf(prefs.getBlockedApps()) }
    var isBlockReelsEnabled by remember { mutableStateOf(prefs.isBlockReelsAndShortsEnabled) }
    var isStrictModeEnabled by remember { mutableStateOf(prefs.isStrictModeEnabled) }
    var isYouTubeStudyEnabled by remember { mutableStateOf(prefs.isYouTubeStudyModeEnabled) }

    // Service binding
    var timerService by remember { mutableStateOf<FocusTimerService?>(null) }
    val focusState by FocusTimerService.focusState.collectAsState()

    DisposableEffect(Unit) {
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as? FocusTimerService.LocalBinder
                timerService = binder?.getService()
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                timerService = null
            }
        }
        val intent = Intent(context, FocusTimerService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)

        onDispose {
            try { context.unbindService(connection) } catch (e: Exception) {}
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF0F1713),
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(20.dp)
                ) {
                    // Drawer Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 24.dp, top = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E3A28)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "⌛", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Regain",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Study Timer for Focus",
                                color = Color(0xFF86EFAC),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Navigation Items
                    Screen.values().forEach { screen ->
                        val isSelected = currentScreen == screen
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.Black else Color.White.copy(alpha = 0.8f)
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                currentScreen = screen
                                scope.launch { drawerState.close() }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = Color(0xFF22C55E),
                                unselectedContainerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Mascot Quick Tip Footer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF14241B))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "🌱 Rega says: \"Stay consistent! Today's focus is tomorrow's mastery.\"",
                            color = Color(0xFF86EFAC),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    ) {
        Crossfade(targetState = currentScreen, label = "screen_crossfade") { screen ->
            when (screen) {
                Screen.FOCUS -> {
                    FocusScreen(
                        focusState = focusState,
                        tags = tags,
                        onStartTimer = {
                            val startIntent = Intent(context, FocusTimerService::class.java).apply {
                                action = FocusTimerService.ACTION_START
                                putExtra("target_seconds", focusState.targetSeconds)
                                putExtra("mode", focusState.mode.name)
                                putExtra("is_strict", focusState.isStrict)
                                putExtra("tag", focusState.activeTag)
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(startIntent)
                            } else {
                                context.startService(startIntent)
                            }
                        },
                        onPauseTimer = { timerService?.pauseTimer() },
                        onResumeTimer = { timerService?.resumeTimer() },
                        onResetTimer = { timerService?.resetTimer() },
                        onToggleStrict = {
                            val newStrict = !focusState.isStrict
                            timerService?.setStrictMode(newStrict)
                            prefs.isStrictModeEnabled = newStrict
                            isStrictModeEnabled = newStrict
                        },
                        onSelectTag = { tag ->
                            timerService?.setActiveTag(tag)
                        },
                        onAddTag = { newTagName ->
                            val newTag = FocusTag(
                                id = (tags.size + 1).toString(),
                                name = newTagName,
                                colorHex = "#22C55E"
                            )
                            val updated = tags + newTag
                            tags = updated
                            prefs.saveTags(updated)
                        },
                        onSelectMode = { mode ->
                            timerService?.setTimerMode(mode)
                        },
                        onSelectSound = { soundId ->
                            timerService?.playAmbientSound(soundId, focusState.soundVolume)
                        },
                        onVolumeChanged = { vol ->
                            timerService?.playAmbientSound(focusState.activeSoundId, vol)
                        },
                        onOpenMenu = {
                            scope.launch { drawerState.open() }
                        }
                    )
                }

                Screen.MULTIPLAYER -> {
                    MultiplayerScreen(
                        onBack = { currentScreen = Screen.FOCUS }
                    )
                }

                Screen.LEADERBOARD -> {
                    LeaderboardScreen(
                        onBack = { currentScreen = Screen.FOCUS }
                    )
                }

                Screen.SCHEDULE -> {
                    ScheduleScreen(
                        schedules = schedules,
                        onToggleSchedule = { id ->
                            val updated = schedules.map {
                                if (it.id == id) it.copy(isEnabled = !it.isEnabled) else it
                            }
                            schedules = updated
                            prefs.saveSchedules(updated)
                        },
                        onAddSchedule = { newSchedule ->
                            val updated = schedules + newSchedule
                            schedules = updated
                            prefs.saveSchedules(updated)
                        },
                        onBack = { currentScreen = Screen.FOCUS }
                    )
                }

                Screen.ANALYTICS -> {
                    AnalyticsScreen(
                        weeklyStats = prefs.getWeeklyStats(),
                        tagStats = prefs.getTagStats(),
                        todayMinutes = prefs.todayFocusMinutes,
                        onBack = { currentScreen = Screen.FOCUS }
                    )
                }

                Screen.APP_BLOCKER -> {
                    AppBlockerScreen(
                        blockedApps = blockedApps,
                        isBlockReelsEnabled = isBlockReelsEnabled,
                        isStrictModeEnabled = isStrictModeEnabled,
                        onToggleBlockReels = { enabled ->
                            isBlockReelsEnabled = enabled
                            prefs.isBlockReelsAndShortsEnabled = enabled
                        },
                        onToggleStrictMode = { enabled ->
                            isStrictModeEnabled = enabled
                            prefs.isStrictModeEnabled = enabled
                            timerService?.setStrictMode(enabled)
                        },
                        onToggleAppBlocked = { pkg ->
                            val updated = blockedApps.map {
                                if (it.packageName == pkg) it.copy(isBlocked = !it.isBlocked) else it
                            }
                            blockedApps = updated
                            prefs.saveBlockedApps(updated)
                        },
                        onBack = { currentScreen = Screen.FOCUS }
                    )
                }

                Screen.YOUTUBE_STUDY -> {
                    YouTubeStudyScreen(
                        isStudyModeEnabled = isYouTubeStudyEnabled,
                        onToggleStudyMode = { enabled ->
                            isYouTubeStudyEnabled = enabled
                            prefs.isYouTubeStudyModeEnabled = enabled
                        },
                        onBack = { currentScreen = Screen.FOCUS }
                    )
                }

                Screen.SETTINGS -> {
                    SettingsScreen(
                        currentTheme = currentTheme,
                        onSelectTheme = { theme ->
                            currentTheme = theme
                            prefs.selectedTheme = theme
                        },
                        onBack = { currentScreen = Screen.FOCUS }
                    )
                }
            }
        }
    }
}
