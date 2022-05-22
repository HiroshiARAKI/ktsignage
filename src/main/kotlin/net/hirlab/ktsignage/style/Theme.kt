/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.style

import net.hirlab.ktsignage.ResourceAccessor
import tornadofx.Stylesheet
import tornadofx.cssclass

/**
 * Application theme.
 */
class Theme : Stylesheet() {
    init {
        openSansFont {
            font = ResourceAccessor.openSansFont
        }
    }

    companion object {
        val openSansFont by cssclass()
    }
}