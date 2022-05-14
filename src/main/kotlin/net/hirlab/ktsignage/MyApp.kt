package net.hirlab.ktsignage

import com.google.inject.Guice
import kotlin.reflect.KClass
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import net.hirlab.ktsignage.style.Theme
import net.hirlab.ktsignage.view.MainView
import tornadofx.*

class MyApp: App(MainView::class, Theme::class) {
    private val guice = Guice.createInjector(AppModule())

    init {
        FX.dicontainer = object : DIContainer {
            override fun <T : Any> getInstance(type: KClass<T>) = guice.getInstance(type.java)
        }
    }

    override fun stop() {
        super.stop()
        applicationScope.cancel()
    }

    companion object {
        /**
         * Coroutine scope depending on application lifecycle.
         */
        val applicationScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.JavaFx.immediate)
    }
}