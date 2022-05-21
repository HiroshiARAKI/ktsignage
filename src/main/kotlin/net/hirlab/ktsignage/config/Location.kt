/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.config

import net.hirlab.ktsignage.model.data.City

/**
 * Available locations.
 */
data class Location(
    val country: Country,
    override val itemName: String,
    override val value: City,
) : SettingItem {
    override fun select() {
        Setting.location = this
    }

    companion object {
        val DEFAULT = from(Country.DEFAULT, City(id=1848354, name="Yokohama-shi"))

        fun from(country: Country, city: City) = Location(
            country = country,
            itemName = "${country.itemName}(city=$city)",
            value = city,
        )
    }
}