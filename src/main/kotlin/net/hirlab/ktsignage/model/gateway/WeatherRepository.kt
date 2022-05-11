package net.hirlab.ktsignage.model.gateway

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.hirlab.ktsignage.config.Config
import net.hirlab.ktsignage.model.data.Weather
import net.hirlab.ktsignage.util.Logger
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class WeatherRepository {
    private val client = OkHttpClient()

    suspend fun getCurrentWeather(): Weather? = withContext(Dispatchers.IO) {
        val url = "$URL?id=${Config.location.value}&lang=${Config.lang.code}&appid=${Config.WEATHER_API_KEY}"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            Logger.w("getCurrentWeather() is failed. (code=${response.code}, message=${response.message})")
            return@withContext null
        }
        JSONObject(response.body!!.string()).toWeather()
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