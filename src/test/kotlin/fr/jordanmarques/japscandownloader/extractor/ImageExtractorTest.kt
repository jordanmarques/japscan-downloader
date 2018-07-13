package fr.jordanmarques.japscandownloader.extractor

import fr.jordanmarques.japscandownloader.extractor.image.ImageExtractor
import fr.jordanmarques.japscandownloader.getResourceAsString
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import org.assertj.core.api.Assertions
import org.jsoup.Jsoup
import org.junit.Before
import org.junit.Test

class ImageExtractorTest {

    @InjectMockKs
    lateinit var imageExtractor: ImageExtractor

    @Before
    fun init() = MockKAnnotations.init(this)

    @Test
    fun `should extract an image`() {
        val document = Jsoup.parse(getResourceAsString("image-extract.html", this::class))

        val img = imageExtractor.extract(document)

        Assertions.assertThat(img).isNotNull
    }


}