/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.config

import net.hirlab.ktsignage.util.Logger
import java.util.*
import kotlin.reflect.KClass

/**
 * App settings.
 */
object Setting {
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

    var imageTransition = ImageTransition.DEFAULT
        set(value) {
            field = value
            settingMap[ImageTransition::class] = value
            synchronized(lock) { listeners.forEach { it.onImageTransitionChanged(value) } }
            Logger.d("$TAG.imageTransition is changed to $value")
        }

    val settingMap = mutableMapOf<KClass<out SettingItem>, SettingItem>(
        Language::class to lang,
        Location::class to location,
        DateFormat::class to dateFormat,
        ImageTransition::class to imageTransition,
    )

    var openWeatherAPIKey = ""
        set(value) {
            field = value
            OpenWeatherApiKey.setKey(value)
            synchronized(lock) { listeners.forEach { it.onOpenWeatherAPIKeyChanged(OpenWeatherApiKey) } }
            Logger.d("$TAG.openWeatherAPIKey is changed to $value")
        }

    var imageDirectory = ""
        set(value) {
            field = value
            ImageDirectory.setPath(value)
            synchronized(lock) { listeners.forEach { it.onImageDirectoryChanged(ImageDirectory) } }
            Logger.d("$TAG.imageDirectory is changed to $value")
        }

    private val listeners = mutableSetOf<Listener>()

    fun addListener(listener: Listener) = synchronized(lock) { listeners.add(listener) }

    fun removeListener(listener: Listener) = synchronized(lock) { listeners.remove(listener) }

    fun getLog() = "Config: DateFormat=$dateFormat, Lang=$lang, Location=$location"

    /**
     * Listener for setting changes.
     */
    interface Listener {
        fun onLanguageChanged(language: Language) { }
        fun onLocationChanged(location: Location) { }
        fun onDateFormatChanged(dateFormat: DateFormat) { }
        fun onOpenWeatherAPIKeyChanged(apiKey: OpenWeatherApiKey) { }
        fun onImageDirectoryChanged(directory: ImageDirectory) { }
        fun onImageTransitionChanged(transition: ImageTransition) { }
    }
}