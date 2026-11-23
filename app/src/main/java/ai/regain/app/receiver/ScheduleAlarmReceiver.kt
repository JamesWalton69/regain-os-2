package ai.regain.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import ai.regain.app.data.PreferencesManager
import ai.regain.app.data.TimerMode
import ai.regain.app.service.FocusTimerService
import java.util.Calendar

class ScheduleAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prefs = PreferencesManager(context)
        val schedules = prefs.getSchedules()
        val now = Calendar.getInstance()
        val currentDay = now.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon...
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMin = now.get(Calendar.MINUTE)

        val activeSchedule = schedules.firstOrNull { s ->
            s.isEnabled && s.days.contains(currentDay) &&
                    s.startHour == currentHour &&
                    Math.abs(s.startMin - currentMin) <= 2
        }

        if (activeSchedule != null && !FocusTimerService.focusState.value.isRunning) {
            val durationHours = activeSchedule.endHour - activeSchedule.startHour
            val durationMins = (activeSchedule.endMin - activeSchedule.startMin) + (durationHours * 60)
            val targetSecs = maxOf(25 * 60, durationMins * 60)

            val serviceIntent = Intent(context, FocusTimerService::class.java).apply {
                action = FocusTimerService.ACTION_START
                putExtra("target_seconds", targetSecs)
                putExtra("mode", TimerMode.TIMER.name)
                putExtra("tag", activeSchedule.tag)
                putExtra("is_strict", prefs.isStrictModeEnabled)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}
