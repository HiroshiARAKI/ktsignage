/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

/**
 * Parser class for SVG.
 */
object SvgParser {
    private const val X_PATH_EXPRESSION = "//path/@d"

    /**
     * Gets (extracts) the path of SVG file.
     */
    suspend fun getPath(url: String): String = withContext(Dispatchers.IO) {
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url).let { svgFile ->
            val svgPaths =
                XPathFactory.newInstance().newXPath()
                    .compile(X_PATH_EXPRESSION).evaluate(svgFile, XPathConstants.NODESET) as NodeList
            svgPaths.item(0).nodeValue
        }
    }
}