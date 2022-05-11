package net.hirlab.ktsignage.view.component

import javafx.scene.Group
import net.hirlab.ktsignage.model.data.Weather
import net.hirlab.ktsignage.style.Theme
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
    private val city = viewModel.cityString.stringBinding { it ?: "" }

    init {
        root += vbox {
            addClass(Theme.date)
            label(viewModel.dateString)
            hbox {
                addClass(Theme.weather)
                label(city) {
                    addClass(Theme.city)
                }
                imageview(viewModel.weatherIcon) {
                    fitWidth = 90.0
                    fitHeight = 90.0
                }
                label(temp) {
                    addClass(Theme.marginLeftRight)
                }
                vbox {
                    addClass(Theme.minMaxTemp)
                    addClass(Theme.marginLeftRight)
                    label(maxTemp)
                    label(minTemp)
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
