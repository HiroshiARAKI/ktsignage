package net.hirlab.ktsignage.viewmodel.component

import com.google.common.annotations.VisibleForTesting
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import net.hirlab.ktsignage.ResourceAccessor
import net.hirlab.ktsignage.model.data.ImageBuffer
import net.hirlab.ktsignage.util.Logger
import net.hirlab.ktsignage.util.runWithDelay
import net.hirlab.ktsignage.viewmodel.ViewModel
import java.io.File
import java.util.concurrent.TimeUnit
import javax.activation.MimetypesFileTypeMap

class BackgroundImageViewModel : ViewModel() {
    private lateinit var images: ImageBuffer

    val currentImage = SimpleObjectProperty<Image>()

    private var imageSwitchingJob: Job? = null
    private var currentPointer = 0

    fun loadImages() {
        viewModelScope.launch(Dispatchers.IO) {
            val imagePathList = getImageFilePaths()
            images = ImageBuffer(imagePathList.map { Image(it) })
            Logger.d("loadImages(): load paths ... $imagePathList")
            currentPointer = 0
            withContext(Dispatchers.JavaFx) { currentImage.value = images.first() }
            Logger.d("loadImages(): Done current image is ${currentImage.value.url}")
            startImageSwitching()
        }
    }

    private fun startImageSwitching() {
        if (imageSwitchingJob?.isActive == true) imageSwitchingJob?.cancel()
        imageSwitchingJob = viewModelScope.launch(Dispatchers.Default) {
            delay(SLIDESHOW_DELAY_TIME_MILLIS)
            runWithDelay(SLIDESHOW_DELAY_TIME_MILLIS) {
                withContext(Dispatchers.JavaFx) { currentImage.value = images.next() }
            }
        }
    }

    fun nextImage() {
        currentImage.value = images.next()
        startImageSwitching()
    }

    fun prevImage() {
        currentImage.value = images.prev()
        startImageSwitching()
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