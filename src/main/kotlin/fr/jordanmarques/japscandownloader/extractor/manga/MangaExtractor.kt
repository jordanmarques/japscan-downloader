package fr.jordanmarques.japscandownloader.extractor.manga

import fr.jordanmarques.japscandownloader.util.JAPSCAN_URL
import fr.jordanmarques.japscandownloader.extractor.chapter.ChapterExtractor
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MangaExtractor(private val chapterExtractor: ChapterExtractor) {

    private val log = LoggerFactory.getLogger(MangaExtractor::class.java)

    fun extract(japscanUrl: String = JAPSCAN_URL, manga: String, prefix: String) {
        val document = Jsoup.connect("$japscanUrl/$manga").get()

        log.info("Start downloading $manga")
        for (i in 1..numberOfChapter(document, prefix)) {
            chapterExtractor.extract(manga = manga, chapter = i.toString(), prefix = prefix)
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