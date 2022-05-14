package model.data

import com.google.common.truth.Truth
import net.hirlab.ktsignage.model.data.RingBuffer
import org.junit.Test
import java.lang.IllegalArgumentException

class RingBufferTest {

    @Test(expected = IllegalArgumentException::class)
    fun `initialize - throw IllegalArgumentException if passes empty list`() {
        RingBuffer<String>(emptyList())
    }

    @Test
    fun getTripleSet() {
        val buffer = RingBuffer(FAKE_STRING_LIST)
        Truth.assertThat(buffer.getTripleSet()).isEqualTo(RingBuffer.ItemSet("D", "A", "B"))
    }

    @Test
    fun `moveNext and getTripleSet`() {
        val buffer = RingBuffer(FAKE_STRING_LIST)
        buffer.moveNext()
        Truth.assertThat(buffer.getTripleSet()).isEqualTo(RingBuffer.ItemSet("A", "B", "C"))
    }

    @Test
    fun `movePrevious and getTripleSet`() {
        val buffer = RingBuffer(FAKE_STRING_LIST)
        buffer.movePrevious()
        Truth.assertThat(buffer.getTripleSet()).isEqualTo(RingBuffer.ItemSet("C", "D", "A"))
    }

    @Test
    fun `getTripleSet - SHORT`() {
        val buffer = RingBuffer(FAKE_STRING_SHORT_LIST)
        Truth.assertThat(buffer.getTripleSet()).isEqualTo(RingBuffer.ItemSet("B", "A", "B"))
    }

    @Test
    fun `moveNext and getTripleSet - SHORT`() {
        val buffer = RingBuffer(FAKE_STRING_SHORT_LIST)
        buffer.moveNext()
        Truth.assertThat(buffer.getTripleSet()).isEqualTo(RingBuffer.ItemSet("A", "B", "A"))
    }

    @Test
    fun `movePrevious and getTripleSet - SHORT`() {
        val buffer = RingBuffer(FAKE_STRING_SHORT_LIST)
        buffer.movePrevious()
        Truth.assertThat(buffer.getTripleSet()).isEqualTo(RingBuffer.ItemSet("A", "B", "A"))
    }

    @Test
    fun `getTripleSet - ONE element`() {
        val buffer = RingBuffer(FAKE_STRING_ONE_ELEMENT)
        Truth.assertThat(buffer.getTripleSet()).isEqualTo(RingBuffer.ItemSet("A", "A", "A"))
    }

    @Test
    fun `moveNext and getTripleSet - ONE element`() {
        val buffer = RingBuffer(FAKE_STRING_ONE_ELEMENT)
        buffer.moveNext()
        Truth.assertThat(buffer.getTripleSet()).isEqualTo(RingBuffer.ItemSet("A", "A", "A"))
    }

    @Test
    fun `movePrevious and getTripleSet - ONE element`() {
        val buffer = RingBuffer(FAKE_STRING_ONE_ELEMENT)
        buffer.movePrevious()
        Truth.assertThat(buffer.getTripleSet()).isEqualTo(RingBuffer.ItemSet("A", "A", "A"))
    }

    companion object {
        private val FAKE_STRING_LIST = listOf("A", "B", "C", "D")
        private val FAKE_STRING_SHORT_LIST = listOf("A", "B")
        private val FAKE_STRING_ONE_ELEMENT = listOf("A")
    }
}