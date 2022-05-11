package net.hirlab.ktsignage.config

import net.hirlab.ktsignage.util.Logger
import java.util.*
import kotlin.reflect.KClass

/**
 * App configurations.
 */
object Config {
    private val lock = Any()
    private val TAG = this::class.java.simpleName

    var dateFormat = DateFormat.DEFAULT
        set(value) {
            field = value
            settingMap[DateFormat::class] = value
            synchronized(lock) { listeners.forEach { it.onDateFormatChanged(value) } }
            Logger.d("$TAG.dateFormat is changed to $value")
        }

    var lang = Language.DEFAULT
        set(value) {
            field = value
            settingMap[Language::class] = value
            locale = Language.map[lang.code] ?: Locale.getDefault()
            synchronized(lock) { listeners.forEach { it.onLanguageChanged(value) } }
            Logger.d("$TAG.lang is changed to $value")
        }

    var locale: Locale = Language.map[lang.code] ?: Locale.getDefault()

    var location = Location.DEFAULT
        set(value) {
            field = value
            settingMap[Location::class] = value
            synchronized(lock) { listeners.forEach { it.onLocationChanged(value) } }
            Logger.d("$TAG.location is changed to $value")
        }

    val settingMap = mutableMapOf<KClass<out SettingItem>, SettingItem>(
        Language::class to lang,
        Location::class to location,
        DateFormat::class to dateFormat
    )

    var WEATHER_API_KEY = "057aff0074ef798e0be84fc10360aacb"

    private val listeners = mutableSetOf<Listener>()

    fun addListener(listener: Listener) = synchronized(lock) { listeners.add(listener) }

    fun removeListener(listener: Listener) = synchronized(lock) { listeners.remove(listener) }

    /**
     * Listener for setting changes.
     */
    interface Listener {
        fun onLanguageChanged(language: Language)
        fun onLocationChanged(location: Location)
        fun onDateFormatChanged(dateFormat: DateFormat)
    }
}