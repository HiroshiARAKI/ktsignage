/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage

import javafx.scene.text.Font
import java.net.URL

/**
 * Accessor of resources like images, fonts ...
 */
object ResourceAccessor {
    val imagePath: String = this::class.java.classLoader.getResource("image/")!!.path
    val fontPath: String = this::class.java.classLoader.getResource("font/")!!.toExternalForm()
    val dataPath: String = this::class.java.classLoader.getResource("sys/data/")!!.path
    val iconURL: URL = this::class.java.classLoader.getResource("sys/data/icon.png")!!
    val openWeatherIconsPath: String =
        this::class.java.classLoader.getResource("sys/open_weather_icons/")!!.toExternalForm()

    val openSansFont: Font by lazy {  Font.loadFont("${fontPath}OpenSans.ttf", 60.0) }
}

