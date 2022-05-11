package net.hirlab.ktsignage.config

import java.util.Locale
import kotlin.reflect.KClass

/**
 * Application Settings.
 */
enum class Settings(val itemName: String, val item: KClass<out SettingItem>) {
    LANGUAGE("Language", Language::class),
    LOCATION("Location", Location::class),
    DATE_FORMAT("Date format", DateFormat::class),
}

/**
 * Interface of setting item.
 */
interface SettingItem {
    /** Displayed item name. */
    val itemName: String
    /** Value that will be saved internally as configurations. */
    val value: Any

    /**
     * Selects the item as application setting.
     * This will notify this change to [Config.Listener]s.
     */
    fun select()
}

/**
 * Available languages.
 */
enum class Language(override val itemName: String, val code: String, override val value: Locale) : SettingItem {
    JA("日本語", "ja", Locale.JAPANESE),
    EN("English", "en", Locale.ENGLISH);

    override fun select() { Config.lang = this }

    companion object {
        val DEFAULT = EN
        val map = values().associateBy({ it.code }, { it.value })
    }
}

/**
 * Available locations.
 */
enum class Location(override val itemName: String, override val value: String) : SettingItem {
    YOKOHAMA("横浜", "1848354");

    override fun select() { Config.location = this }

    companion object {
        val DEFAULT = YOKOHAMA
    }
}

/**
 * Available date formats.
 */
enum class DateFormat(override val itemName: String, override val value: String) : SettingItem {
    JIS1("yyyy-MM-dd (E) HH:mm:ss", "yyyy-MM-dd (E) HH:mm:ss"),
    JIS2("yyyy/MM/dd (E) HH:mm:ss", "yyyy/MM/dd (E) HH:mm:ss"),
    USA1("MM-dd-yyyy (E) HH:mm:ss", "MM-dd-yyyy (E) HH:mm:ss"),
    USA2("MM/dd/yyyy (E) HH:mm:ss", "MM/dd/yyyy (E) HH:mm:ss"),
    EUR1("dd-MM-yyyy (E) HH:mm:ss", "dd-MM-yyyy (E) HH:mm:ss"),
    EUR2("dd/MM/yyyy (E) HH:mm:ss", "dd/MM/yyyy (E) HH:mm:ss");

    override fun select() { Config.dateFormat = this }

    companion object {
        val DEFAULT = JIS2
    }
}