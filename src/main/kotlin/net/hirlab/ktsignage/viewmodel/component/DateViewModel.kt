package net.hirlab.ktsignage.viewmodel.component

import javafx.beans.property.SimpleFloatProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import net.hirlab.ktsignage.config.*
import net.hirlab.ktsignage.model.data.Weather
import net.hirlab.ktsignage.model.gateway.WeatherRepository
import net.hirlab.ktsignage.util.Logger
import net.hirlab.ktsignage.util.image
import net.hirlab.ktsignage.util.runWithDelay
import net.hirlab.ktsignage.viewmodel.ViewModel
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class DateViewModel(
    private val weatherRepository: WeatherRepository = WeatherRepository()
) : ViewModel() {
    val dateString = SimpleStringProperty("")
    val weatherIcon = SimpleObjectProperty<Image>()
    val tempFloat = SimpleFloatProperty(Weather.INVALID_TEMP)
    val maxTempFloat = SimpleFloatProperty(Weather.INVALID_TEMP)
    val minTempFloat = SimpleFloatProperty(Weather.INVALID_TEMP)
    val cityString = SimpleStringProperty()

    private var formatter = getDateTimeFormatter()

    private val configListener = object : Setting.Listener {
        override fun onLanguageChanged(language: Language) {
            viewModelScope.launch {
                dbAccessQueue.send { loadCurrentWeather() }
            }
        }

        override fun onLocationChanged(location: Location) {
            viewModelScope.launch {
                dbAccessQueue.send { loadCurrentWeather() }
            }
            formatter = getDateTimeFormatter()
        }

        override fun onDateFormatChanged(dateFormat: DateFormat) {
            formatter = getDateTimeFormatter()
        }

        override fun onOpenWeatherAPIKeyChanged(apiKey: OpenWeatherApiKey) {
            viewModelScope.launch {
                dbAccessQueue.send { loadCurrentWeather() }
            }
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

    init {
        Setting.addListener(configListener)
        viewModelScope.launch {
            runWithDelay(TIMER_DELAY_MILLIS) {
                ZonedDateTime.now(ZoneId.systemDefault()).let {
                    dateString.value = it.format(formatter)
                }
            }
        }
        viewModelScope.launch {
            runWithDelay(WEATHER_UPDATE_DELAY_MILLIS) {
                dbAccessQueue.send { loadCurrentWeather() }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Setting.removeListener(configListener)
    }

    private suspend fun loadCurrentWeather() {
        val weather = weatherRepository.getCurrentWeather()
        Logger.d("Current weather is $weather")
        if (weather != null) {
            weatherIcon.value = image("https://openweathermap.org/img/wn/${weather.icon}@2x.png")
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
        private fun getDateTimeFormatter() = DateTimeFormatter.ofPattern(Setting.dateFormat.value, Setting.locale)
    }
}