package fr.jordanmarques.japscandownloader.extractor.range

import fr.jordanmarques.japscandownloader.extractor.Extractor
import fr.jordanmarques.japscandownloader.extractor.MangaExtractorContext
import fr.jordanmarques.japscandownloader.util.JAPSCAN_URL
import fr.jordanmarques.japscandownloader.extractor.chapter.ChapterExtractor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ChapterRangeExtractor(private val chapterExtractor: ChapterExtractor): Extractor {

    private val log = LoggerFactory.getLogger(ChapterRangeExtractor::class.java)

    override fun mode()= "range"

    override fun extract(mangaExtractorContext: MangaExtractorContext) {

        if (mangaExtractorContext.range.isEmpty()) {
            val message = """
                            In 'range' Mode, a range of chapter should be provided
                            Example: java -Dmode=range -Dmanga=nanatsu-no-taizai -Drange=123-140
                            """
            log.error(message)
            throw Exception(message)

        }

        val split = mangaExtractorContext.range.split("-")
        val from = split[0].toInt()
        val to = split[1].toInt()

        if(to <= from){
            val message = """
                The range is incorrect, the first number should be greater than the second.
                 """
            log.error(message)
            throw Exception(message)
        }

        log.info("Start downloading ${mangaExtractorContext.manga}")
        for (i in from..to) {
            chapterExtractor.extract(MangaExtractorContext(
                    manga = mangaExtractorContext.manga,
                    chapter = i.toString(),
                    prefix = mangaExtractorContext.prefix,
                    range = mangaExtractorContext.range
            ))
        }
    }
}