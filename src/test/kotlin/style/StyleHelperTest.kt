/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package style

import com.google.common.truth.Truth
import net.hirlab.ktsignage.style.cssStyleBlock
import org.junit.Test

class StyleHelperTest {
    @Test
    fun `cssStyleBlock - test`() {
        val result = cssStyleBlock {
            + "ABC"
            + "DEF"
        }
        Truth.assertThat(result).isEqualTo("ABCDEF")
    }
}