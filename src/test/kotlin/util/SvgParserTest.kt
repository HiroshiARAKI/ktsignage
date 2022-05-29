/*
 * Copyright (c) 2022 Hiroshi ARAKI. All Rights Reserved.
 */

package util

import com.google.common.truth.Truth
import kotlinx.coroutines.runBlocking
import net.hirlab.ktsignage.ResourceAccessor
import net.hirlab.ktsignage.util.SvgParser
import org.junit.Test

class SvgParserTest {
    @Test
    fun `getPath - 01d`() = runBlocking {
        val url = ResourceAccessor.openWeatherIconsPath + "01d.svg"
        val result = SvgParser.getPath(url)
        Truth.assertThat(result[0]).isEqualTo(SVG_PATH_01d)
    }

    companion object {
        private const val SVG_PATH_01d = " M 256 379.586 C 324.414 379.586 379.586 323.972 379.586 256 C 379.586 188.028 323.972 132.414 256 132.414 C 188.028 132.414 132.414 188.028 132.414 256 C 132.414 323.972 187.586 379.586 256 379.586 Z  M 256 168.166 C 304.552 168.166 343.834 207.448 343.834 256 C 343.834 304.552 304.552 343.834 256 343.834 C 207.448 343.834 168.166 304.552 168.166 256 C 168.166 207.448 207.448 168.166 256 168.166 Z "
    }
}