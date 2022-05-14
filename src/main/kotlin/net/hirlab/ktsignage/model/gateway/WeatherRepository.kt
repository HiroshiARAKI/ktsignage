package net.hirlab.ktsignage.model.gateway

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.hirlab.ktsignage.config.Setting
import net.hirlab.ktsignage.model.data.Weather
import net.hirlab.ktsignage.util.Logger
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class WeatherRepository {
    private val client = OkHttpClient()

    suspend fun getCurrentWeather(): Weather? = withContext(Dispatchers.IO) {
        if (Setting.openWeatherAPIKey.isBlank()) return@withContext null

        val url = "$URL?id=${Setting.location.value}&lang=${Setting.lang.code}&appid=${Setting.openWeatherAPIKey}"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            Logger.w("getCurrentWeather() is failed. (code=${response.code}, message=${response.message})")
            Status.setStatus(false, "Invalid API Key.")
            // TODO: Handle error messages of OpenWeather API (#2)
            return@withContext null
        }
        Status.setStatus(true, "Valid API Key!")
        JSONObject(response.body!!.string()).toWeather()
    }

    /**
     * OpenWeather API access status.
     */
    object Status {
        var isSuccess = true
            private set
        var message = ""
            private set

        private val listeners = mutableSetOf<Listener>()

        fun setStatus(isSuccess: Boolean, message: String) {
            this.isSuccess = isSuccess
            this.message = message
            listeners.forEach { it.onStatusChanged(isSuccess, message) }
        }

        fun addListener(listener: Listener) { listeners.add(listener) }
        fun removeListener(listener: Listener) { listeners.remove(listener) }

        fun interface Listener {
            fun onStatusChanged(isSuccess: Boolean, message: String)
        }
    }

    companion object {
        private const val URL = "https://api.openweathermap.org/data/2.5/weather"

        private fun JSONObject.toWeather(): Weather {
            val weather = getJSONArray("weather")[0] as JSONObject
            val temp = getJSONObject("main")
            val time = getLong("dt")
            val sys = getJSONObject("sys")
            val city = getString("name")
            return Weather(
                main = weather.getString("main"),
                description = weather.getString("description"),
                icon = weather.getString("icon"),
                country = sys.getString("country"),
                city = city,
                weatherId = weather.getInt("id"),
                temp = temp.getFloat("temp"),
                maxTemp = temp.getFloat("temp_max"),
                minTemp = temp.getFloat("temp_min"),
                humidity = temp.getInt("humidity"),
                time = time,
            )
        }
    }
}