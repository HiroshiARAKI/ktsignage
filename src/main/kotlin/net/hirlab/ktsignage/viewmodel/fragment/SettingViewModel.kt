/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.viewmodel.fragment

import javafx.beans.property.SimpleObjectProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.hirlab.ktsignage.MyApp
import net.hirlab.ktsignage.config.*
import net.hirlab.ktsignage.model.dao.CityDao
import net.hirlab.ktsignage.model.dao.PreferencesDao
import net.hirlab.ktsignage.model.data.City
import net.hirlab.ktsignage.util.Logger
import net.hirlab.ktsignage.viewmodel.ViewModel
import tornadofx.asObservable
import tornadofx.onChange

class SettingViewModel : ViewModel() {

    private val preferencesDao: PreferencesDao by di()
    private val cityDao: CityDao by di()

    var currentCountry: Country = Setting.country
        private set
    var currentCity: City = Setting.city
        private set

    val cityListProperty = SimpleObjectProperty(emptyList<City>().asObservable())
    var cityNameToId = mapOf<String, Int>()
        private set

    /**
     * Queue to limit database access.
     */
    private val settingQueue = Channel<suspend () -> Unit>(Channel.CONFLATED).also { queue ->
        MyApp.applicationScope.launch {
            queue.consumeEach { it.invoke() }
        }
    }

    private val configListener = object : Setting.Listener {
        override fun onLanguageChanged(language: Language) { saveSetting(language) }
        override fun onLocationChanged(location: Location) { saveSetting(location) }
        override fun onDateFormatChanged(dateFormat: DateFormat) { saveSetting(dateFormat) }
        override fun onOpenWeatherAPIKeyChanged(apiKey: OpenWeatherApiKey) { saveSetting(apiKey) }
        override fun onImageDirectoryChanged(directory: ImageDirectory) { saveSetting(directory) }
        override fun onImageTransitionChanged(transition: ImageTransition) { saveSetting(transition) }
        override fun onDateBackgroundThemeChanged(dateBackGround: DateBackGround) { saveSetting(dateBackGround) }
    }

    init {
        cityListProperty.value.onChange { cities ->
            cityNameToId = cities.list.toList().associateBy({ it.name }, { it.id })
        }
    }

    /**
     * Initializes this ViewModel.
     */
    fun initialize() {
        Setting.addListener(configListener)
        viewModelScope.launch(Dispatchers.IO) {
            val cities = cityDao.getCities(Setting.country.value) ?: return@launch
            withContext(Dispatchers.JavaFx) {
                cityListProperty.value = cities.asObservable()
                Logger.d("$TAG.initialize(): Done loading cities=$cities")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Setting.removeListener(configListener)
    }

    /**
     * Saves [settingItem] to preferences file.
     */
    private fun saveSetting(settingItem: SettingItem) {
        Logger.d("$TAG.saveSetting(settingItem=$settingItem)")
        MyApp.applicationScope.launch {
            when (settingItem) {
                is DateFormat -> preferencesDao.saveDateFormat(settingItem)
                is Language -> preferencesDao.saveLanguage(settingItem)
                is Location -> preferencesDao.saveLocation(settingItem)
                is OpenWeatherApiKey -> preferencesDao.saveOpenWeatherAPIKey(settingItem)
                is ImageDirectory -> preferencesDao.saveImageDirectory(settingItem)
                is ImageTransition -> preferencesDao.saveImageTransition(settingItem)
                is DateBackGround -> preferencesDao.saveDateBackground(settingItem)
            }
        }
    }

    /**
     * Sets OpenWeather API Key to [Setting] after [delayMillis] ms and will save it to preferences file.
     * The delay is time offset to avoid unnecessary changes and file accesses.
     */
    fun setOpenWeatherAPIKeyAfterDelay(key: String, delayMillis: Long = 1000) {
        runAfterDelay(delayMillis) {
            Setting.openWeatherAPIKey = key
            Logger.d("setOpenWeatherAPIKeyAfterDelay(): save API Key ($key)")
        }
    }

    /**
     * Sets image directory path to [Setting] after [delayMillis] ms and will save it to preferences file.
     * The delay is time offset to avoid unnecessary changes and file accesses.
     */
    fun setImageDirectoryAfterDelay(path: String, delayMillis: Long = 0) {
        runAfterDelay(delayMillis) {
            Setting.imageDirectory = path
            Logger.d("setImageDirectoryAfterDelay(): save image directory ($path)")
        }
    }

    fun loadCities(country: Country) = viewModelScope.launch(Dispatchers.IO) {
        val cities = cityDao.getCities(country.value) ?: return@launch
        currentCountry = country
        withContext(Dispatchers.JavaFx) {
            cityListProperty.value = cities.asObservable()
        }
    }

    fun selectCity(city: City) {
        currentCity = city
    }

    private fun runAfterDelay(delayMillis: Long = 1000, func: () -> Unit) {
        MyApp.applicationScope.launch {
            settingQueue.send {
                delay(delayMillis)
                func()
            }
        }
    }

    companion object {
        private val TAG = SettingViewModel::class.java.simpleName
    }
}