/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.Controller
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

/**
 * ViewModel class for TornadoFX.
 */
abstract class ViewModel : Controller(){
    /**
     * [CoroutineScope] for TornadoFX ViewModel.
     */
    val viewModelScope: CoroutineScope =
        CloseableCoroutineScope(SupervisorJob() + Dispatchers.JavaFx.immediate)

    /**
     * Called when this ViewModel is destroyed.
     */
    open fun onDestroy() {
        if (viewModelScope.isActive) viewModelScope.cancel()
    }

    internal class CloseableCoroutineScope(context: CoroutineContext) : Closeable, CoroutineScope {
        override val coroutineContext: CoroutineContext = context
        override fun close() {
            coroutineContext.cancel()
        }
    }
}