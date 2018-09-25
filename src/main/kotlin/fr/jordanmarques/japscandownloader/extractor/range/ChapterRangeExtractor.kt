package fr.jordanmarques.japscandownloader.extractor.range

import fr.jordanmarques.japscandownloader.extractor.Extractor
import fr.jordanmarques.japscandownloader.extractor.MangaExtractorContext
import fr.jordanmarques.japscandownloader.util.JAPSCAN_URL
import fr.jordanmarques.japscandownloader.extractor.chapter.ChapterExtractor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ChapterRangeExtractor(private val chapterExtractor: ChapterExtractor): Extractor {

    override fun mode()= "range"

    override fun extract(mangaExtractorContext: MangaExtractorContext) {

        val range = mangaExtractorContext.range
        val from = range.from
        val to = range.to

        if(to <= from){
            val message = """
                The range is incorrect, the first number should be greater than the second.
                 """
            println(message)
            throw Exception(message)
        }

        println("Start downloading ${mangaExtractorContext.manga}")
        for (i in from..to) {
            println()
            println("Download chapter $i/$to")
            chapterExtractor.extract(MangaExtractorContext(
                    manga = mangaExtractorContext.manga,
                    chapter = i.toString(),
                    prefix = mangaExtractorContext.prefix,
                    range = mangaExtractorContext.range
            ))
        }
    }
}
