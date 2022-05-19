/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.view.fragment

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventTarget
import javafx.scene.Group
import javafx.scene.control.ColorPicker
import javafx.scene.control.Label
import javafx.util.StringConverter
import kotlinx.coroutines.launch
import net.hirlab.ktsignage.MyApp
import net.hirlab.ktsignage.config.*
import net.hirlab.ktsignage.model.dao.WeatherDao
import net.hirlab.ktsignage.style.ColorConstants
import net.hirlab.ktsignage.style.Theme
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
        addClass(Theme.settingItemContainer)
        Settings.values().forEach { settingItem(it) }
    }

    private val settingDetail = vbox {
        addClass(Theme.settingDetailContainer)
    }

    private val container = hbox {
        addClass(Theme.settingRootContainer)
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
        addClass(Theme.settingItem)
        onLeftClick {
            Logger.d("${setting.name} is clicked. Launch detail items (${setting.item})")
            val settingValues = when (setting.item) {
                Language::class -> Language.values()
                Location::class -> Location.values()
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
            updateSettingDetail(settingValues)
        }
    }

    private inline fun <reified T : SettingItem> updateSettingDetail(settingValues: List<T>) {
        val type = settingValues[0]::class
        settingDetail.replaceChildren(
            vbox {
                addClass(Theme.settingDetailContainer)
                combobox (values = settingValues) {
                    converter = getConvertor<T>(type)
                    Logger.d("type= $type")
                    value = Setting.settingMap[type] as T?
                    valueProperty().onChange { it?.select() }
                }
            }
        )
    }

    private fun openWithOpenWeatherAPISetting() {
        settingDetail.replaceChildren(
            vbox {
                addClass(Theme.settingInput)
                label("Your OpenWeather API Key:")
                textfield {
                    addClass(Theme.textSmaller)
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
                    addClass(Theme.marginTopBottom)
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
                addClass(Theme.settingInput)
                label("Image directory:")
                button {
                    addClass(Theme.textSmaller)
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
        settingDetail.replaceChildren(
            vbox {
                hbox {
                    label("Preinstall Theme: ")
                    combobox (values = DateBackGround.values().toList()) {
                        converter = getConvertor(DateBackGround::class)
                        value = Setting.settingMap[DateBackGround::class] as DateBackGround?
                        valueProperty().onChange { it?.select() }
                    }
                }
                hbox {
                    label("Background")
                    ColorPicker().apply {
                        setOnAction {
                            value
                        }
                    }
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
                    Location::class -> Location.valueOfOrDefault(string)
                    DateFormat::class -> DateFormat.valueOfOrDefault(string)
                    ImageTransition::class -> ImageTransition.valueOfOrDefault(string)
                    else -> null
                } as T?
            }
        }
    }
}