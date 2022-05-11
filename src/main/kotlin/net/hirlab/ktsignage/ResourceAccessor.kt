package net.hirlab.ktsignage

import javafx.scene.text.Font

/**
 * Accessor of resources like images, fonts ...
 */
object ResourceAccessor {
    val imagePath: String = this::class.java.classLoader.getResource("image/")!!.path
    val fontPath: String = this::class.java.classLoader.getResource("font/")!!.toExternalForm()
    val dataPath: String = this::class.java.classLoader.getResource("sys/data/")!!.path

    val openSansFont: Font by lazy {  Font.loadFont("${fontPath}OpenSans.ttf", 60.0) }
}

