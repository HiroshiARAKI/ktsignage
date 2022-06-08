/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.model.dao.prod

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.hirlab.ktsignage.MyApp
import net.hirlab.ktsignage.ResourceAccessor
import net.hirlab.ktsignage.model.dao.CityDao
import net.hirlab.ktsignage.model.data.City
import net.hirlab.ktsignage.util.Logger
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Data access object of [City] that can be used on OpenWeather API.
 */
class OpenWeatherAPICityDao : CityDao {
    val applicationScope
        get() = MyApp.applicationScope

    private lateinit var  cityJson: JSONObject

    private val initializingJob: Job = applicationScope.launch(Dispatchers.IO) {
        val inputStream = ResourceAccessor.cityDataPath
        val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
        StringBuilder().let { builder ->
            reader.readLines().forEach { builder.append(it) }
            cityJson = JSONObject(builder.toString())
        }
    }

    override suspend fun getCities(countryCode: String): List<City>? {
        if (!initializingJob.isCompleted)
            Logger.d("$TAG.getCities(): Wait a second... now initializing")

        initializingJob.join()
        val citiesJson = cityJson.getJSONArray(countryCode) ?: run {
            Logger.w("$TAG.getCities(): $countryCode is not supported.")
            return null
        }
        val cities = mutableListOf<City>()
        citiesJson.forEach {
            val id = (it as JSONObject).getInt(ID)
            val name = it.getString(NAME)
            cities.add(City(id, name))
        }
        Logger.d("$TAG.getCities(): Gets cities of $countryCode > ${cities[0]}...")
        return cities
    }

    companion object {
        private val TAG = OpenWeatherAPICityDao::class.java.simpleName
        private const val ID = "id"
        private const val NAME = "name"
    }
}