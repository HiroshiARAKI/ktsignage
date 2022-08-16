/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.model.dao.prod

import com.google.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.hirlab.ktsignage.config.Setting
import net.hirlab.ktsignage.model.dao.WeatherDao
import net.hirlab.ktsignage.model.data.City
import net.hirlab.ktsignage.model.data.Weather
import net.hirlab.ktsignage.util.Logger
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject

@Singleton
class OpenWeatherAPI : WeatherDao {
    private val client = OkHttpClient()

    override suspend fun getCurrentWeather(): Weather? = withContext(Dispatchers.IO) {
        if (Setting.openWeatherAPIKey.isBlank()) return@withContext null

        val url = "$URL_CURRENT_WEATHER?id=${Setting.location.value.id}" +
                "&lang=${Setting.lang.code}&appid=${Setting.openWeatherAPIKey}"
        requestTo(url) { response ->
            if (!response.isSuccessful) {
                Logger.w("getCurrentWeather() is failed. (code=${response.code}, message=${response.message})")
                val message = if (Setting.city.id == City.INVALID_CITY_ID) {
                    "Set your location at first."
                } else {
                    "${response.code}: ${response.message}"
                }
                WeatherDao.Status.setStatus(false, message)
                // TODO: Handle error messages of OpenWeather API (#2)
                return@requestTo null
            }
            WeatherDao.Status.setStatus(true, "Valid API Key!")
            JSONObject(response.body!!.string()).toWeather()
        }
    }

    override suspend fun get5DaysWeather(): List<Weather> = withContext(Dispatchers.IO) {
        if (Setting.openWeatherAPIKey.isBlank()) return@withContext emptyList()

        val url = "$URL_5_DAYS_WEATHER?id=${Setting.location.value.id}" +
                "&lang=${Setting.lang.code}&appid=${Setting.openWeatherAPIKey}"
       requestTo(url) { response ->
           if (!response.isSuccessful) {
               Logger.w("getCurrentWeather() is failed. (code=${response.code}, message=${response.message})")
               WeatherDao.Status.setStatus(false, "Invalid API Key.")
               // TODO: Handle error messages of OpenWeather API (#2)
               return@requestTo emptyList()
           }
           WeatherDao.Status.setStatus(true, "Valid API Key!")
           JSONObject(response.body!!.string()).to5DaysWeatherList()
       }
    }

    private fun <T> requestTo(url: String, body: (Response) -> T): T {
        return client.newCall(Request.Builder().url(url).build()).execute().let { response ->
            response.use { body(it) }
        }
    }

    companion object {
        private const val URL_CURRENT_WEATHER = "https://api.openweathermap.org/data/2.5/weather"
        private const val URL_5_DAYS_WEATHER = "https://api.openweathermap.org/data/2.5/forecast"

        private fun JSONObject.toWeather(): Weather {
            val weather = getJSONArray("weather")[0] as JSONObject
            val temp = getJSONObject("main")
            val time = getLong("dt")
            val sys = getJSONObject("sys")
            val city = getStringOrNull("name") ?: ""
            return Weather(
                main = weather.getString("main"),
                description = weather.getString("description"),
                icon = weather.getString("icon"),
                country = sys.getStringOrNull("country") ?: "",
                city = city,
                weatherId = weather.getInt("id"),
                temp = temp.getFloat("temp"),
                maxTemp = temp.getFloat("temp_max"),
                minTemp = temp.getFloat("temp_min"),
                humidity = temp.getInt("humidity"),
                time = time,
            )
        }

        private fun JSONObject.to5DaysWeatherList(): List<Weather> {
            val result = mutableListOf<Weather>()
            val weatherList = getJSONArray("list") as JSONArray
            weatherList.forEach {
                result.add((it as JSONObject).toWeather())
            }
            return result
        }

        private fun JSONObject.getStringOrNull(key: String): String? =
            if (has(key)) getString(key) else null
    }
}