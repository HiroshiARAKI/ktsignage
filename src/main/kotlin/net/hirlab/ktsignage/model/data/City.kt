/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.model.data

import javafx.util.StringConverter

/**
 * City data class.
 * @property id city code used in OpenWeather API.
 * @property name city name in English.
 */
data class City(
    val id: Int,
    val name: String,
)

fun cityStringConvertor(cityNameToId: Map<String, Int>) = object : StringConverter<City>() {
    override fun toString(item: City?): String {
        item ?: return ""
        return item.name
    }

    override fun fromString(string: String?): City? {
        if (string == null) return null
        return cityNameToId[string]?.let { City(it, string) }
    }
}