package fr.jordanmarques.japscandownloader.extractor.chapter

import fr.jordanmarques.japscandownloader.extractor.chapter.image.ImageExtractor
import fr.jordanmarques.japscandownloader.extractor.chapter.image.crypted.CryptedImageExtractor
import fr.jordanmarques.japscandownloader.getResourceAsString
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.jsoup.Jsoup
import org.junit.Before
import org.junit.Test

class ChapterExtractorTest {

    @InjectMockKs
    lateinit var chapterExtractor: ChapterExtractor

    @MockK
    lateinit var imageExtractor: ImageExtractor

    @MockK
    lateinit var cryptedImageExtractor: CryptedImageExtractor

    @Before
    fun init() = MockKAnnotations.init(this)

    @Test
    fun `should return the number of scans in a chapter`() {
        val document = Jsoup.parse(getResourceAsString("number-of-scan-in-chapter.html", this::class))
        val expectedNumberOfScans = 104

        val numberOfScansInChapter = chapterExtractor.numberOfScansInChapter(document)

        Assertions.assertThat(numberOfScansInChapter).isEqualTo(expectedNumberOfScans)
    }
}