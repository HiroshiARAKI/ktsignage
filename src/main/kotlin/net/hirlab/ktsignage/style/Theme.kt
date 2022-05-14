package net.hirlab.ktsignage.style

import javafx.geometry.Pos
import net.hirlab.ktsignage.ResourceAccessor
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px

/**
 * Application theme.
 */
class Theme : Stylesheet() {
    init {
        // TODO: Separate StyleSheets on each views (#1)
        base {
            fontSize = FONT_SIZE
            padding = box(5.px)
        }

        date {
            font = ResourceAccessor.openSansFont
            fontSize = 3.8.rem
            padding = box(10.px)
            backgroundColor += ColorConstants.WHITE_ALPHA_30
            alignment = Pos.CENTER
        }

        weather {
            fontSize = 3.2.rem
            alignment = Pos.CENTER
            padding = box(10.px)
        }

        minMaxTemp {
            fontSize = 1.5.rem
            alignment = Pos.CENTER
        }

        city {
            fontSize = 2.0.rem
        }

        settingRootContainer {
            prefWidth = SETTING_FRAGMENT_WIDTH
            minHeight = 300.px
        }

        settingItemContainer {
            padding = box(5.px)
            prefWidth = SETTING_FRAGMENT_WIDTH / 2
            font = ResourceAccessor.openSansFont
            fontSize = 1.3.rem
            backgroundColor += ColorConstants.PALE_WHITE
        }

        settingItem {
            padding = box(5.px)
            prefWidth = SETTING_FRAGMENT_WIDTH / 2
            borderColor += box(
                top = ColorConstants.TRANSPARENT,
                right = ColorConstants.TRANSPARENT,
                left = ColorConstants.TRANSPARENT,
                bottom = ColorConstants.GRAY
            )
            hover {
                opacity = 0.7
            }
        }

        settingDetailContainer {
            padding = box(5.px)
            fontSize = 1.1.rem
            backgroundColor += ColorConstants.LIGHT_GRAY
            prefWidth = SETTING_FRAGMENT_WIDTH / 2
        }

        settingDetail {
            padding = box(5.px)
            borderColor += box(
                top = ColorConstants.TRANSPARENT,
                right = ColorConstants.TRANSPARENT,
                left = ColorConstants.TRANSPARENT,
                bottom = ColorConstants.PALE_WHITE
            )
            hover {
                opacity = 0.7
            }
        }

        settingInput {
            padding = box(5.px, 0.px)
            fontSize = 1.0.rem
        }

        marginLeftRight {
            padding = box(0.px, 10.px)
        }

        marginTopBottom {
            padding = box(10.px, 0.px)
        }

        textSmaller {
            fontSize = 0.75.rem
        }
    }

    companion object {
        val FONT_SIZE = 18.px
        val Number.rem
            get() = FONT_SIZE * this

        private val SETTING_FRAGMENT_WIDTH = 700.0.px

        val base by cssclass()
        val date by cssclass()
        val weather by cssclass()
        val minMaxTemp by cssclass()
        val city by cssclass()

        val settingRootContainer by cssclass()
        val settingItemContainer by cssclass()
        val settingItem by cssclass()
        val settingDetailContainer by cssclass()
        val settingDetail by cssclass()
        val settingInput by cssclass()

        val marginLeftRight by cssclass()
        val marginTopBottom by cssclass()

        val textSmaller by cssclass()
    }
}