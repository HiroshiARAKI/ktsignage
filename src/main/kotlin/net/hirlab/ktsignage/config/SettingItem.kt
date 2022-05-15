/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.config

import kotlin.reflect.KClass

/**
 * Application Settings.
 */
enum class Settings(val itemName: String, val item: KClass<out SettingItem>) {
    LANGUAGE("Language", Language::class),
    LOCATION("Location", Location::class),
    DATE_FORMAT("Date format", DateFormat::class),
    OPEN_WEATHER_API_KEY("OpenWeather API Key", OpenWeatherApiKey::class),
    IMAGE_DIRECTORY("Image directory", ImageDirectory::class),
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
     * This will notify this change to [Setting.Listener]s.
     */
    fun select() { }

    interface SettingItemCompanion {
        val DEFAULT: SettingItem
        fun valueOfOrDefault(name: String): SettingItem
    }
}
