/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.util

/**
 * Utility class that handles the platform.
 */
object Platform {
    private val OS = System.getProperty("os.name").lowercase()
    private const val WINDOWS = "windows"
    private const val MAC = "mac"
    private const val LINUX = "linux"

    /** Whether the OS is Windows. */
    fun isWindows() = OS.startsWith(WINDOWS)

    /** Whether the OS is macOS. */
    fun isMac() = OS.startsWith(MAC)

    /** Whether the OS is Linux. */
    fun isLinux() = OS.startsWith(LINUX)
}