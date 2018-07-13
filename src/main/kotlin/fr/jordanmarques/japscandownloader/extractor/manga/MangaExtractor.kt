package fr.jordanmarques.japscandownloader.extractor.manga

import fr.jordanmarques.japscandownloader.util.JAPSCAN_URL
import fr.jordanmarques.japscandownloader.extractor.chapter.ChapterExtractor
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MangaExtractor(private val chapterExtractor: ChapterExtractor) {

    private val log = LoggerFactory.getLogger(MangaExtractor::class.java)

    fun extract(japscanUrl: String = JAPSCAN_URL, manga: String, prefix: String) {
        val document = Jsoup.connect("${JAPSCAN_URL}/$manga").get()
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