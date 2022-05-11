package viewmodel.component

import com.google.common.truth.Truth
import net.hirlab.ktsignage.viewmodel.component.BackgroundImageViewModel
import org.junit.Test

class BackgroundImageViewModelTest {
    @Test
    fun `getImageFilePaths - `() {
        val result = BackgroundImageViewModel.getImageFilePaths()
        val expected = listOf("2-2.JPG", "DSC01373.JPG",)
        Truth.assertThat(result.map { it.split("/").last() }).isEqualTo(expected)
    }
}