/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.viewmodel.component

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.paint.Paint
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import net.hirlab.ktsignage.ResourceAccessor
import net.hirlab.ktsignage.config.*
import net.hirlab.ktsignage.model.dao.WeatherDao
import net.hirlab.ktsignage.model.data.Weather
import net.hirlab.ktsignage.util.Logger
import net.hirlab.ktsignage.util.SvgParser
import net.hirlab.ktsignage.util.runWithDelay
import net.hirlab.ktsignage.util.simpleBackgroundOf
import net.hirlab.ktsignage.viewmodel.ViewModel
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class DateViewModel : ViewModel() {
    private val weatherRepository: WeatherDao by di()

    val rootScaleProperty = SimpleDoubleProperty(DateViewSize.DEFAULT.value)

    val dateString = SimpleStringProperty("")
    val weatherIconSvg = SimpleObjectProperty<List<String>>()
    val tempFloat = SimpleFloatProperty(Weather.INVALID_TEMP)
    val maxTempFloat = SimpleFloatProperty(Weather.INVALID_TEMP)
    val minTempFloat = SimpleFloatProperty(Weather.INVALID_TEMP)
    val cityString = SimpleStringProperty()

    val backgroundColorProperty = SimpleObjectProperty(
        simpleBackgroundOf(Setting.dateBackgroundTheme.value.backgroundColor)
    )
    val textColorProperty = SimpleObjectProperty<Paint>(Setting.dateBackgroundTheme.value.textColor)

    private var formatter = Setting.getDateTimeFormatter()

    private val configListener = object : Setting.Listener {
        override fun onLanguageChanged(language: Language) {
            restartWeatherLoading()
            formatter = Setting.getDateTimeFormatter()
        }

        override fun onLocationChanged(location: Location) {
            restartWeatherLoading()
            formatter = Setting.getDateTimeFormatter()
        }

        override fun onDateFormatChanged(dateFormat: DateFormat) {
            formatter = Setting.getDateTimeFormatter()
        }

        override fun onOpenWeatherAPIKeyChanged(apiKey: OpenWeatherApiKey) {
            restartWeatherLoading()
        }

        override fun onImageDirectoryChanged(directory: ImageDirectory) {
            // do nothing
        }

        override fun onImageTransitionChanged(transition: ImageTransition) {
            // do nothing
        }

        override fun onDateBackgroundThemeChanged(dateBackGround: DateBackGround) {
            val theme = dateBackGround.value
            textColorProperty.value = theme.textColor
            backgroundColorProperty.value = simpleBackgroundOf(theme.backgroundColor)
        }

        override fun onDateViewSizeChanged(dateViewSize: DateViewSize) {
            rootScaleProperty.set(dateViewSize.value)
        }
    }

    /**
     * Queue to limit database access.
     */
    private val dbAccessQueue = Channel<suspend () -> Unit>(Channel.CONFLATED).also { queue ->
        viewModelScope.launch {
            queue.consumeEach { it.invoke() }
        }
    }

    private var weatherLoadingJob: Job? = null

    init {
        Setting.addListener(configListener)
        viewModelScope.launch {
            runWithDelay(TIMER_DELAY_MILLIS) {
                ZonedDateTime.now(ZoneId.systemDefault()).let {
                    dateString.value = it.format(formatter)
                }
            }
        }
        restartWeatherLoading()
    }

    override fun onDestroy() {
        super.onDestroy()
        Setting.removeListener(configListener)
    }

    private fun restartWeatherLoading() {
        weatherLoadingJob?.cancel()
        weatherLoadingJob = viewModelScope.launch {
            runWithDelay(WEATHER_UPDATE_DELAY_MILLIS) {
                dbAccessQueue.send { loadCurrentWeather() }
            }
        }
    }

    private suspend fun loadCurrentWeather() {
        val weather = weatherRepository.getCurrentWeather()
        Logger.d("Current weather is $weather")
        if (weather != null) {
            weatherIconSvg.value =
                SvgParser.getPath("${ResourceAccessor.openWeatherIconsPath}src/svg/${weather.icon}.svg")
            tempFloat.value = weather.temp.toCelsius()
            maxTempFloat.value = weather.maxTemp.toCelsius()
            minTempFloat.value = weather.minTemp.toCelsius()
            cityString.value = weather.city
        }
    }

    companion object {
        private const val TIMER_DELAY_MILLIS = 100L
        private val WEATHER_UPDATE_DELAY_MILLIS = TimeUnit.MINUTES.toMillis(5)

        private fun Float.toCelsius() = this - 273.15F
    }
}