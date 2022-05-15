/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.config

/**
 * Available locations.
 */
enum class Location(override val itemName: String, override val value: String) : SettingItem {
    YOKOHAMA("横浜", "1848354");

    override fun select() { Setting.location = this }

    companion object : SettingItem.SettingItemCompanion{
        override val DEFAULT = YOKOHAMA

        override fun valueOfOrDefault(name: String) = try {
            valueOf(name)
        } catch (e: IllegalArgumentException) {
            DEFAULT
        }
    }
}