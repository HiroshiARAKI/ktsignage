/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.config

object OpenWeatherApiKey : SettingItem {
    override val itemName = "OpenWeather API Key"
    override var value: String = ""
        private set

    fun setKey(key: String) { value = key }
}