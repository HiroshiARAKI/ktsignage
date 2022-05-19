/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.util

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Wrapper of functions to log messages.
 */
object Logger {
    fun i(message: String) = log(message, Level.I)
    fun d(message: String) = log(message, Level.D)
    fun w(message: String) = log(message, Level.W)

    private fun log(message: String, level: Level) =
        println("Log(${time()}) > ${level.c}$TAG-${level.name}: $message$RESET")

    private fun time() = ZonedDateTime.now().format(LOGGER_FORMATTER)

    private const val TAG = "Kt Signage"

    private val LOGGER_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    private const val RESET = "\u001B[0m"
    private const val NONE = "\u001B[0m"
    private const val RED = "\u001B[31m"
    private const val GREEN = "\u001B[32m"

    private enum class Level(val c: String) {
        I(NONE), D(GREEN), W(RED)
    }
}