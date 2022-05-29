/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.view.component

import javafx.scene.Group
import javafx.scene.transform.Scale
import net.hirlab.ktsignage.model.data.Weather
import net.hirlab.ktsignage.style.Styles
import net.hirlab.ktsignage.style.Theme
import net.hirlab.ktsignage.util.Logger
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

    private var iconSvgPathGroup: Group? = null

    private var lastScaling: Scale? = null

    init {
        viewModel.weatherIconSvg.onChange {
            if (it == null) return@onChange
            iconSvgPathGroup?.replaceChildren {
                region {
                    it.forEach {
                        svgpath {
                            content = it
                            fillProperty().bind(viewModel.textColorProperty)
                            strokeProperty().bind(viewModel.textColorProperty)
                        }
                    }
                    scaleX = 0.2
                    scaleY = 0.2
                }
            }
        }
        val container = vbox {
            addClass(Theme.openSansFont)
            backgroundProperty().bind(viewModel.backgroundColorProperty)
            style = Styles.date
            label(viewModel.dateString) {
                textFillProperty().bind(viewModel.textColorProperty)
            }
            hbox(spacing = 10) {
                style = Styles.weather
                label(viewModel.cityString) {
                    textFillProperty().bind(viewModel.textColorProperty)
                    style = Styles.city
                }
                // the contents are set when finished loading weather information.
                iconSvgPathGroup = group { }
                label(temp) {
                    textFillProperty().bind(viewModel.textColorProperty)
                    style = Styles.marginLeftRight
                }
                vbox {
                    style = Styles.minMaxTemp + Styles.marginLeftRight
                    label(maxTemp).apply {
                        textFillProperty().bind(viewModel.textColorProperty)
                    }
                    label(minTemp).apply {
                        textFillProperty().bind(viewModel.textColorProperty)
                    }
                }
            }
        }
        root += container
        viewModel.rootScaleProperty.onChange {
            Logger.d("rootScaleProperty.onChange ${container.height}")
            if (lastScaling != null)
                root.transforms.remove(lastScaling)
            // If the container has not shown on primaryStage, container.height returns 0.0.
            // So, at the first time, uses the assumed height.
            // TODO: Fix this logic.
            val pivotY = if (container.height == 0.0) 245.0 else container.height
            lastScaling = Scale(it, it, 0.0, pivotY)
            root.transforms.add(lastScaling)
        }
    }

    companion object {
        private const val LOADING = "-"
        private const val CELSIUS_FORMAT = "%.1f℃"

        private fun Number?.isValidTemp() = this != null && this != Weather.INVALID_TEMP
    }
}
