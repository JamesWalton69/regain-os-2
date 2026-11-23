package ai.regain.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ai.regain.app.MainActivity
import ai.regain.app.R
import ai.regain.app.data.FocusState
import ai.regain.app.data.TimerMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FocusTimerService : Service() {

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var timerJob: Job? = null
    private var mediaPlayer: MediaPlayer? = null

    companion object {
        const val CHANNEL_ID = "regain_focus_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START = "ai.regain.action.START"
        const val ACTION_PAUSE = "ai.regain.action.PAUSE"
        const val ACTION_RESUME = "ai.regain.action.RESUME"
        const val ACTION_RESET = "ai.regain.action.RESET"

        private val _focusState = MutableStateFlow(FocusState())
        val focusState: StateFlow<FocusState> = _focusState.asStateFlow()

        var isServiceRunning = false
            private set
    }

    inner class LocalBinder : Binder() {
        fun getService(): FocusTimerService = this@FocusTimerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        isServiceRunning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val targetSecs = intent.getIntExtra("target_seconds", _focusState.value.targetSeconds)
                val mode = intent.getStringExtra("mode")?.let { TimerMode.valueOf(it) } ?: _focusState.value.mode
                val isStrict = intent.getBooleanExtra("is_strict", _focusState.value.isStrict)
                val tag = intent.getStringExtra("tag") ?: _focusState.value.activeTag
                startTimer(targetSecs, mode, isStrict, tag)
            }
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
            ACTION_RESET -> resetTimer()
        }
        return START_STICKY
    }

    fun startTimer(targetSeconds: Int, mode: TimerMode, isStrict: Boolean, tag: String) {
        _focusState.value = _focusState.value.copy(
            targetSeconds = targetSeconds,
            elapsedSeconds = 0,
            mode = mode,
            isStrict = isStrict,
            activeTag = tag,
            isRunning = true,
            isPaused = false
        )
        startForeground(NOTIFICATION_ID, buildNotification())
        startTicker()
    }

    fun pauseTimer() {
        if (_focusState.value.isStrict) return // Cannot pause in strict mode
        _focusState.value = _focusState.value.copy(isRunning = false, isPaused = true)
        timerJob?.cancel()
        pauseAmbientSound()
        updateNotification()
    }

    fun resumeTimer() {
        _focusState.value = _focusState.value.copy(isRunning = true, isPaused = false)
        startTicker()
        resumeAmbientSound()
        updateNotification()
    }

    fun resetTimer() {
        if (_focusState.value.isStrict && _focusState.value.isRunning) return // Cannot reset in strict mode
        timerJob?.cancel()
        stopAmbientSound()
        _focusState.value = _focusState.value.copy(
            elapsedSeconds = 0,
            isRunning = false,
            isPaused = false
        )
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    fun setStrictMode(enabled: Boolean) {
        _focusState.value = _focusState.value.copy(isStrict = enabled)
    }

    fun setTimerMode(mode: TimerMode) {
        if (_focusState.value.isRunning) return
        _focusState.value = _focusState.value.copy(mode = mode)
    }

    fun setActiveTag(tag: String) {
        _focusState.value = _focusState.value.copy(activeTag = tag)
    }

    fun setTargetSeconds(seconds: Int) {
        if (_focusState.value.isRunning) return
        _focusState.value = _focusState.value.copy(targetSeconds = seconds)
    }

    fun playAmbientSound(soundId: String?, volume: Float = 0.7f) {
        _focusState.value = _focusState.value.copy(activeSoundId = soundId, soundVolume = volume)
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        if (soundId == null) return

        val rawResId = when (soundId) {
            "lofi" -> R.raw.lofi
            "rain" -> R.raw.rain
            "fireplace" -> R.raw.fireplace
            "ocean" -> R.raw.ocean
            "forest" -> R.raw.forest
            "white_noise" -> R.raw.white_noise
            else -> null
        } ?: return

        try {
            mediaPlayer = MediaPlayer.create(this, rawResId).apply {
                isLooping = true
                setVolume(volume, volume)
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun pauseAmbientSound() {
        try { mediaPlayer?.pause() } catch (e: Exception) {}
    }

    private fun resumeAmbientSound() {
        try { mediaPlayer?.start() } catch (e: Exception) {}
    }

    private fun stopAmbientSound() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {}
    }

    private fun startTicker() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (_focusState.value.isRunning) {
                delay(1000L)
                val current = _focusState.value
                val newElapsed = current.elapsedSeconds + 1

                if (current.mode == TimerMode.TIMER || current.mode == TimerMode.POMODORO) {
                    if (newElapsed >= current.targetSeconds) {
                        handleTimerComplete()
                        break
                    } else {
                        _focusState.value = current.copy(elapsedSeconds = newElapsed)
                        updateNotification()
                    }
                } else {
                    // Stopwatch mode
                    _focusState.value = current.copy(elapsedSeconds = newElapsed)
                    updateNotification()
                }
            }
        }
    }

    private fun handleTimerComplete() {
        val current = _focusState.value
        if (current.mode == TimerMode.POMODORO) {
            if (!current.isBreak) {
                // Focus finished -> start break
                _focusState.value = current.copy(
                    isBreak = true,
                    elapsedSeconds = 0,
                    targetSeconds = 5 * 60, // 5 min short break
                    isRunning = true
                )
            } else {
                // Break finished -> next round
                val nextRound = current.pomodoroRound + 1
                if (nextRound > current.totalRounds) {
                    // All rounds done
                    resetTimer()
                    return
                } else {
                    _focusState.value = current.copy(
                        isBreak = false,
                        pomodoroRound = nextRound,
                        elapsedSeconds = 0,
                        targetSeconds = 25 * 60,
                        isRunning = true
                    )
                }
            }
            startTicker()
        } else {
            // Timer complete
            _focusState.value = current.copy(isRunning = false, isPaused = false, elapsedSeconds = current.targetSeconds)
            stopAmbientSound()
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
        updateNotification()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Focus Session Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows live focus countdown and ambient music controls"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val current = _focusState.value
        val remainingSecs = if (current.mode == TimerMode.STOPWATCH) current.elapsedSeconds else maxOf(0, current.targetSeconds - current.elapsedSeconds)
        val mins = remainingSecs / 60
        val secs = remainingSecs % 60
        val timeStr = String.format("%02d:%02d", mins, secs)

        val title = if (current.isBreak) "Break Time - Relax" else "${current.activeTag}: $timeStr"
        val subtitle = if (current.isStrict) "🔒 Strict Mode Active • No Escape" else if (current.isRunning) "Keep going! Rega is focusing with you" else "Session Paused"

        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(current.isRunning)
            .setOnlyAlertOnce(true)

        return builder.build()
    }

    private fun updateNotification() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, buildNotification())
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        timerJob?.cancel()
        stopAmbientSound()
    }
}
