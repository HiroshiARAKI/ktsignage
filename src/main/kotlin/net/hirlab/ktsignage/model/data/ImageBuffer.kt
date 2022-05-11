package net.hirlab.ktsignage.model.data

import javafx.scene.image.Image

/**
 * Ring buffer for background images.
 */
class ImageBuffer(initialImages: List<Image>){
    private val images = initialImages.toMutableList()
    private val maxIndex: Int
        get() = images.size - 1
    private var currentIndex = 0

    /** Gets the first image. */
    fun first() = images[0]

    /** Gets the next image. */
    fun next() = images[currentIndex.next()]

    /** Gets the previous image. */
    fun prev() = images[currentIndex.prev()]

    private fun Int.next() = if (this >= maxIndex) {
        currentIndex = 0
        0
    } else {
        ++currentIndex
    }

    private fun Int.prev() = if (this <= 0) {
        currentIndex = maxIndex
        maxIndex
    } else {
        --currentIndex
    }
}