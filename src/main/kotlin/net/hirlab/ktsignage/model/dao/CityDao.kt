/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.model.dao

import net.hirlab.ktsignage.model.data.City

/**
 * Data access object to handle [City].
 */
interface CityDao {
    /**
     * Gets list of [City] associated with the country of [countryCode] (ISO 3166-1 alpha-2).
     */
    suspend fun getCities(countryCode: String): List<City>?
}