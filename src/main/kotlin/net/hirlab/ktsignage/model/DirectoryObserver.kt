/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.hirlab.ktsignage.util.Logger
import net.hirlab.ktsignage.util.Platform
import net.hirlab.ktsignage.util.runWithDelay
import java.nio.file.FileSystems
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds.*
import java.nio.file.WatchEvent
import java.nio.file.WatchService

/**
 * Directory observer singleton class.
 */
object DirectoryObserver {
    private val TAG = this::class.java.simpleName
    private const val UPDATE_DELAY_MILLIS = 1000L

    private val listeners = mutableSetOf<Listener>()

    private val watcherMap = mutableMapOf<String, WatchService>()

    /**
     * Starts observing the [targetPath] changes.
     */
    suspend fun start(targetPath: String) = withContext(Dispatchers.IO) {
        Logger.d("$TAG.start(): start watching $targetPath changes.")
        val adjustedPath =
            if (Platform.isWindows()) targetPath.replaceFirst("^/(.:/)", "$1").drop(1) else targetPath
        val watcher = FileSystems.getDefault().newWatchService()
        watcherMap[adjustedPath] = watcher
        val watchKey = Paths.get(adjustedPath).register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)

        runWithDelay(UPDATE_DELAY_MILLIS) {
            if (!watchKey.isValid) {
                Logger.w("$TAG.start(): watch key is invalid. (path=$adjustedPath)")
                return@runWithDelay
            }
            try {
                if (watcher.poll() != null) {
                    watchKey.pollEvents().forEach {
                        Logger.d("$it")
                        notifyChanges(it.kind(), it.context())
                    }
                }
                watchKey.reset()
            } catch (e: InterruptedException) {
                Logger.w("$TAG($adjustedPath): Error occurs. (message=${e.message})")
            }
        }
    }

    /**
     * Stops observing [targetPath] changes.
     */
    fun stop(targetPath: String) {
        watcherMap[targetPath]?.close()
    }

    @Synchronized
    fun addListener(listener: Listener) { listeners.add(listener) }

    @Synchronized
    fun removeListener(listener: Listener) { listeners.remove(listener) }

    @Synchronized
    fun notifyChanges(kind: WatchEvent.Kind<*>, context: Any) {
        listeners.forEach { it.onChanged(kind, context) }
    }

    fun interface Listener {
        fun onChanged(kind: WatchEvent.Kind<*>, context: Any)
    }
}