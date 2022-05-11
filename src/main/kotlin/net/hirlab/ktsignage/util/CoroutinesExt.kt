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
suspend fun loadImage(url: String) = withContext(Dispatchers.IO) { Image(url) }