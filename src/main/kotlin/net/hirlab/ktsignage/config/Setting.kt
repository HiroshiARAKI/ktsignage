/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.config

import net.hirlab.ktsignage.model.data.City
import net.hirlab.ktsignage.util.Logger
import java.time.format.DateTimeFormatter
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

    var country = Country.DEFAULT

    var city = City(id=1848354, name="Yokohama-shi")

    var location = Location.from(country, city)
        set(value) {
            field = value
            settingMap[Country::class] = value.country
            country = value.country
            city = value.value
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

    var dateBackgroundTheme = DateBackGround.DARK
        set(value) {
            field = value
            settingMap[DateBackGround::class] = value
            synchronized(lock) { listeners.forEach { it.onDateBackgroundThemeChanged(value) } }
            Logger.d("$TAG.dateBackgroundTheme is changed to $value")
        }

    var dateViewSize = DateViewSize.DEFAULT
        set(value) {
            field = value
            settingMap[DateViewSize::class] = value
            synchronized(lock) { listeners.forEach { it.onDateViewSizeChanged(value) } }
            Logger.d("$TAG.dateViewSize is changed to $value")
        }

    val settingMap = mutableMapOf<KClass<out SettingItem>, SettingItem>(
        Language::class to lang,
        Country::class to country,
        DateFormat::class to dateFormat,
        ImageTransition::class to imageTransition,
        DateBackGround::class to dateBackgroundTheme,
        DateViewSize::class to dateViewSize,
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

    fun getDateTimeFormatter() = DateTimeFormatter.ofPattern(dateFormat.value, locale)

    fun getLog() = "Config: DateFormat=$dateFormat, Lang=$lang, Country=$country, city=$city."

    /**
     * Listener for setting changes.
     */
    interface Listener {
        fun onLanguageChanged(language: Language)
        fun onLocationChanged(location: Location)
        fun onDateFormatChanged(dateFormat: DateFormat)
        fun onOpenWeatherAPIKeyChanged(apiKey: OpenWeatherApiKey)
        fun onImageDirectoryChanged(directory: ImageDirectory)
        fun onImageTransitionChanged(transition: ImageTransition)
        fun onDateBackgroundThemeChanged(dateBackGround: DateBackGround)
        fun onDateViewSizeChanged(dateViewSize: DateViewSize)
    }
}