/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.style

/**
 * Manager object of application styles.
 * @see StyleHelper
 * @see cssStyleBlock
 */
object Styles {
    private const val ROOT_FONT_SIZE = 18
    private const val SETTING_FRAGMENT_WIDTH = 750

    val root = cssStyleBlock {
        + fontSize(ROOT_FONT_SIZE)
        + padding(5)
    }

    val date = cssStyleBlock {
        + fontSize(ROOT_FONT_SIZE * 4)
        + padding(10)
        + textAlign(TextAlignment.CENTER)
    }

    val city = cssStyleBlock {
        + fontSize(ROOT_FONT_SIZE * 3)
    }

    val weather = cssStyleBlock {
        + fontSize(ROOT_FONT_SIZE * 4)
        + textAlign(TextAlignment.CENTER)
        + padding(10)
    }

    val minMaxTemp = cssStyleBlock {
        + fontSize(ROOT_FONT_SIZE * 1.4)
        + textAlign(TextAlignment.CENTER)
    }

    val componentBlock = cssStyleBlock {
        + padding(10, 0)
    }

    val marginLeftRight = cssStyleBlock {
        + padding(0, 10)
    }

    val settingFragmentContainer = cssStyleBlock {
        + minWidth(SETTING_FRAGMENT_WIDTH)
        + minHeight(400)
    }

    val settingFragmentLeftArea = cssStyleBlock {
        + minWidth(SETTING_FRAGMENT_WIDTH / 2)
        + fontSize(ROOT_FONT_SIZE * 1.2)
        + backgroundColor(ColorConstants.PALE_WHITE)
        + padding(5)
    }

    val settingfFragmentRightArea = cssStyleBlock {
        + minWidth(SETTING_FRAGMENT_WIDTH / 2)
        + fontSize(ROOT_FONT_SIZE)
        + backgroundColor(ColorConstants.LIGHT_GRAY)
        + padding(5)
    }

    val settingMainItem = cssStyleBlock {
        + minWidth(SETTING_FRAGMENT_WIDTH / 2)
        + padding(5)
        + border(bottom = 1, color = ColorConstants.GRAY)
    }

    val settingTitle = cssStyleBlock {
        + fontSize(24)
        + minWidth(300)
        + padding(5, 5, 3, 5)
        + border(bottom = 1)
    }

    val settingSubTitle = cssStyleBlock {
        + fontSize(18)
        + padding(5, 5, 3, 5)
    }

    val settingInput = cssStyleBlock {
        + maxWidth(SETTING_FRAGMENT_WIDTH / 2 - 50)
    }

    val settingButton = cssStyleBlock {
        + border(0, 0, 0, 0)
        + backgroundColor(ColorConstants.GREEN)
        + textColor(ColorConstants.WHITE)
        + textAlign(TextAlignment.CENTER)
        + padding(10, 20)
    }

    val textSmaller = cssStyleBlock {
        + fontSize(ROOT_FONT_SIZE * 0.7)
    }
}