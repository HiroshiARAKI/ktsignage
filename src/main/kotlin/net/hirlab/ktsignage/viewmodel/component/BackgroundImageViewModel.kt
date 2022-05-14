package net.hirlab.ktsignage.viewmodel.component

import com.google.common.annotations.VisibleForTesting
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import net.hirlab.ktsignage.ResourceAccessor
import net.hirlab.ktsignage.model.data.RingBuffer
import net.hirlab.ktsignage.util.Logger
import net.hirlab.ktsignage.util.image
import net.hirlab.ktsignage.util.runWithDelay
import net.hirlab.ktsignage.viewmodel.ViewModel
import java.io.File
import java.util.concurrent.TimeUnit
import javax.activation.MimetypesFileTypeMap

class BackgroundImageViewModel : ViewModel() {
    private lateinit var imageBuffer: RingBuffer<String>

    val currentImage = SimpleObjectProperty<Image>()
    private lateinit var prevImageCache: Image
    private lateinit var nextImageCache: Image

    private var imageSwitchingJob: Job? = null
    private var currentPointer = 0

    /**
     * Initial setup.
     */
    fun initializeImages() {
        viewModelScope.launch(Dispatchers.IO) {
            imageBuffer = RingBuffer(getImageFilePaths())
            Logger.d("loadImages(): load paths ... $imageBuffer")
            currentPointer = 0
            loadImage()
            startImageSwitching()
        }
    }

    /**
     * Loads next image and changes [currentImage].
     */
    fun nextImage() {
        if (!::nextImageCache.isInitialized) return
        viewModelScope.launch {
            currentImage.value = nextImageCache
            imageBuffer.moveNext()
            loadImage(needsCurrentImageUpdate = false)
            startImageSwitching()
        }
    }

    /**
     * Loads previous image and changes [currentImage].
     */
    fun prevImage() {
        if (!::prevImageCache.isInitialized) return
        viewModelScope.launch {
            imageBuffer.movePrevious()
            loadImage(needsCurrentImageUpdate = false)
            startImageSwitching()
        }
    }

    private fun startImageSwitching() {
        if (imageSwitchingJob?.isActive == true) imageSwitchingJob?.cancel()
        imageSwitchingJob = viewModelScope.launch(Dispatchers.Default) {
            delay(SLIDESHOW_DELAY_TIME_MILLIS)
            runWithDelay(SLIDESHOW_DELAY_TIME_MILLIS) {
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
        private val SLIDESHOW_DELAY_TIME_MILLIS = TimeUnit.MINUTES.toMillis(1)

        @VisibleForTesting
        fun getImageFilePaths(): List<String> {
            return File(ResourceAccessor.imagePath).walk()
                .toList()
                .mapNotNull { if (it.isImageFile()) it.toURI().toURL().toExternalForm() else null }
        }

        private fun File.isImageFile() = isFile && isImage()
        private fun File.isImage() =
            MimetypesFileTypeMap().getContentType(this).split("/").firstOrNull()?.equals("image") == true
    }
}