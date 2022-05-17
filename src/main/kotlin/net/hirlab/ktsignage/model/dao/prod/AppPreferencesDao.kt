/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.model.dao.prod

import com.google.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.hirlab.ktsignage.ResourceAccessor
import net.hirlab.ktsignage.config.*
import net.hirlab.ktsignage.model.dao.PreferencesDao
import net.hirlab.ktsignage.util.Logger
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

/**
 * Data access object for preferences for product.
 */
@Singleton
class AppPreferencesDao : PreferencesDao {
    private val preferencesPath = ResourceAccessor.dataPath + PREFERENCES_XML
    private val preferencesFile: File
        get() = File(preferencesPath)

    private val xPath = XPathFactory.newInstance().newXPath()

    override suspend fun initialize() = withContext(Dispatchers.IO) {
        if (preferencesFile.exists()) {
            Logger.d("$TAG.initialize: found preferences (path=$preferencesPath)!.")
            val preferences = getPreferences()
            xPathScope {
                Setting.dateFormat = getNode(DATE_FORMAT, preferences)?.let {
                    DateFormat.valueOfOrDefault(it.textContent)
                } ?: DateFormat.DEFAULT
                Setting.lang = getNode(LANGUAGE, preferences)?.let {
                    Language.valueOfOrDefault(it.textContent)
                } ?: Language.DEFAULT
                Setting.location = getNode(LOCATION, preferences)?.let {
                    Location.valueOfOrDefault(it.textContent)
                } ?: Location.DEFAULT
                Setting.openWeatherAPIKey = getNode(OPEN_WEATHER_API_KEY, preferences)?.textContent ?: ""
                Setting.imageDirectory = getNode(IMAGE_DIRECTORY, preferences)?.textContent
                    ?: ResourceAccessor.imagePath
                Setting.imageTransition = getNode(IMAGE_TRANSITION, preferences)?.let {
                    ImageTransition.valueOfOrDefault(it.textContent)
                } ?: ImageTransition.DEFAULT
            }
            Logger.d("$TAG.initialize: loaded preferences (${Setting.getLog()})")
        } else {
            saveSettingsFromCache(true)
        }
    }

    override suspend fun saveDateFormat(dateFormat: DateFormat) { saveSettings(DATE_FORMAT, dateFormat.name) }

    override suspend fun saveLanguage(language: Language) { saveSettings(LANGUAGE, language.name) }

    override suspend fun saveLocation(location: Location) { saveSettings(LOCATION, location.name) }

    override suspend fun saveOpenWeatherAPIKey(apiKey: OpenWeatherApiKey) {
        saveSettings(OPEN_WEATHER_API_KEY, apiKey.value)
    }

    override suspend fun saveImageDirectory(directory: ImageDirectory) {
        saveSettings(IMAGE_DIRECTORY, directory.value)
    }

    override suspend fun saveImageTransition(transition: ImageTransition) {
        saveSettings(IMAGE_TRANSITION, transition.name)
    }

    private suspend fun saveSettings(target: String, settingItem: String) = withContext(Dispatchers.IO) {
        val preferences = if (preferencesFile.exists()) getPreferences() else newPreferences()
        xPathScope {
            updateOrAppendNode(target, preferences, settingItem)
        }
        save(preferences, false)
        Logger.d("$TAG.saveSettings(target=$target, settingItem=$settingItem)")
    }

    private suspend fun saveSettingsFromCache(needsCreate: Boolean) = withContext(Dispatchers.IO) {
        val preferences = if (needsCreate) newPreferences() else getPreferences()
        xPathScope {
            updateOrAppendNode(DATE_FORMAT, preferences, Setting.dateFormat.name)
            updateOrAppendNode(LANGUAGE, preferences, Setting.lang.name)
            updateOrAppendNode(LOCATION, preferences, Setting.location.name)
            updateOrAppendNode(OPEN_WEATHER_API_KEY, preferences, Setting.openWeatherAPIKey)
            updateOrAppendNode(IMAGE_DIRECTORY, preferences, Setting.imageDirectory)
            updateOrAppendNode(IMAGE_TRANSITION, preferences, Setting.imageTransition.name)
        }
        save(preferences, needsCreate)
    }

    private suspend fun save(preferences: Document, needsIndent: Boolean) {
        TransformerFactory.newInstance()
            .newTransformer().apply {
                if (needsIndent)
                    setOutputProperty(OutputKeys.INDENT, "yes")
                setOutputProperty(OutputKeys.METHOD, "xml")
                setOutputProperty("{https://xml.apache.org/xalan}indent-amount", "4")
            }
            .transform(DOMSource(preferences), StreamResult(preferencesFile))
        Logger.d("$TAG.saveSettings: saved to $preferencesPath!")
    }

    private fun getPreferences() = DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .parse(preferencesFile)

    private fun newPreferences(): Document = DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .domImplementation
        .createDocument("", PREFERENCES, null)

    private fun XPath.updateOrAppendNode(target: String, preferences: Document, newValue: String) {
        getNode(target, preferences)?.let {
            it.textContent = newValue
        } ?: run {
            preferences.documentElement.appendChild(
                preferences.createElement(target).also {
                    it.textContent = newValue
                }
            )
        }
    }

    private fun XPath.getNode(target: String, preferences: Document) =
        evaluate("$PREFERENCES/$target", preferences, XPathConstants.NODE) as Node?

    private fun xPathScope(f: XPath.() -> Unit) {
        xPath.run { f() }
    }

    companion object {
        private val TAG = AppPreferencesDao::class.java.simpleName

        private const val PREFERENCES_XML = "preferences.xml"
        private const val PREFERENCES = "preferences"
        private const val DATE_FORMAT = "dateformat"
        private const val LANGUAGE = "language"
        private const val LOCATION = "location"
        private const val OPEN_WEATHER_API_KEY = "open_weather_api_key"
        private const val IMAGE_DIRECTORY = "image_directory"
        private const val IMAGE_TRANSITION = "image_transition"
    }
}