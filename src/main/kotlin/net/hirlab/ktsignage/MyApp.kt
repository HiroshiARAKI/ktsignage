/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage

import com.google.inject.Guice
import javafx.scene.image.Image
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.javafx.JavaFx
import net.hirlab.ktsignage.style.Theme
import net.hirlab.ktsignage.view.MainView
import tornadofx.App
import tornadofx.DIContainer
import tornadofx.FX
import java.io.File
import kotlin.reflect.KClass

class MyApp: App(MainView::class, Theme::class) {
    private val guice = Guice.createInjector(AppModule())

    init {
        FX.dicontainer = object : DIContainer {
            override fun <T : Any> getInstance(type: KClass<T>) = guice.getInstance(type.java)
        }
    }

    override fun start(stage: Stage) {
        createPreferencesDirectory()
        val icon = Image(ResourceAccessor.iconPath)
        stage.icons.add(icon)
        super.start(stage)
    }

    override fun stop() {
        super.stop()
        applicationScope.cancel()
    }

    private fun createPreferencesDirectory() {
        val preferencesDir = ResourceAccessor.userHome + File.separator + ".ktsignage" + File.separator + "data"
        File(preferencesDir).mkdirs()
    }

    companion object {
        /**
         * Coroutine scope depending on application lifecycle.
         */
        val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.JavaFx.immediate)
    }
}