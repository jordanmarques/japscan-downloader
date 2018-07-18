package fr.jordanmarques.japscandownloader.extractor.manga

import fr.jordanmarques.japscandownloader.extractor.Extractor
import fr.jordanmarques.japscandownloader.extractor.MangaExtractorContext
import fr.jordanmarques.japscandownloader.extractor.chapter.ChapterExtractor
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MangaExtractor(private val chapterExtractor: ChapterExtractor): Extractor {

    private val log = LoggerFactory.getLogger(MangaExtractor::class.java)

    override fun mode() = "full"

    override fun extract(mangaExtractorContext: MangaExtractorContext) {
        val document = Jsoup.connect("${mangaExtractorContext.japscanUrl}/${mangaExtractorContext.manga}").get()

        log.info("Start downloading ${mangaExtractorContext.manga}")
        for (i in 1..numberOfChapter(document, mangaExtractorContext.prefix)) {
            chapterExtractor.extract(MangaExtractorContext(
                    manga = mangaExtractorContext.manga,
                    chapter = i.toString(),
                    prefix = mangaExtractorContext.prefix,
                    range = mangaExtractorContext.range)
            )
        }
    }

    fun numberOfChapter(document: Document, prefix: String): Int {
        val lastChapter = document.select("#chapitres")[0].attr("data-uri")

        return when {
            prefix.isNotEmpty() -> lastChapter.replace(prefix, "").toInt()
            else -> lastChapter.toInt()
        }
    }
}