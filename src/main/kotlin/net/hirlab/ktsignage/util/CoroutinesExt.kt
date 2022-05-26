/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.util

import javafx.scene.image.Image
import kotlinx.coroutines.*

/**
 * Runs [block] repeatedly with [timeMillis] delay while [this] coroutine scope is active.
 */
suspend fun CoroutineScope.runWithDelay(timeMillis: Long, block: suspend () -> Unit) {
    while (true) {
        if (!isActive) break
        block()
        delay(timeMillis)
    }
}

/**
 * Loads [Image] with IO thread.
 */
suspend fun image(url: String) = withContext(Dispatchers.IO) {
    Logger.d("image(url=$url)")
    Image(url)
}