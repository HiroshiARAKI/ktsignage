/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.viewmodel.component

import com.google.common.annotations.VisibleForTesting
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.hirlab.ktsignage.config.*
import net.hirlab.ktsignage.model.data.RingBuffer
import net.hirlab.ktsignage.util.Logger
import net.hirlab.ktsignage.util.image
import net.hirlab.ktsignage.util.runWithDelay
import net.hirlab.ktsignage.viewmodel.ViewModel
import java.io.File
import javax.activation.MimetypesFileTypeMap

class BackgroundImageViewModel : ViewModel() {
    private lateinit var imageBuffer: RingBuffer<String>

    val currentImage = SimpleObjectProperty<Image>()
    private lateinit var prevImageCache: Image
    private lateinit var nextImageCache: Image

    private var imageSwitchingJob: Job? = null

    private val mutex = Mutex()

    private val settingListener = object : Setting.Listener {
        override fun onLanguageChanged(language: Language) {
            // do nothing
        }
        override fun onLocationChanged(location: Location) {
            // do nothing
        }
        override fun onDateFormatChanged(dateFormat: DateFormat) {
            // do nothing
        }
        override fun onOpenWeatherAPIKeyChanged(apiKey: OpenWeatherApiKey) {
            // do nothing
        }
        override fun onImageDirectoryChanged(directory: ImageDirectory) {
            initializeImages()
        }
        override fun onImageTransitionChanged(transition: ImageTransition) {
            Logger.d("$TAG#onImageTransitionChanged(): startImageSwitching with $transition")
            startImageSwitching()
        }
        override fun onDateBackgroundThemeChanged(dateBackGround: DateBackGround) {
            //
        }
    }

    init {
        Setting.addListener(settingListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        Setting.removeListener(settingListener)
    }

    /**
     * Initial setup.
     */
    fun initializeImages() {
        viewModelScope.launch(Dispatchers.IO) {
            mutex.withLock {
                val imagePaths = getImageFilePaths()
                if (imagePaths.isEmpty()) {
                    Logger.w("initializeImages(): ${Setting.imageDirectory} has no images.")
                    return@launch
                }
                imageBuffer = RingBuffer(imagePaths)
                Logger.d("loadImages(): load paths ... $imageBuffer")
                loadImage()
                startImageSwitching()
            }
        }
    }

    /**
     * Loads next image and changes [currentImage].
     */
    fun nextImage() {
        if (!::nextImageCache.isInitialized) return
        viewModelScope.launch {
            mutex.withLock {
                currentImage.value = nextImageCache
                imageBuffer.moveNext()
                loadImage(needsCurrentImageUpdate = false)
                startImageSwitching()
            }
        }
    }

    /**
     * Loads previous image and changes [currentImage].
     */
    fun prevImage() {
        if (!::prevImageCache.isInitialized) return
        viewModelScope.launch {
            mutex.withLock {
                currentImage.value = prevImageCache
                imageBuffer.movePrevious()
                loadImage(needsCurrentImageUpdate = false)
                startImageSwitching()
            }
        }
    }

    private fun startImageSwitching() {
        if (imageSwitchingJob?.isActive == true) imageSwitchingJob?.cancel()
        imageSwitchingJob = viewModelScope.launch(Dispatchers.Default) {
            delay(Setting.imageTransition.value)
            runWithDelay(Setting.imageTransition.value) {
                imageBuffer.moveNext()
                loadImage()
            }
        }
    }

    private suspend fun loadImage(needsCurrentImageUpdate: Boolean = true) = withContext(Dispatchers.IO) {
        val (prev, current, next) = imageBuffer.getTripleSet()

        if (needsCurrentImageUpdate)
            withContext(Dispatchers.JavaFx) { currentImage.value = image(current) }

        Logger.d("loadImages(): Done current image is ${currentImage.value.url}")
        // Update caches
        nextImageCache = image(next)
        prevImageCache = image(prev)
    }

    companion object {
        private val TAG = BackgroundImageViewModel::class.java.simpleName

        @VisibleForTesting
        fun getImageFilePaths(): List<String> {
            return File(Setting.imageDirectory).walk()
                .toList()
                .mapNotNull { if (it.isImageFile()) it.toURI().toURL().toExternalForm() else null }
        }

        private fun File.isImageFile() = isFile && isImage()
        private fun File.isImage() =
            MimetypesFileTypeMap().getContentType(this).split("/").firstOrNull()?.equals("image") == true
    }
}