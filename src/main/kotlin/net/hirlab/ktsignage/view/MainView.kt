package net.hirlab.ktsignage.view

import javafx.scene.Group
import javafx.scene.input.MouseButton
import javafx.scene.layout.Priority
import javafx.stage.StageStyle
import kotlinx.coroutines.launch
import net.hirlab.ktsignage.MyApp
import net.hirlab.ktsignage.model.dao.PreferencesDao
import net.hirlab.ktsignage.style.Theme
import net.hirlab.ktsignage.util.Logger
import net.hirlab.ktsignage.view.component.BackGroundImageView
import net.hirlab.ktsignage.view.component.DateView
import net.hirlab.ktsignage.view.fragment.SettingFragment
import tornadofx.*
import kotlin.math.abs

/**
 * Main view of application.
 */
class MainView : BaseView(TITLE) {
    override val root = Group()
    private val dateView: DateView by inject()
    private val backGroundImageView: BackGroundImageView by inject()

    private val preferencesDao: PreferencesDao by di()

    private val windowWidthChangeListener = ChangeListener<Number> { _, _, newVal ->
        container.prefWidth = newVal.toDouble()
    }

    private val windowHeightChangeListener = ChangeListener<Number> { _, _, newVal ->
        container.prefHeight = newVal.toDouble()
        backGroundImageView.view.prefHeight(newVal.toDouble())
    }

    private val container = vbox {
        prefWidth = primaryStage.width
        prefHeight = primaryStage.height
        addClass(Theme.base)

        spacer {  }
        add(dateView.root)
        hgrow = Priority.ALWAYS
    }

    private var mousePointMemory: MousePoint? = null

    init {
        MyApp.applicationScope.launch { preferencesDao.initialize() }

        root += backGroundImageView.root
        root += container
        root.setOnMouseClicked  {
            val doubleClicked = it.button == MouseButton.PRIMARY && it.clickCount == 2
            if (doubleClicked) {
                Logger.d("MainView is double clicked, so try to open setting fragment.")
                find<SettingFragment>().openModal(stageStyle = StageStyle.UTILITY)
            }
        }
        root.setOnMousePressed {
            mousePointMemory = MousePoint(it.sceneX, it.sceneY)
        }
        root.setOnMouseDragged {
            if (mousePointMemory == null) return@setOnMouseDragged
            val delta = it.sceneX - mousePointMemory!!.x
            if (abs(delta) < SWIPE_THRESHOLD) return@setOnMouseDragged
            backGroundImageView.swipe(delta < 0)
            mousePointMemory = null
        }
        primaryStage.widthProperty().addListener(windowWidthChangeListener)
        primaryStage.heightProperty().addListener(windowHeightChangeListener)
        container.toFront()
    }


    override fun onChangeFullScreenState(isFullScreen: Boolean) {
        super.onChangeFullScreenState(isFullScreen)
        container.prefWidth = primaryStage.width
        container.prefHeight = primaryStage.height
    }

    companion object {
        private const val TITLE = "Kt Signage"
        private const val SWIPE_THRESHOLD = 10
    }

    private data class MousePoint(val x: Double, val y: Double)
}
