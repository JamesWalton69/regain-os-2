package ai.regain.app.data

enum class TimerMode(val title: String) {
    TIMER("Timer"),
    STOPWATCH("Stopwatch"),
    POMODORO("Pomodoro")
}

data class FocusTag(
    val id: String,
    val name: String,
    val colorHex: String,
    val isDefault: Boolean = false
)

data class FocusState(
    val mode: TimerMode = TimerMode.TIMER,
    val elapsedSeconds: Int = 0,
    val targetSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isStrict: Boolean = false,
    val activeTag: String = "Study",
    val pomodoroRound: Int = 1,
    val totalRounds: Int = 4,
    val isBreak: Boolean = false,
    val activeSoundId: String? = null,
    val soundVolume: Float = 0.7f
)

data class AmbientSound(
    val id: String,
    val name: String,
    val emoji: String,
    val rawResName: String
)

data class ScheduleItem(
    val id: String,
    val title: String,
    val startHour: Int,
    val startMin: Int,
    val endHour: Int,
    val endMin: Int,
    val days: List<Int> = listOf(1, 2, 3, 4, 5), // Mon..Fri
    val isEnabled: Boolean = true,
    val tag: String = "Study",
    val iconEmoji: String = "🌤️"
)

data class Participant(
    val id: String,
    val name: String,
    val focusMinutes: Int,
    val avatarColorHex: String,
    val isYou: Boolean = false,
    val status: String = "Focusing"
)

data class StudyRoom(
    val id: String,
    val name: String,
    val activeCount: Int,
    val description: String,
    val iconEmoji: String
)

data class LeaderboardItem(
    val rank: Int,
    val name: String,
    val focusMinutes: Int,
    val avatarColorHex: String,
    val isUser: Boolean = false,
    val streakDays: Int = 5
)

data class AppBlockItem(
    val packageName: String,
    val appName: String,
    val isBlocked: Boolean = true,
    val blockReelsOnly: Boolean = false,
    val dailyLimitMinutes: Int = 0,
    val usedTodayMinutes: Int = 0,
    val iconEmoji: String = "📱"
)

data class DailyUsageStat(
    val dayLabel: String,
    val focusHours: Float,
    val screenHours: Float,
    val isCurrentDay: Boolean = false
)

data class TagStat(
    val tagName: String,
    val minutes: Int,
    val colorHex: String
)

enum class RegainTheme(val title: String, val bgStart: Long, val bgEnd: Long, val accent: Long) {
    AURORA("Aurora Night", 0xFF07120D, 0xFF0D281A, 0xFF22C55E),
    OLED("Midnight OLED", 0xFF000000, 0xFF050505, 0xFF10B981),
    FOREST("Deep Forest", 0xFF06140D, 0xFF122C1E, 0xFF4ADE80),
    SUNSET("Sunset Serenity", 0xFF120815, 0xFF281122, 0xFFF59E0B),
    NEON("Cyberpunk Neon", 0xFF070B19, 0xFF121A38, 0xFF06B6D4)
}
