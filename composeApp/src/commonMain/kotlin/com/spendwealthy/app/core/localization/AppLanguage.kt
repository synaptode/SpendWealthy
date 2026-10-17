package com.spendwealthy.app.core.localization

/**
 * Supported application languages across SpendWealthy.
 */
enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val flagEmoji: String,
    val region: String
) {
    INDONESIAN("id", "Indonesian", "Bahasa Indonesia", "🇮🇩", "Indonesia"),
    ENGLISH("en", "English", "English", "🇺🇸", "Global / US"),
    MALAY("ms", "Malay", "Bahasa Melayu", "🇲🇾", "Malaysia"),
    THAI("th", "Thai", "ไทย", "🇹🇭", "ประเทศไทย"),
    CHINESE("zh", "Mandarin", "中文 (简体)", "🇨🇳", "中国 / 华语"),
    JAPANESE("ja", "Japanese", "日本語", "🇯🇵", "日本"),
    VIETNAMESE("vi", "Vietnamese", "Tiếng Việt", "🇻🇳", "Việt Nam"),
    ARABIC("ar", "Arabic", "العربية", "🇸🇦", "العالم العربي");

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: INDONESIAN
        }
    }
}
