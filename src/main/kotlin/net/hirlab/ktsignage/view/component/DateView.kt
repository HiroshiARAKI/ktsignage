/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.view.component

import javafx.scene.Group
import net.hirlab.ktsignage.config.Setting
import net.hirlab.ktsignage.model.data.Weather
import net.hirlab.ktsignage.style.Styles
import net.hirlab.ktsignage.style.Theme
import net.hirlab.ktsignage.style.backgroundColor
import net.hirlab.ktsignage.style.textColor
import net.hirlab.ktsignage.view.BaseView
import net.hirlab.ktsignage.viewmodel.component.DateViewModel
import tornadofx.*


class DateView : BaseView() {
    override val root = Group()
    override val viewModel: DateViewModel by inject()

    private val temp = viewModel.tempFloat.stringBinding {
        if (it.isValidTemp()) CELSIUS_FORMAT.format(it) else LOADING
    }
    private val maxTemp = viewModel.maxTempFloat.stringBinding {
        if (it.isValidTemp()) "Max: $CELSIUS_FORMAT".format(it) else ""
    }
    private val minTemp = viewModel.minTempFloat.stringBinding {
        if (it.isValidTemp()) "min: $CELSIUS_FORMAT".format(it) else ""
    }

    init {
        val theme = Setting.dateBackgroundTheme.value
        root += vbox {
            addClass(Theme.openSansFont)
            viewModel.backgroundColorStyleProperty = styleProperty()
            style += backgroundColor(theme.backgroundColor)
            style += Styles.date
            label(viewModel.dateString) {
                viewModel.textColorStyleProperties.add(styleProperty())
                textFill = theme.textColor
            }
            hbox {
                style = Styles.weather
                label(viewModel.cityString) {
                    viewModel.textColorStyleProperties.add(styleProperty())
                    style += Styles.city + textColor(theme.textColor)
                }
                imageview(viewModel.weatherIcon) {
                    fitWidth = 90.0
                    fitHeight = 90.0
                }
                label(temp) {
                    viewModel.textColorStyleProperties.add(styleProperty())
                    style += Styles.marginLeftRight + textColor(theme.textColor)
                }
                vbox {
                    style = Styles.minMaxTemp + Styles.marginLeftRight
                    label(maxTemp).apply {
                        viewModel.textColorStyleProperties.add(styleProperty())
                        style += textColor(theme.textColor)
                    }
                    label(minTemp).apply {
                        viewModel.textColorStyleProperties.add(styleProperty())
                        style += textColor(theme.textColor)
                    }
                }
            }
        }
    }

    companion object {
        private const val LOADING = "-"
        private const val CELSIUS_FORMAT = "%.1f℃"

        private fun Number?.isValidTemp() = this != null && this != Weather.INVALID_TEMP
    }
}
