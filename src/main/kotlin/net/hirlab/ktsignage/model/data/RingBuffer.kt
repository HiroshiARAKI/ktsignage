/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package net.hirlab.ktsignage.model.data

/**
 * Ring-buffer for background images.
 */
class RingBuffer<T>(items: List<T>){
    private val items = items.toMutableList()
    private val maxIndex: Int
        get() = items.size - 1
    private var currentIndex = Index(maxIndex)

    init {
        if (items.isEmpty())
            throw IllegalArgumentException("RingBuffer does not support empty list.")
    }

    /**
     * Gets previous, current and next items.
     */
    fun getTripleSet() = ItemSet(
        prev = items[currentIndex.prev()],
        current = items[currentIndex.get()],
        next = items[currentIndex.next()],
    )

    /** Moves index to next. */
    fun moveNext() = currentIndex.inc()

    /** Moves index to previous. */
    fun movePrevious() = currentIndex.dec()

    private class Index(private val maxIndex: Int) {
        private var currentIndex = 0
        fun get() = currentIndex
        fun prev(): Int {
            return if (currentIndex == 0) {
                maxIndex
            } else {
               currentIndex - 1
            }
        }
        fun next(): Int {
            return if (currentIndex == maxIndex) {
                0
            } else {
                currentIndex + 1
            }
        }
        fun inc(): Int {
            currentIndex = next()
            return currentIndex
        }
        fun dec(): Int {
            currentIndex = prev()
            return currentIndex
        }
    }

    /**
     * Wrapper data class of previous, current and next items.
     */
    data class ItemSet<T>(
        val prev: T,
        val current: T,
        val next: T,
    )
}