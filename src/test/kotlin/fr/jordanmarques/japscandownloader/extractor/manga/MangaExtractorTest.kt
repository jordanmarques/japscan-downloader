package fr.jordanmarques.japscandownloader.extractor.manga

import fr.jordanmarques.japscandownloader.extractor.chapter.ChapterExtractor
import fr.jordanmarques.japscandownloader.getResourceAsString
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.jsoup.Jsoup
import org.junit.Before
import org.junit.Test

class MangaExtractorTest {

    @InjectMockKs
    lateinit var mangaExtractor: MangaExtractor

    @MockK
    lateinit var chapterExtractor: ChapterExtractor

    @Before
    fun init() = MockKAnnotations.init(this)

    @Test
    fun `should return the manga chapters number`() {
        val document = Jsoup.parse(getResourceAsString("number-of-manga-chapters.html", this::class))
        val expectedNumberOfChapters = 274

        val numberOfChapters = mangaExtractor.numberOfChapter(document, "")

        Assertions.assertThat(numberOfChapters).isEqualTo(expectedNumberOfChapters)

    }
}

