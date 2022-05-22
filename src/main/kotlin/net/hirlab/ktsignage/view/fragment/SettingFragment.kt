/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.view.fragment

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventTarget
import javafx.scene.Group
import javafx.scene.control.ColorPicker
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.util.StringConverter
import kotlinx.coroutines.launch
import net.hirlab.ktsignage.MyApp
import net.hirlab.ktsignage.config.*
import net.hirlab.ktsignage.model.dao.WeatherDao
import net.hirlab.ktsignage.model.data.City
import net.hirlab.ktsignage.model.data.cityStringConvertor
import net.hirlab.ktsignage.style.ColorConstants
import net.hirlab.ktsignage.style.Styles
import net.hirlab.ktsignage.style.Theme
import net.hirlab.ktsignage.style.hoverOpacity
import net.hirlab.ktsignage.util.Logger
import net.hirlab.ktsignage.viewmodel.fragment.SettingViewModel
import tornadofx.*
import kotlin.reflect.KClass

/**
 * Fragment for setting changes.
 */
class SettingFragment : Fragment(TITLE) {
    override val root = Group()

    private val viewModel: SettingViewModel by inject()

    private val settingList = vbox {
        style = Styles.settingFragmentLeftArea
        Settings.values().forEach { settingItem(it) }
    }

    private val settingDetail = vbox {
        style = Styles.settingfFragmentRightArea
    }

    private val container = hbox {
        addClass(Theme.openSansFont)
        style += Styles.settingFragmentContainer
        add(settingList)
        add(settingDetail)
    }

    private val weatherAPIKeyValidation = SimpleBooleanProperty(WeatherDao.Status.isSuccess)
    private val weatherAPIKeyValidationText = SimpleStringProperty(WeatherDao.Status.message)

    private val weatherRepositoryStatusListener = WeatherDao.Status.Listener { isSuccess, message ->
        MyApp.applicationScope.launch {
            weatherAPIKeyValidation.value = isSuccess
            weatherAPIKeyValidationText.value = message
        }
    }

    init {
        root += container
        WeatherDao.Status.addListener(weatherRepositoryStatusListener)
        viewModel.initialize()
    }

    override fun onDelete() {
        super.onDelete()
        WeatherDao.Status.removeListener(weatherRepositoryStatusListener)
    }

    private fun EventTarget.settingItem(setting: Settings) = label(setting.itemName) {
        style = Styles.settingMainItem
        hoverOpacity(0.7)
        onLeftClick {
            Logger.d("${setting.name} is clicked. Launch detail items (${setting.item})")
            val settingValues = when (setting.item) {
                Language::class -> Language.values()
                Country::class -> Country.values()
                DateFormat::class -> DateFormat.values()
                ImageTransition::class -> ImageTransition.values()
                OpenWeatherApiKey::class -> {
                    openWithOpenWeatherAPISetting()
                    null
                }
                ImageDirectory::class -> {
                    openImageDirectorySetting()
                    null
                }
                DateBackGround::class -> {
                    openDateBackgroundSetting()
                    null
                }
                else -> null
            }?.toList() ?: return@onLeftClick
            updateSettingDetail(setting.itemName, settingValues)
        }
    }

    private inline fun <reified T : SettingItem> updateSettingDetail(title: String, settingValues: List<T>) {
        val type = settingValues[0]::class
        val parentDetailVBox = vbox {
            vbox {
                spacing = 5.0
                style = Styles.componentBlock
                label(title) { style = Styles.settingTitle }
                if (type == Country::class) {
                    label("Country") { style = Styles.settingSubTitle }
                }
                combobox (values = settingValues) {
                    style = Styles.settingInput
                    converter = getConvertor<T>(type)
                    Logger.d("type= $type")
                    value = Setting.settingMap[type] as T?
                    valueProperty().onChange {
                        if (it is Country) {
                            viewModel.loadCities(it)
                        } else {
                            it?.select()
                        }
                    }
                }
            }

            if (type == Country::class) {
                vbox {
                    spacing = 5.0
                    style = Styles.componentBlock
                    label("City") { style = Styles.settingSubTitle }
                    combobox <City> {
                        style = Styles.settingInput
                        items = viewModel.cityListProperty.value
                        value = Setting.city
                        converter = cityStringConvertor(viewModel.cityNameToId)
                        viewModel.cityListProperty.onChange {
                            value = it?.get(0)
                            items = it
                            converter = cityStringConvertor(viewModel.cityNameToId)
                        }
                        valueProperty().onChange {
                            if (it != null) viewModel.selectCity(it)
                        }
                    }
                }
                button("Save") {
                    style = Styles.settingButton
                    hoverOpacity(0.7)
                    action {
                        Setting.location = Location.from(viewModel.currentCountry, viewModel.currentCity)
                    }
                }
            }
        }
        settingDetail.replaceChildren(parentDetailVBox)
    }

