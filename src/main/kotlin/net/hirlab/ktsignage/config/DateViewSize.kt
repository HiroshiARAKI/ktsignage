/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.config

/**
 * Available DateView size.
 */
enum class DateViewSize(override val itemName: String, override val value: Double) : SettingItem {
    SMALL("Small", 0.8),
    MEDIUM("Medium", 1.0),
    LARGE("Large", 1.2);

    override fun select() {
        Setting.dateViewSize = this
    }

    companion object : SettingItem.SettingItemCompanion {
        override val DEFAULT = MEDIUM

        override fun valueOfOrDefault(name: String) = try {
            valueOf(name)
        } catch (e: IllegalArgumentException) {
            DEFAULT
        }
    }
}