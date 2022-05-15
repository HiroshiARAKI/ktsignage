/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.config

/**
 * Available date formats.
 */
enum class DateFormat(override val itemName: String, override val value: String) : SettingItem {
    JIS1("yyyy-MM-dd (E) HH:mm:ss", "yyyy-MM-dd (E) HH:mm:ss"),
    JIS2("yyyy/MM/dd (E) HH:mm:ss", "yyyy/MM/dd (E) HH:mm:ss"),
    USA1("MM-dd-yyyy (E) HH:mm:ss", "MM-dd-yyyy (E) HH:mm:ss"),
    USA2("MM/dd/yyyy (E) HH:mm:ss", "MM/dd/yyyy (E) HH:mm:ss"),
    EUR1("dd-MM-yyyy (E) HH:mm:ss", "dd-MM-yyyy (E) HH:mm:ss"),
    EUR2("dd/MM/yyyy (E) HH:mm:ss", "dd/MM/yyyy (E) HH:mm:ss");

    override fun select() { Setting.dateFormat = this }

    companion object : SettingItem.SettingItemCompanion{
        override val DEFAULT = JIS2

        override fun valueOfOrDefault(name: String) = try {
            valueOf(name)
        } catch (e: IllegalArgumentException) {
            DEFAULT
        }
    }
}