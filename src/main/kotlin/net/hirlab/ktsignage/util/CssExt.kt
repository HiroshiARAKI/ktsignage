/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.util

import javafx.scene.paint.Color
import tornadofx.c
import tornadofx.css

fun backgroundColorStyle(color: Color) = "-fx-background-color: ${color.css};"

fun String.toColor(): Color {
    var code = this
    if (this.first() == '#') code = code.drop(1)
    if (code.substring(0..1) == "0x") code = code.drop(2)
    return c(
        code.substring(0..1).toInt(16),
        code.substring(2..3).toInt(16),
        code.substring(4..5).toInt(16),
        code.substring(6..7).toInt(16) / 255.0
    )
}