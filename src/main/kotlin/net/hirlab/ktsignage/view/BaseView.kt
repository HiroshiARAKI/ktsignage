/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.view

import javafx.stage.Screen
import net.hirlab.ktsignage.util.Logger
import net.hirlab.ktsignage.viewmodel.ViewModel
import tornadofx.ChangeListener
import tornadofx.View

/**
 * Base View class.
 */
abstract class BaseView(title: String? = null) : View(title){
    protected open val viewModel: ViewModel? = null
    private val screenBounds = Screen.getPrimary().bounds
    private val screenVisualBounds = Screen.getPrimary().visualBounds

    private val fullScreenStateChangeListener = ChangeListener<Boolean> { _, _, newValue ->
        Logger.d("onChangeFullScreen: isFullScreen=$newValue")
        primaryStage.run {
            width = if (newValue) screenBounds.width else screenVisualBounds.width
            height = if (newValue) screenBounds.height else screenVisualBounds.height
        }
        onChangeFullScreenState(newValue)
    }

    init {
        primaryStage.run {
            isMaximized = true
            width = screenVisualBounds.width
            height = screenVisualBounds.height
        }
        primaryStage.fullScreenProperty().addListener(fullScreenStateChangeListener)
    }

    /**
     * Called when [primaryStage].[isFullScreen] is changed.
     */
    open fun onChangeFullScreenState(isFullScreen: Boolean) { }

    override fun onUndock() {
        super.onUndock()
        viewModel?.onDestroy()
    }
}