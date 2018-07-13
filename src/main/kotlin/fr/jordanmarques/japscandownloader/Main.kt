package fr.jordanmarques.japscandownloader

import fr.jordanmarques.japscandownloader.extractor.chapter.ChapterExtractor
import fr.jordanmarques.japscandownloader.extractor.manga.MangaExtractor
import fr.jordanmarques.japscandownloader.extractor.range.ChapterRangeExtractor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Main @Autowired constructor(
        private var mangaExtractor: MangaExtractor,
        private var chapterRangeExtractor: ChapterRangeExtractor,
        private var chapterExtractor: ChapterExtractor
) {

    private val log = LoggerFactory.getLogger(Main::class.java)

    fun run() {
        val mode = System.getProperties().getProperty("mode")
        val manga = System.getProperties().getProperty("manga")
        val chapter = System.getProperties().getProperty("chapter")
        val range = System.getProperties().getProperty("range")
        val prefix = System.getProperties()?.getProperty("prefix") ?: ""

        if(manga.isEmpty()){
            val message = """
                A Name of manga should be provide.
                Example: java -Dmode=full -Dmanga=nanatsu-no-taizai
                 """
            log.error(message)
            throw Exception(message)
        }

        mode?.let {
            when (it) {

                "full" -> mangaExtractor.extract(manga = manga, prefix = prefix)

                "chapter" -> {
                    if(chapter.isEmpty()){
                        val message = """
                            In 'chapter mode, a number of chapter should be provide'
                            Example: java -Dmode=chapter -Dmanga=nanatsu-no-taizai -Dchapter=200
                            """
                        log.error(message)
                        throw Exception(message)
                    }
                    chapterExtractor.extract(manga = manga, chapter = chapter, prefix = prefix)
                }

                "range" -> {
                    if(range.isEmpty()){
                        val message = """
                            In 'range' Mode, a range of chapter should be provide
                            Example: java -Dmode=range -Dmanga=nanatsu-no-taizai -Drange=123-140
                            """
                        log.error(message)
                        throw Exception(message)

                    }

                    chapterRangeExtractor.extract(manga = manga, range = range, prefix = prefix)
                }

                else -> {
                    val message = """
                        A mode should be provide:

                        full -> download all the chapters of the manga
                        range -> download a range of chapters of the manga
                        chapter -> download a chapter of the manga

                        Example: java -Dmode=full -Dmanga=nanatsu-no-taizai
                        """
                    log.error(message)
                    throw Exception(message)
                }
            }
        }
    }
}