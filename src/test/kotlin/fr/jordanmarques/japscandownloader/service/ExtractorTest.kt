package fr.jordanmarques.japscandownloader.service

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import org.assertj.core.api.Assertions
import org.jsoup.Jsoup
import org.junit.Before
import org.junit.Test

class ExtractorTest {

    @InjectMockKs
    lateinit var extractor: Extractor

    @Before
    fun init() = MockKAnnotations.init(this)

    @Test
    fun `should extract an image`() {
        val document = Jsoup.parse(getResource("slam-dunk.html"))

        val img = extractor.image(document)

        //ImageIO.write(img, "png", File("toto.png"))

        Assertions.assertThat(img).isNotNull
    }

    private fun getResource(path: String): String {
        return this::class.java.classLoader.getResource(path).readText()
    }
}