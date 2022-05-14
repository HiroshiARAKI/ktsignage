package net.hirlab.ktsignage.viewmodel.fragment

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.hirlab.ktsignage.MyApp
import net.hirlab.ktsignage.config.*
import net.hirlab.ktsignage.model.dao.PreferencesDao
import net.hirlab.ktsignage.util.Logger
import net.hirlab.ktsignage.viewmodel.ViewModel

class SettingViewModel : ViewModel() {

    private val preferencesDao: PreferencesDao by di()

    /**
     * Queue to limit database access.
     */
    private val settingQueue = Channel<suspend () -> Unit>(Channel.CONFLATED).also { queue ->
        MyApp.applicationScope.launch {
            queue.consumeEach { it.invoke() }
        }
    }

    /**
     * Saves [settingItem] to preferences file.
     */
    fun saveSetting(settingItem: SettingItem) {
        MyApp.applicationScope.launch {
            when (settingItem) {
                is DateFormat -> preferencesDao.saveDateFormat(settingItem)
                is Language -> preferencesDao.saveLanguage(settingItem)
                is Location -> preferencesDao.saveLocation(settingItem)
                is OpenWeatherApiKey -> preferencesDao.saveOpenWeatherAPIKey(settingItem)
            }
        }
    }

    /**
     * Sets OpenWeather API Key to [Setting] after [delayMillis] ms and will save it to preferences file.
     * The delay is time offset to avoid unnecessary changes and file accesses.
     */
    fun setOpenWeatherAPIKeyAfterDelay(key: String, delayMillis: Long = 1000) {
        MyApp.applicationScope.launch {
            settingQueue.send {
                delay(delayMillis)
                Setting.openWeatherAPIKey = key
                Logger.d("setOpenWeatherAPIKeyAfterDelay(): save API Key ($key)")
            }
        }
    }
}