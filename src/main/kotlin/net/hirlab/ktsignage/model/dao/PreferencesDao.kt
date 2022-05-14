package net.hirlab.ktsignage.model.dao

import net.hirlab.ktsignage.config.DateFormat
import net.hirlab.ktsignage.config.Language
import net.hirlab.ktsignage.config.Location
import net.hirlab.ktsignage.config.OpenWeatherApiKey

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
}