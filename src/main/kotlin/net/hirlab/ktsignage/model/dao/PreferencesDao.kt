/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.model.dao

import net.hirlab.ktsignage.config.*

/**
 * Data access object for preferences.
 */
interface PreferencesDao {
    /**
     * Initializes preferences data file.
     */
    suspend fun initialize()

    /**
     * Saves [dateFormat] to preferences data file.
     */
    suspend fun saveDateFormat(dateFormat: DateFormat)

    /**
     * Saves [language] to preferences data file.
     */
    suspend fun saveLanguage(language: Language)

    /**
     * Saves [location] to preferences data file.
     */
    suspend fun saveLocation(location: Location)

    /**
     * Saves [apiKey] of OpenWeather to preferences data file.
     */
    suspend fun saveOpenWeatherAPIKey(apiKey: OpenWeatherApiKey)

    /**
     * Saves image [directory] to preferences data file.
     */
    suspend fun saveImageDirectory(directory: ImageDirectory)

    /**
     * Saves image [transition] to preferences data file.
     */
    suspend fun saveImageTransition(transition: ImageTransition)

    /**
     * Saves image [dateBackGround] to preferences data file.
     */
    suspend fun saveDateBackground(dateBackGround: DateBackGround)
}