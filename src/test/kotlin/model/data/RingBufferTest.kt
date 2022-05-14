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

    companion object {
        private val FAKE_STRING_LIST = listOf("A", "B", "C", "D")
    }
}