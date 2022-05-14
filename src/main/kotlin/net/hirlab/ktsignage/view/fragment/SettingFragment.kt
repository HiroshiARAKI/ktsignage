package net.hirlab.ktsignage.view.fragment

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventTarget
import javafx.scene.Group
import javafx.scene.control.Label
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

    private val settingPropertyMap = mapOf<KClass<out SettingItem>, MutableMap<SettingItem, SimpleStringProperty>>(
        Language::class to mutableMapOf(),
        Location::class to mutableMapOf(),
        DateFormat::class to mutableMapOf(),
    )

    private val configListener = object : Setting.Listener {
        override fun onLanguageChanged(language: Language) { updateLabelOf(language) }
        override fun onLocationChanged(location: Location) { updateLabelOf(location)}
        override fun onDateFormatChanged(dateFormat: DateFormat) { updateLabelOf(dateFormat) }
        override fun onOpenWeatherAPIKeyChanged(apiKey: OpenWeatherApiKey) { viewModel.saveSetting(apiKey) }
        override fun onImageDirectoryChanged(directory: ImageDirectory) { viewModel.saveSetting(directory) }

        private fun updateLabelOf(setting: SettingItem) {
            settingPropertyMap[setting::class]!!.entries.forEach { (item, property) ->
                property.value = item.getLabel(property.name == setting.itemName)
            }
            viewModel.saveSetting(setting)
        }
    }

    private val weatherRepositoryStatusListener = WeatherDao.Status.Listener { isSuccess, message ->
        MyApp.applicationScope.launch {
            weatherAPIKeyValidation.value = isSuccess
            weatherAPIKeyValidationText.value = message
        }
    }

    init {
        root += container
        Setting.addListener(configListener)
        WeatherDao.Status.addListener(weatherRepositoryStatusListener)
    }

    override fun onDelete() {
        super.onDelete()
        Setting.removeListener(configListener)
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
                OpenWeatherApiKey::class -> {
                    openWithOpenWeatherAPISetting()
                    null
                }
                ImageDirectory::class -> {
                    openImageDirectorySetting()
                    null
                }
                else -> null
            }?.toList() ?: return@onLeftClick
            updateSettingDetail(settingValues)
        }
    }

    private fun updateSettingDetail(settingValues: List<SettingItem>) {
        settingDetail.replaceChildren(
            flowpane {
                hgap = 10.0
                vgap = 10.0
                addClass(Theme.settingDetailContainer)
                settingValues.forEach {
                    val isSelected = it == Setting.settingMap[it::class]
                    val name = SimpleStringProperty(null, it.itemName, it.getLabel(isSelected))
                    settingPropertyMap[it::class]!![it] = name
                    label(name) {
                        addClass(Theme.settingDetail)
                        onLeftClick(1) {
                            if (it != Setting.settingMap[it::class]) it.select()
                        }
                    }
                }
            }
        )
    }

    private fun openWithOpenWeatherAPISetting() {
        settingDetail.replaceChildren(
            vbox {
                addClass(Theme.settingWeatherStatus)
                label("Your OpenWeather API Key:")
                textfield {
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
                addClass(Theme.settingWeatherStatus)
                label("Image directory:")
                button {
                    text = Setting.imageDirectory
                    action {
                        val dir = chooseDirectory("Select Image Directory")
                        if (dir != null)
                            text = dir.path
                    }
                    textProperty().onChange {
                        if (!it.isNullOrBlank())
                            viewModel.setImageDirectoryAfterDelay(it)
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

    private fun SettingItem.getLabel(isSelected: Boolean) = (if (isSelected) "☑︎ " else "□ ") + itemName

    companion object {
        private const val TITLE = "Application Settings"
    }
}