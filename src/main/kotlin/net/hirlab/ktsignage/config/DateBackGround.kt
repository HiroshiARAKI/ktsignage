/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.config

import javafx.scene.paint.Color
import net.hirlab.ktsignage.style.ColorConstants
import net.hirlab.ktsignage.util.Logger

enum class DateBackGround(override val itemName: String, override val value: Theme) : SettingItem {
    BLIGHT("Blight", Theme.BLIGHT_THEME),
    DARK("Dark", Theme.DARK_THEME),
    CUSTOM("Custom", Theme());

    override fun select() {
        Logger.d("DateBackGround.select() $this(bg=${value.backgroundColor}, text=${value.textColor})")
        Setting.dateBackgroundTheme = this
    }

    data class Theme(
        var backgroundColor: Color = ColorConstants.WHITE,
        var textColor: Color = ColorConstants.WHITE,
    ) {
        companion object {
            val BLIGHT_THEME = Theme(ColorConstants.WHITE_ALPHA_30, ColorConstants.BLACK)
            val DARK_THEME = Theme(ColorConstants.BLACK_ALPHA_30, ColorConstants.WHITE)
        }
    }

    companion object : SettingItem.SettingItemCompanion {
        override val DEFAULT = DARK
        override fun valueOfOrDefault(name: String) = try {
            valueOf(name)
        } catch (e: IllegalArgumentException) {
            DEFAULT
        }
    }
}