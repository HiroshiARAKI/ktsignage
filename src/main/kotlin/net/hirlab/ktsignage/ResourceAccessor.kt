/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage

import javafx.scene.text.Font
import java.io.File
import java.io.InputStream

/**
 * Accessor of resources like images, fonts ...
 */
object ResourceAccessor {
    val userHome: String = System.getProperty("user.home")

    val imagePath: String = this::class.java.classLoader.getResource("image/")?.path ?: userHome
    val fontPath: InputStream = this::class.java.classLoader.getResourceAsStream("font/OpenSans.ttf")!!
    val cityDataPath: InputStream = this::class.java.classLoader.getResourceAsStream("sys/data/city.json")!!
    val iconPath: InputStream = this::class.java.classLoader.getResourceAsStream("sys/data/icon.png")!!

    val openWeatherIconsPath: String =
        this::class.java.classLoader.getResource("sys/open_weather_icons/")!!.toExternalForm()
    val localDataPath: String
        get() = userHome + File.separator + ".ktsignage" + File.separator + "data" + File.separator

    val openSansFont: Font by lazy {  Font.loadFont(fontPath, 60.0) }
}

