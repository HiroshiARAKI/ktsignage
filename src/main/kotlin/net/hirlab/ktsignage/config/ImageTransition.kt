/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.config

import java.util.concurrent.TimeUnit

enum class ImageTransition(override val itemName: String, override val value: Long) : SettingItem {
//    DUR_1_SEC("1 sec.", TimeUnit.SECONDS.toMicros(1)),
    DUR_30_SEC("30 sec.", TimeUnit.SECONDS.toMicros(30)),
    DUR_1_MIN("1 min.", TimeUnit.MINUTES.toMillis(1)),
    DUR_2_MIN("2 min.", TimeUnit.MINUTES.toMillis(2)),
    DUR_3_MIN("3 min.", TimeUnit.MINUTES.toMillis(3)),
    DUR_4_MIN("4 min.", TimeUnit.MINUTES.toMillis(4)),
    DUR_5_MIN("5 min.", TimeUnit.MINUTES.toMillis(5)),
    DUR_10_MIN("10 min.", TimeUnit.MINUTES.toMillis(10)),
    ;

    override fun select() {
        Setting.imageTransition = this
    }

    companion object : SettingItem.SettingItemCompanion {
        override val DEFAULT = DUR_1_MIN
        override fun valueOfOrDefault(name: String) = try {
            valueOf(name)
        } catch (e: IllegalArgumentException) {
            DEFAULT
        }
    }
}