package net.hirlab.ktsignage.util

/**
 * Wrapper of functions to log messages.
 */
object Logger {
    fun i(message: String) = log(message, Level.I)
    fun d(message: String) = log(message, Level.D)
    fun w(message: String) = log(message, Level.W)

    private fun log(message: String, level: Level) =
        println("Log > ${level.c}$TAG-${level.name}: $message$RESET")

    private const val TAG = "Kt Signage"

    private const val RESET = "\u001B[0m"
    private const val NONE = "\u001B[0m"
    private const val RED = "\u001B[31m"
    private const val GREEN = "\u001B[32m"

    private enum class Level(val c: String) {
        I(NONE), D(GREEN), W(RED)
    }
}