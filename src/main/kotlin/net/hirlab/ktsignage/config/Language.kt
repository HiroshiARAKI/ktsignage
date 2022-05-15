/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.config

import java.util.*

/**
 * Available languages.
 */
enum class Language(override val itemName: String, val code: String, override val value: Locale) : SettingItem {
    JA("日本語", "ja", Locale.JAPANESE),
    EN("English", "en", Locale.ENGLISH);

    override fun select() { Setting.lang = this }

    companion object : SettingItem.SettingItemCompanion {
        override val DEFAULT = EN
        val map = values().associateBy({ it.code }, { it.value })

        override fun valueOfOrDefault(name: String) = try {
            valueOf(name)
        } catch (e: IllegalArgumentException) {
            DEFAULT
        }
    }
}