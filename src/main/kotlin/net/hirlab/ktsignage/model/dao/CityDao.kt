/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.model.dao

import net.hirlab.ktsignage.model.data.City

interface CityDao {
    suspend fun getCities(countryCode: String): List<City>?
}