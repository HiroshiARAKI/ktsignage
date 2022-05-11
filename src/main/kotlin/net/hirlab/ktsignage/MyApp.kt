package net.hirlab.ktsignage

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.javafx.JavaFx
import net.hirlab.ktsignage.style.Theme
import net.hirlab.ktsignage.view.MainView
import tornadofx.App

class MyApp: App(MainView::class, Theme::class) {

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