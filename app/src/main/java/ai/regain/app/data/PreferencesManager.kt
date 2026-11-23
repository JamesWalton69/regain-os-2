package ai.regain.app.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("regain_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_TAGS = "tags_list"
        private const val KEY_SCHEDULES = "schedules_list"
        private const val KEY_BLOCKED_APPS = "blocked_apps_list"
        private const val KEY_STRICT_MODE = "strict_mode_enabled"
        private const val KEY_BLOCK_REELS = "block_reels_shorts"
        private const val KEY_YT_STUDY_MODE = "yt_study_mode"
        private const val KEY_THEME = "current_theme"
        private const val KEY_TOTAL_STREAK = "total_streak"
        private const val KEY_TODAY_FOCUS_MINS = "today_focus_mins"
        private const val KEY_STATS = "daily_stats_json"
    }

    var isStrictModeEnabled: Boolean
        get() = prefs.getBoolean(KEY_STRICT_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_STRICT_MODE, value).apply()

    var isBlockReelsAndShortsEnabled: Boolean
        get() = prefs.getBoolean(KEY_BLOCK_REELS, true)
        set(value) = prefs.edit().putBoolean(KEY_BLOCK_REELS, value).apply()

    var isYouTubeStudyModeEnabled: Boolean
        get() = prefs.getBoolean(KEY_YT_STUDY_MODE, true)
        set(value) = prefs.edit().putBoolean(KEY_YT_STUDY_MODE, value).apply()

    var selectedTheme: RegainTheme
        get() {
            val name = prefs.getString(KEY_THEME, RegainTheme.AURORA.name) ?: RegainTheme.AURORA.name
            return try { RegainTheme.valueOf(name) } catch (e: Exception) { RegainTheme.AURORA }
        }
        set(value) = prefs.edit().putString(KEY_THEME, value.name).apply()

    var totalStreakDays: Int
        get() = prefs.getInt(KEY_TOTAL_STREAK, 7)
        set(value) = prefs.edit().putInt(KEY_TOTAL_STREAK, value).apply()

    var todayFocusMinutes: Int
        get() = prefs.getInt(KEY_TODAY_FOCUS_MINS, 190) // 3h 10m initial authentic seed
        set(value) = prefs.edit().putInt(KEY_TODAY_FOCUS_MINS, value).apply()

    fun getTags(): List<FocusTag> {
        val json = prefs.getString(KEY_TAGS, null) ?: return defaultTags()
        val type = object : TypeToken<List<FocusTag>>() {}.type
        return gson.fromJson(json, type) ?: defaultTags()
    }

    fun saveTags(tags: List<FocusTag>) {
        prefs.edit().putString(KEY_TAGS, gson.toJson(tags)).apply()
    }

    private fun defaultTags(): List<FocusTag> = listOf(
        FocusTag("1", "Study", "#22C55E", true),
        FocusTag("2", "Physics", "#EAB308", true),
        FocusTag("3", "Maths", "#3B82F6", true),
        FocusTag("4", "Coding", "#A855F7", false),
        FocusTag("5", "Reading", "#EC4899", false),
        FocusTag("6", "Deep Work", "#10B981", false)
    )

    fun getSchedules(): List<ScheduleItem> {
        val json = prefs.getString(KEY_SCHEDULES, null) ?: return defaultSchedules()
        val type = object : TypeToken<List<ScheduleItem>>() {}.type
        return gson.fromJson(json, type) ?: defaultSchedules()
    }

    fun saveSchedules(schedules: List<ScheduleItem>) {
        prefs.edit().putString(KEY_SCHEDULES, gson.toJson(schedules)).apply()
    }

    private fun defaultSchedules(): List<ScheduleItem> = listOf(
        ScheduleItem("s1", "Morning Study", 6, 0, 8, 0, listOf(1, 2, 3, 4, 5), true, "Study", "🌤️"),
        ScheduleItem("s2", "Physics", 8, 0, 10, 0, listOf(1, 2, 3, 4, 5), true, "Physics", "📚"),
        ScheduleItem("s3", "Afternoon", 14, 0, 16, 0, listOf(1, 2, 3, 4, 5), true, "Study", "☀️"),
        ScheduleItem("s4", "Maths", 18, 0, 21, 0, listOf(1, 2, 3, 4, 5), true, "Maths", "📐")
    )

    fun getBlockedApps(): List<AppBlockItem> {
        val json = prefs.getString(KEY_BLOCKED_APPS, null) ?: return defaultBlockedApps()
        val type = object : TypeToken<List<AppBlockItem>>() {}.type
        return gson.fromJson(json, type) ?: defaultBlockedApps()
    }

    fun saveBlockedApps(apps: List<AppBlockItem>) {
        prefs.edit().putString(KEY_BLOCKED_APPS, gson.toJson(apps)).apply()
    }

    private fun defaultBlockedApps(): List<AppBlockItem> = listOf(
        AppBlockItem("com.instagram.android", "Instagram", isBlocked = true, blockReelsOnly = true, dailyLimitMinutes = 30, usedTodayMinutes = 15, iconEmoji = "📸"),
        AppBlockItem("com.google.android.youtube", "YouTube", isBlocked = true, blockReelsOnly = true, dailyLimitMinutes = 45, usedTodayMinutes = 20, iconEmoji = "▶️"),
        AppBlockItem("com.snapchat.android", "Snapchat", isBlocked = true, blockReelsOnly = true, dailyLimitMinutes = 20, usedTodayMinutes = 8, iconEmoji = "👻"),
        AppBlockItem("com.facebook.katana", "Facebook", isBlocked = true, blockReelsOnly = true, dailyLimitMinutes = 20, usedTodayMinutes = 5, iconEmoji = "👥"),
        AppBlockItem("com.reddit.frontpage", "Reddit", isBlocked = true, blockReelsOnly = false, dailyLimitMinutes = 30, usedTodayMinutes = 25, iconEmoji = "🤖"),
        AppBlockItem("com.zhiliaoapp.musically", "TikTok", isBlocked = true, blockReelsOnly = false, dailyLimitMinutes = 0, usedTodayMinutes = 0, iconEmoji = "🎵"),
        AppBlockItem("com.twitter.android", "X (Twitter)", isBlocked = true, blockReelsOnly = false, dailyLimitMinutes = 30, usedTodayMinutes = 12, iconEmoji = "🐦"),
        AppBlockItem("com.netflix.mediaclient", "Netflix", isBlocked = true, blockReelsOnly = false, dailyLimitMinutes = 0, usedTodayMinutes = 0, iconEmoji = "🎬")
    )

    fun getWeeklyStats(): List<DailyUsageStat> = listOf(
        DailyUsageStat("M", 2.0f, 1.5f, false),
        DailyUsageStat("T", 2.0f, 1.8f, false),
        DailyUsageStat("W", 1.5f, 2.2f, false),
        DailyUsageStat("T", 1.5f, 1.4f, false),
        DailyUsageStat("F", 1.2f, 2.8f, false),
        DailyUsageStat("S", 1.5f, 2.0f, false),
        DailyUsageStat("S", 3.16f, 1.1f, true)
    )

    fun getTagStats(): List<TagStat> = listOf(
        TagStat("Physics", 105, "#EAB308"), // 1h 45m
        TagStat("Maths", 72, "#3B82F6"),   // 1h 12m
        TagStat("Untagged", 56, "#94A3B8") // 56m
    )
}
