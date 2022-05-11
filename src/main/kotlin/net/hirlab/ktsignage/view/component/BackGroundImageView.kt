package net.hirlab.ktsignage.view.component

import net.hirlab.ktsignage.view.BaseView
import net.hirlab.ktsignage.viewmodel.component.BackgroundImageViewModel
import tornadofx.borderpane
import tornadofx.imageview

/**
 * View handling the background image.
 */
class BackGroundImageView : BaseView() {
    override val root = borderpane()
    override val viewModel = BackgroundImageViewModel()

    val view = imageview(viewModel.currentImage) {
        fitWidthProperty().bind(primaryStage.widthProperty())
        isPreserveRatio = true
    }

    /**
     * Swipes background image.
     * If [isNext] is `true`, shows next image, otherwise previous one.
     */
    fun swipe(isNext: Boolean) {
        if (isNext) viewModel.nextImage()
        else viewModel.prevImage()
    }

    init {
        viewModel.loadImages()
        root.center = view
    }
}