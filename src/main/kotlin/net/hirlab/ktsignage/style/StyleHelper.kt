/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.style

import javafx.scene.Node
import javafx.scene.paint.Color
import tornadofx.css

/**
 * Creates CSS Styles as String.
 *
 * You can declare styles in the scope of [block]. The following snippet represents this example usage.
 * ```kotlin
 * vbox = {
 *     style = cssStyleBlock {
 *         + backgroundColor(c(0xFF, 0x0FF, 0xFF, 0.5))
 *         + textColor(c(0x23, 0x023, 0x23))
 *         + textAlign(TextAlignment.CENTER)
 *     }
 * }
 * ```
 */
fun cssStyleBlock(block: StyleHelper.() -> Unit): String {
    val helper = StyleHelper()
    block(helper)
    return helper.result
}

fun minWidth(px: Int) = "-fx-min-width: ${px}px;"

fun maxWidth(px: Int) = "-fx-max-width: ${px}px;"

fun minHeight(px: Int) = "-fx-min-height: ${px}px;"

fun backgroundColor(color: Color) = "-fx-background-color: ${color.css};"

fun fontSize(px: Number) = "-fx-font-size: ${px}px;"

fun textColor(color: Color) = "-fx-text-fill: ${color.css};"

fun textAlign(textAlignment: TextAlignment) = "-fx-alignment: ${textAlignment.content};"

fun padding(all: Int) = "-fx-padding: ${all}px;"

fun padding(topBottom: Int, leftRight: Int) = "-fx-padding: ${topBottom}px ${leftRight}px;"

fun padding(top: Int, right: Int, bottom: Int, left: Int) = "-fx-padding: ${top}px ${right}px ${bottom}px ${left}px;"

fun border(
    top: Int = 0,
    right: Int = 0,
    bottom: Int = 0,
    left: Int = 0,
    color: Color = ColorConstants.BLACK,
    style: String = "solid",
) = "-fx-border-color: ${color.css};" +
        "-fx-border-width: ${top}px ${right}px ${bottom}px ${left}px;" +
        "-fx-border-style: $style;"

fun Node.hoverOpacity(opacity: Double) {
    setOnMouseEntered { this.opacity = opacity }
    setOnMouseExited { this.opacity = 1.0 }
}

enum class TextAlignment(val content: String) {
    LEFT("left"), RIGHT("right"), CENTER("center"),
}

/**
 * Helper class to construct CSS Styles.
 */
class StyleHelper {
    var result: String = ""
        private set

    operator fun String.unaryPlus() {
        result += this
    }
}