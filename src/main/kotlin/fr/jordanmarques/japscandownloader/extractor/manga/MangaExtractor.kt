package fr.jordanmarques.japscandownloader.extractor.manga

import fr.jordanmarques.japscandownloader.extractor.Extractor
import fr.jordanmarques.japscandownloader.extractor.MangaExtractorContext
import fr.jordanmarques.japscandownloader.extractor.chapter.ChapterExtractor
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component

@Component
class MangaExtractor(private val chapterExtractor: ChapterExtractor): Extractor {

    override fun mode() = "full"

    override fun extract(mangaExtractorContext: MangaExtractorContext) {
        val manga = Jsoup.connect("${mangaExtractorContext.japscanUrl}/${mangaExtractorContext.manga}").get()

        println()
        println("Start downloading ${mangaExtractorContext.manga}")
        val numberOfChapters = manga.numberOfChapters(mangaExtractorContext.prefix)
        for (i in 1..numberOfChapters) {
            println("Download chapter $i/$numberOfChapters")
            chapterExtractor.extract(MangaExtractorContext(
                    manga = mangaExtractorContext.manga,
                    chapter = i.toString(),
                    prefix = mangaExtractorContext.prefix,
                    range = mangaExtractorContext.range)
            )
        }
    }
}

fun Document.numberOfChapters(prefix: String): Int {
    val lastChapter = this.select("#chapitres")[0].attr("data-uri")

    return when {
        prefix.isNotEmpty() -> lastChapter.replace(prefix, "").toInt()
        else -> lastChapter.toInt()
    }
}
