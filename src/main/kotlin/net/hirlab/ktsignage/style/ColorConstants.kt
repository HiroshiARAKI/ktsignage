/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.style

import tornadofx.c

object ColorConstants {
    val TRANSPARENT = c(0, 0, 0, 0.0)

    val BLACK = c(0x00, 0x00, 0x00)
    val WHITE = c(0xff, 0xff, 0xff)

    val PALE_WHITE = c(0xfe, 0xfb, 0xf3)
    val GRAY = c(0x9d, 0x9d, 0x9d)
    val LIGHT_GRAY = c(0xbd, 0xbd, 0xbd)

    val WHITE_ALPHA_30 = c(0xff, 0xff, 0xff, 0.3)
    val BLACK_ALPHA_30 = c(0x00, 0x00, 0x00, 0.3)

    val ERROR = c(0xbb, 0x64, 0x64)
    val SUCCESS = c(0x6D, 0x8B, 0x74)
}