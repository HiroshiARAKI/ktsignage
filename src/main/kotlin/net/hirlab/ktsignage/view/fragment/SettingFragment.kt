package net.hirlab.ktsignage.view.fragment

import javafx.beans.property.SimpleStringProperty
import javafx.event.EventTarget
import javafx.scene.Group
import net.hirlab.ktsignage.config.*
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

    private val settingPropertyMap = mapOf<KClass<out SettingItem>, MutableMap<SettingItem, SimpleStringProperty>>(
        Language::class to mutableMapOf(),
        Location::class to mutableMapOf(),
        DateFormat::class to mutableMapOf(),
    )

    private val configListener = object : Config.Listener {
        override fun onLanguageChanged(language: Language) { updateLabelOf(language) }
        override fun onLocationChanged(location: Location) { updateLabelOf(location)}
        override fun onDateFormatChanged(dateFormat: DateFormat) { updateLabelOf(dateFormat) }

        private fun updateLabelOf(setting: SettingItem) {
            settingPropertyMap[setting::class]!!.entries.forEach { (item, property) ->
                property.value = item.getLabel(property.name == setting.itemName)
            }
            viewModel.saveSetting(setting)
        }
    }

    init {
        root += container
        Config.addListener(configListener)
    }

    override fun onDelete() {
        super.onDelete()
        Config.removeListener(configListener)
    }

    private fun EventTarget.settingItem(setting: Settings) = label(setting.itemName) {
        addClass(Theme.settingItem)
        onLeftClick {
            Logger.d("${setting.name} is clicked. Launch detail items (${setting.item})")
            val settingValues = when (setting.item) {
                Language::class -> Language.values()
                Location::class -> Location.values()
                DateFormat::class -> DateFormat.values()
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
                    val isSelected = it == Config.settingMap[it::class]
                    val name = SimpleStringProperty(null, it.itemName, it.getLabel(isSelected))
                    settingPropertyMap[it::class]!![it] = name
                    label(name) {
                        addClass(Theme.settingDetail)
                        onLeftClick(1) {
                            if (it != Config.settingMap[it::class]) it.select()
                        }
                    }
                }
            }
        )
    }

    private fun SettingItem.getLabel(isSelected: Boolean) = (if (isSelected) "☑︎ " else "□ ") + itemName

    companion object {
        private const val TITLE = "Application Settings"
    }
}