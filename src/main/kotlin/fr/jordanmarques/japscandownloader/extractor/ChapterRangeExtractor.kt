package fr.jordanmarques.japscandownloader.extractor

import fr.jordanmarques.japscandownloader.JAPSCAN_URL
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ChapterRangeExtractor @Autowired constructor(private var chapterExtractor: ChapterExtractor) {

    private val log = LoggerFactory.getLogger(ChapterRangeExtractor::class.java)

    fun extract(japscanUrl: String = JAPSCAN_URL, manga: String, prefix: String, range: String) {

        val split = range.split("-")
        val from = split[0].toInt()
        val to = split[1].toInt()

        if(to <= from){
            val message = """
                The range is incorrect, the first number should be greater than the second.
                 """
            log.error(message)
            throw Exception(message)
        }

        log.info("Start downloading $manga")
        for (i in from..to) {
            chapterExtractor.extract(manga = manga, chapter = i.toString(), prefix = prefix)
        }
    }
}