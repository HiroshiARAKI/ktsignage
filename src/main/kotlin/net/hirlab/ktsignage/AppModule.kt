/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage

import com.google.inject.AbstractModule
import net.hirlab.ktsignage.model.dao.CityDao
import net.hirlab.ktsignage.model.dao.PreferencesDao
import net.hirlab.ktsignage.model.dao.WeatherDao
import net.hirlab.ktsignage.model.dao.prod.AppPreferencesDao
import net.hirlab.ktsignage.model.dao.prod.OpenWeatherAPI
import net.hirlab.ktsignage.model.dao.prod.OpenWeatherAPICityDao

/**
 * Application module for Google Guice, dependency injection framework.
 */
class AppModule : AbstractModule() {
    override fun configure() {
        bind(PreferencesDao::class.java).to(AppPreferencesDao::class.java)
        bind(WeatherDao::class.java).to(OpenWeatherAPI::class.java)
        bind(CityDao::class.java).to(OpenWeatherAPICityDao::class.java)
    }
}