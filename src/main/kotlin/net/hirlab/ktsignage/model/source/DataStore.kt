/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.model.source

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.hirlab.ktsignage.MyApp
import net.hirlab.ktsignage.ResourceAccessor
import net.hirlab.ktsignage.util.Logger
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.util.concurrent.CountDownLatch
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

/**
 * Gets [DataStore] of preferences.xml
 */
fun preferencesDataStore() = DataStore("preferences")

/**
 * DataStore class for TornadoFX application
 */
class DataStore(private val name: String) {
    private val tag = "${this::class.java.simpleName}($name)"
    private val preferencesPath = ResourceAccessor.dataPath + name + ".xml"
    private val preferencesFile: File
        get() = File(preferencesPath)
    private val xPath = XPathFactory.newInstance().newXPath()

    private val transformer
        get() =  TransformerFactory.newInstance().newTransformer().apply {
            setOutputProperty(OutputKeys.METHOD, "xml")
            setOutputProperty(OutputKeys.ENCODING, "UTF-8")
            setOutputProperty("{https://xml.apache.org/xalan}indent-amount", "4")
        }

    private val applicationScope
        get() = MyApp.applicationScope

    private val cache = mutableMapOf<Key, Value>()

    private val countDownLatch = CountDownLatch(1)

    private val mutex = Mutex()

    init {
        applicationScope.launch(Dispatchers.IO) {
            if (preferencesFile.exists()) {
                Logger.d("$tag.init: found $name.xml.")
                initializeCache()
            } else {
                initializeDataStore()
                Logger.d("$tag.init: NOT found $name.xml.")
            }
            countDownLatch.countDown()
        }
    }

    /**
     * Gets value of [key].
     */
    fun get(key: String): String? {
        countDownLatch.await()
        return cache[key]
    }

    /**
     * Gets value of [key]. If the value of [key] is absent, returns [defaultValue] and sets [defaultValue] to datastore.
     */
    fun getOrPut(key: String, defaultValue: String): String {
        val value = get(key)
        if (value == null) {
            set(key, defaultValue)
        }
        return value ?: defaultValue
    }

    /**
     * Sets [value] to datastore.
     */
    fun set(key: String, value: String) {
        countDownLatch.await()
        cache[key] = value
        saveValueAsync(key, value)
    }

    private suspend fun initializeCache() {
        getDataStore().getDataNodeList()?.let { nodeList ->
            (0 until nodeList.length).forEach { index ->
                val node = nodeList.item(index) as Element?
                if (node?.nodeName == CHILD_TAG) {
                    node.getAttribute(ATTRIBUTE_KEY).takeIfAvailable {
                        cache[this] = node.textContent
                    }
                }
            }
        } ?: Logger.w("$tag.initializeCache: Cannot get top node list.")
        Logger.d("$tag.initializeCache: cache=$cache.")
    }

    private fun initializeDataStore() {
        val newDataStore = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .domImplementation
            .createDocument("", TOP_TAG, null)

        transformer.saveToXml(newDataStore, needsLineBrake = true)
    }

    private suspend fun getDataStore() = mutex.withLock {
        DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(preferencesFile)
    }

    private fun saveValueAsync(key: String, value: String) {
        applicationScope.launch(Dispatchers.IO) {
            mutex.withLock {
                if (!preferencesFile.exists()) initializeDataStore()
                val preferences = getDataStore()
                val exists: Boolean
                xPath.apply {
                    exists = updateNodeOrAppend(key, value, preferences)
                }
                transformer.saveToXml(preferences, needsLineBrake = exists)
            }
        }
    }

    private fun XPath.updateNodeOrAppend(key: String, value: String, preferences: Document): Boolean =
        getNode(key, preferences)?.let {
            it.textContent = value
            true
        } ?: run {
            preferences.documentElement.appendChild(
                preferences.createElement(CHILD_TAG).also {
                    it.setAttribute(ATTRIBUTE_KEY, key)
                    it.textContent = value
                }
            )
            false
        }

    private fun XPath.getNode(key: String, preferences: Document) =
        evaluate("$TOP_TAG/$CHILD_TAG/@$ATTRIBUTE_KEY[.=$key]", preferences, XPathConstants.NODE) as Node?

    private fun Document.getDataNodeList() =
        xPath.evaluate("$TOP_TAG/$CHILD_TAG", this, XPathConstants.NODESET) as NodeList?

    private fun Transformer.saveToXml(preferences: Document, needsLineBrake: Boolean = false) {
        if (needsLineBrake) setOutputProperty(OutputKeys.INDENT, "yes")
        transform(DOMSource(preferences), StreamResult(preferencesFile))
    }

    companion object {
        private const val TOP_TAG = "datastore"
        private const val CHILD_TAG = "data"
        private const val ATTRIBUTE_KEY = "key"

        private fun String.takeIfAvailable(block: String.() -> Unit) {
            if (!isNullOrBlank()) block()
        }
    }
}

private typealias Key = String
private typealias Value = String