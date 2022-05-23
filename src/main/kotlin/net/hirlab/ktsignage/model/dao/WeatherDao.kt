/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.model.dao

import net.hirlab.ktsignage.model.data.Weather

/**
 * Data access object to handle weather data.
 */
interface WeatherDao {
    /**
     * Gets current weather.
     * @return [Weather] data if exists, otherwise `null`
     */
    suspend fun getCurrentWeather(): Weather?

    /**
     * Gets 5 days weathers.
     * @return List of [Weather] if exists, otherwise [emptyList]
     */
    suspend fun get5DaysWeather(): List<Weather>

    /**
     * OpenWeather API access status.
     */
    object Status {
        /** OpenWeather API access is success. */
        var isSuccess = true
            private set
        /** Message gotten when accesses OpenWeather API. */
        var message = ""
            private set

        private val listeners = mutableSetOf<Listener>()

        /** Sets a result status of OpenWeather API access. */
        @Synchronized
        internal fun setStatus(isSuccess: Boolean, message: String) {
            Status.isSuccess = isSuccess
            Status.message = message
            listeners.forEach { it.onStatusChanged(isSuccess, message) }
        }

        /** Adds [Listener]. */
        @Synchronized
        fun addListener(listener: Listener) { listeners.add(listener) }

        /** Removes [Listener]. */
        @Synchronized
        fun removeListener(listener: Listener) { listeners.remove(listener) }

        /**
         * Listener of this [Status] changes.
         */
        fun interface Listener {
            /** Called when this [Status] is changed. */
            fun onStatusChanged(isSuccess: Boolean, message: String)
        }
    }
}