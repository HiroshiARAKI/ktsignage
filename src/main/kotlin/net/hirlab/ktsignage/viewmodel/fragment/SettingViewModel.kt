package net.hirlab.ktsignage.viewmodel.fragment

import kotlinx.coroutines.launch
import net.hirlab.ktsignage.MyApp
import net.hirlab.ktsignage.config.DateFormat
import net.hirlab.ktsignage.config.Language
import net.hirlab.ktsignage.config.Location
import net.hirlab.ktsignage.config.SettingItem
import net.hirlab.ktsignage.model.dao.PreferencesDao
import net.hirlab.ktsignage.viewmodel.ViewModel

class SettingViewModel : ViewModel() {
    private val preferencesDao: PreferencesDao by di()

    /**
     * Saves [settingItem] to preferences file.
     */
    fun saveSetting(settingItem: SettingItem) {
        MyApp.applicationScope.launch {
            when (settingItem) {
                is DateFormat -> preferencesDao.saveDateFormat(settingItem)
                is Language -> preferencesDao.saveLanguage(settingItem)
                is Location -> preferencesDao.saveLocation(settingItem)
            }
        }
    }
}