    private fun openWithOpenWeatherAPISetting() {
        settingDetail.replaceChildren(
            vbox {
                style = Styles.settingInput
                label("Your OpenWeather API Key") { style = Styles.settingSubTitle }
                textfield {
                    style = Styles.textSmaller
                    text = Setting.openWeatherAPIKey
                    textProperty().onChange {
                        if (!it.isNullOrBlank())
                            viewModel.setOpenWeatherAPIKeyAfterDelay(it)
                    }
                }
                label(weatherAPIKeyValidationText) {
                    switchTextColorOfValidation(weatherAPIKeyValidation.value)
                    weatherAPIKeyValidation.onChange { isSuccess ->
                        switchTextColorOfValidation(isSuccess)
                    }
                }
                textflow {
                    style = Styles.componentBlock
                    text("See")
                    hyperlink("this link") {
                        action { hostServices.showDocument("https://openweathermap.org/") }
                    }
                    text("about OpenWeather API.")
                }
            }
        )
    }

    private fun openImageDirectorySetting() {
        settingDetail.replaceChildren(
            vbox {
                style = Styles.settingInput
                label("Image directory") { style = Styles.settingSubTitle }
                button {
                    style = Styles.textSmaller
                    text = Setting.imageDirectory
                    action {
                        chooseDirectory(
                            owner = primaryStage.owner
                        )?.let {
                            text = it.path
                        }
                    }
                    textProperty().onChange {
                        if (!it.isNullOrBlank())
                            viewModel.setImageDirectoryAfterDelay(it)
                    }
                }
            }
        )
    }

    private fun openDateBackgroundSetting() {
        val currentTheme = Setting.dateBackgroundTheme.value
        // Holds elements to change each values.
        var combobox: ComboBox<DateBackGround>? = null
        val bgColorPicker = ColorPicker(currentTheme.backgroundColor)
        val textColorPicker = ColorPicker(currentTheme.textColor)

        // When color pickers are used, changes the selected value of combo box to CUSTOM.
        bgColorPicker.apply {
            setOnAction {
                combobox?.value = DateBackGround.CUSTOM
                combobox?.value?.apply {
                    value.backgroundColor = bgColorPicker.value
                    value.textColor = textColorPicker.value
                }?.select()
            }
        }
        textColorPicker.apply {
            setOnAction {
                combobox?.value = DateBackGround.CUSTOM
                combobox?.value?.apply {
                    value.backgroundColor = bgColorPicker.value
                    value.textColor = textColorPicker.value
                }?.select()
            }
        }

        settingDetail.replaceChildren(
            vbox {
                spacing = 15.0
                vbox {
                    label("Preinstalled Theme: ")
                    combobox = combobox (values = DateBackGround.values().toList()) {
                        converter = getConvertor(DateBackGround::class)
                        value = Setting.settingMap[DateBackGround::class] as DateBackGround?
                        valueProperty().onChange {
                            if (it == null) return@onChange
                            if (it == DateBackGround.CUSTOM) {
                                it.apply {
                                    value.backgroundColor = bgColorPicker.value
                                    value.textColor = textColorPicker.value
                                }
                            } else {
                                bgColorPicker.value = it.value.backgroundColor
                                textColorPicker.value = it.value.textColor
                                it.select()
                            }
                        }
                    }
                }
                vbox {
                    label("Background Color")
                    add(bgColorPicker)
                }
                vbox {
                    label("Text Color")
                    add(textColorPicker)
                }
            }
        )
    }

    private fun Label.switchTextColorOfValidation(isSuccess: Boolean) {
        textFill = if (isSuccess) {
            ColorConstants.SUCCESS
        } else {
            ColorConstants.ERROR
        }
    }

    companion object {
        private const val TITLE = "Application Settings"

        /**
         * Gets original [StringConverter].
         */
        private fun <T : SettingItem> getConvertor(type: KClass<*>) = object : StringConverter<T>() {
            override fun toString(item: T?) = item?.itemName ?: ""

            @Suppress("unchecked_cast")
            override fun fromString(string: String?): T? {
                if (string == null) return null
                return when (type) {
                    Language::class -> Language.valueOfOrDefault(string)
                    DateFormat::class -> DateFormat.valueOfOrDefault(string)
                    ImageTransition::class -> ImageTransition.valueOfOrDefault(string)
                    else -> null
                } as T?
            }
        }
    }
}