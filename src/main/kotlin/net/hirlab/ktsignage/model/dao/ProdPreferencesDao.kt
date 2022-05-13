package net.hirlab.ktsignage.model.dao

import com.google.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.hirlab.ktsignage.ResourceAccessor
import net.hirlab.ktsignage.config.*
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
class ProdPreferencesDao : PreferencesDao {
    private val preferencesPath = ResourceAccessor.dataPath + PREFERENCES_XML
    private val preferencesFile: File
        get() = File(preferencesPath)

    private val xPath = XPathFactory.newInstance().newXPath()

    override suspend fun initialize() = withContext(Dispatchers.IO) {
        if (preferencesFile.exists()) {
            Logger.d("$TAG.initialize: found preferences (path=$preferencesPath)!.")
            val preferences = getPreferences()
            XPathFactory.newInstance().newXPath().let { xPath ->
                Config.dateFormat = xPath.getNode(DATE_FORMAT, preferences)?.let {
                    DateFormat.valueOfOrDefault(it.textContent)
                } ?: DateFormat.DEFAULT

                Config.lang = xPath.getNode(LANGUAGE, preferences)?.let {
                    Language.valueOfOrDefault(it.textContent)
                } ?: Language.DEFAULT

                Config.location = xPath.getNode(LOCATION, preferences)?.let {
                    Location.valueOfOrDefault(it.textContent)
                } ?: Location.DEFAULT
            }
            Logger.d("$TAG.initialize: loaded preferences (${Config.getLog()})")
        } else {
            saveSettingsFromCache(true)
        }
    }

    override suspend fun saveDateFormat(dateFormat: DateFormat) { saveSettings(DATE_FORMAT, dateFormat.name) }
    override suspend fun saveLanguage(language: Language) { saveSettings(LANGUAGE, language.name) }
    override suspend fun saveLocation(location: Location) { saveSettings(LOCATION, location.name) }

    private suspend fun saveSettings(target: String, settingItem: String) = withContext(Dispatchers.IO) {
        val preferences = if (preferencesFile.exists()) getPreferences() else newPreferences()
        xPathScope {
            updateOrAppendNode(target, preferences, settingItem)
        }
        save(preferences)
    }

    private suspend fun saveSettingsFromCache(needsCreate: Boolean) = withContext(Dispatchers.IO) {
        val preferences = if (needsCreate) newPreferences() else getPreferences()
        xPathScope {
            updateOrAppendNode(DATE_FORMAT, preferences, Config.dateFormat.name)
            updateOrAppendNode(LANGUAGE, preferences, Config.lang.name)
            updateOrAppendNode(LOCATION, preferences, Config.location.name)
        }
        save(preferences)
    }

    private suspend fun save(preferences: Document) {
        TransformerFactory.newInstance()
            .newTransformer().apply {
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
        private val TAG = ProdPreferencesDao::class.java.simpleName

        private const val PREFERENCES_XML = "preferences.xml"
        private const val PREFERENCES = "preferences"
        private const val DATE_FORMAT = "dateformat"
        private const val LANGUAGE = "language"
        private const val LOCATION = "location"
    }
}