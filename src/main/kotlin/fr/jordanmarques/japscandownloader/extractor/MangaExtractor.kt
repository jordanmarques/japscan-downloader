package fr.jordanmarques.japscandownloader.extractor

import fr.jordanmarques.japscandownloader.JAPSCAN_URL
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MangaExtractor @Autowired constructor(private var chapterExtractor: ChapterExtractor) {

    private val log = LoggerFactory.getLogger(MangaExtractor::class.java)

    fun extract(japscanUrl: String = JAPSCAN_URL, manga: String, prefix: String) {
        val document = Jsoup.connect("$JAPSCAN_URL/$manga").get()
        val lastChapter = document.select("#chapitres")[0].attr("data-uri")

        val numberOfChapter = when {
            prefix.isNotEmpty() -> lastChapter.replace(prefix, "")
            else -> lastChapter
        }

        log.info("Start downloading $manga")
        for (i in 1..numberOfChapter.toInt()) {
            chapterExtractor.extract(manga = manga, chapter = i.toString(), prefix = prefix)
        }
    }
}