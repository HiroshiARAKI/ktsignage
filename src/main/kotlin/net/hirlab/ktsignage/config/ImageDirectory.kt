/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.config

object ImageDirectory : SettingItem{
    override val itemName = "Image directory"
    override var value = ""
        private set

    fun setPath(path: String) { value = path}
}