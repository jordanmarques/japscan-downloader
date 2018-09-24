package fr.jordanmarques.japscandownloader.launcher.jar

import fr.jordanmarques.japscandownloader.extractor.Extractor
import fr.jordanmarques.japscandownloader.extractor.MangaExtractorContext
import fr.jordanmarques.japscandownloader.extractor.Range
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Main {

    private val log = LoggerFactory.getLogger(Main::class.java)

    @Autowired
    private lateinit var extractors: List<Extractor>

    fun run() {
        val mode = System.getProperties().getProperty("mode")
        val manga = System.getProperties().getProperty("manga")
        val chapter = System.getProperties().getProperty("chapter")?:""
        val range = System.getProperties().getProperty("range")?:""
        val split = range.split("-")
        val from = split[0].toInt()
        val to = split[1].toInt()
        val prefix = System.getProperties()?.getProperty("prefix")?:""

        val mangaExtractorContext = MangaExtractorContext(manga = manga, chapter = chapter, prefix = prefix, range = Range(from = from, to = to))



        if (mode == null || mode.isEmpty()) {
            val message = """
                A mode should be provided:

                        full -> download all the chapters of the manga
                        range -> download a range of chapters of the manga
                        chapter -> download a chapter of the manga

                        Example: java -Dmode=full -Dmanga=nanatsu-no-taizai
                 """
            log.error(message)
            throw Exception(message)
        } else if (manga == null || manga.isEmpty()) {
            val message = """
                A manga name should be provided.
                Example: java -Dmode=full -Dmanga=nanatsu-no-taizai
                 """
            log.error(message)
            throw Exception(message)
        }

        extractors.forEach {
            when { it.mode().equals(mode, ignoreCase = true) -> it.extract(mangaExtractorContext) }
        }

    }
}
