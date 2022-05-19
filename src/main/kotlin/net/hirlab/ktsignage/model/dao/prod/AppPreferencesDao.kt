/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.model.dao.prod

import com.google.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.hirlab.ktsignage.ResourceAccessor
import net.hirlab.ktsignage.config.*
import net.hirlab.ktsignage.model.dao.PreferencesDao
import net.hirlab.ktsignage.model.source.preferencesDataStore
import net.hirlab.ktsignage.util.Logger

/**
 * Data access object for preferences for product.
 */
@Singleton
class AppPreferencesDao : PreferencesDao {
    private val dataStore = preferencesDataStore()

    override suspend fun initialize() = withContext(Dispatchers.IO) {
        Setting.dateFormat = DateFormat.valueOfOrDefault(dataStore.getOrPut(DATE_FORMAT, DateFormat.DEFAULT.name))
        Setting.lang = Language.valueOfOrDefault(dataStore.getOrPut(LANGUAGE, Language.DEFAULT.name))
        Setting.location = Location.valueOfOrDefault(dataStore.getOrPut(LOCATION, Location.DEFAULT.name))
        Setting.imageTransition = ImageTransition.valueOfOrDefault(
            dataStore.getOrPut(IMAGE_TRANSITION, ImageTransition.DEFAULT.name)
        )
        Setting.openWeatherAPIKey = dataStore.getOrPut(OPEN_WEATHER_API_KEY, "")
        Setting.imageDirectory = dataStore.getOrPut(IMAGE_DIRECTORY, ResourceAccessor.imagePath)
        Logger.d("$TAG.initialize: loaded preferences (${Setting.getLog()})")

    }

    override suspend fun saveDateFormat(dateFormat: DateFormat) {
        dataStore.set(DATE_FORMAT, dateFormat.name)
    }

    override suspend fun saveLanguage(language: Language) {
        dataStore.set(LANGUAGE, language.name)
    }

    override suspend fun saveLocation(location: Location) {
        dataStore.set(LOCATION, location.name)
    }

    override suspend fun saveOpenWeatherAPIKey(apiKey: OpenWeatherApiKey) {
        dataStore.set(OPEN_WEATHER_API_KEY, apiKey.value)
    }

    override suspend fun saveImageDirectory(directory: ImageDirectory) {
        dataStore.set(IMAGE_DIRECTORY, directory.value)
    }

    override suspend fun saveImageTransition(transition: ImageTransition) {
        dataStore.set(IMAGE_TRANSITION, transition.name)
    }

    companion object {
        private val TAG = AppPreferencesDao::class.java.simpleName

        private const val DATE_FORMAT = "dateformat"
        private const val LANGUAGE = "language"
        private const val LOCATION = "location"
        private const val OPEN_WEATHER_API_KEY = "open_weather_api_key"
        private const val IMAGE_DIRECTORY = "image_directory"
        private const val IMAGE_TRANSITION = "image_transition"
    }
}