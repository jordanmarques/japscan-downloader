package fr.jordanmarques.japscandownloader.extractor.chapter

import fr.jordanmarques.japscandownloader.extractor.Extractor
import fr.jordanmarques.japscandownloader.extractor.MangaExtractorContext
import fr.jordanmarques.japscandownloader.extractor.chapter.image.ImageExtractor
import fr.jordanmarques.japscandownloader.extractor.chapter.image.crypted.CryptedImageExtractor
import fr.jordanmarques.japscandownloader.extractor.manga.MangaExtractor
import fr.jordanmarques.japscandownloader.util.length
import fr.jordanmarques.japscandownloader.util.toCbz
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File
import javax.imageio.ImageIO

@Component
class ChapterExtractor(
        private val imageExtractor: ImageExtractor,
        private val cryptedImageExtractor: CryptedImageExtractor
): Extractor {
    private val log = LoggerFactory.getLogger(MangaExtractor::class.java)
    private val currentDirectory = System.getProperty("user.dir")

    override fun mode() ="chapter"

    override fun extract(mangaExtractorContext: MangaExtractorContext) {
        log.info("Start downloading chapter ${mangaExtractorContext.prefix}${mangaExtractorContext.chapter}")

        if (mangaExtractorContext.chapter.isEmpty()) {
            val message = """
                            In 'chapter mode, a number of chapter should be provided'
                            Example: java -Dmode=chapter -Dmanga=nanatsu-no-taizai -Dchapter=200
                            """
            log.error(message)
            throw Exception(message)
        }

        val chapterUrl = "${mangaExtractorContext.japscanUrl}/${mangaExtractorContext.manga}/${mangaExtractorContext.prefix}${mangaExtractorContext.chapter}"
        val chapter = Jsoup.connect(chapterUrl).get()
                ?: throw RuntimeException("No Chapter found for url : $chapterUrl")

        createChapterDirectory(mangaExtractorContext)

        for (i in 1..chapter.numberOfScans()) {
            val scanDoc = Jsoup.connect("$chapterUrl/$i.html").get()
                    ?: throw RuntimeException("No Scan found for url : $chapterUrl/$i.html")

            val savePath = "$currentDirectory/${mangaExtractorContext.manga}/${mangaExtractorContext.prefix}${mangaExtractorContext.chapter}/${i.toCbzScanNumber()}.png"

            imageExtractor.extract(scanDoc)
                    ?.let {
                        ImageIO.write(it, "png", File(savePath))
                        log.info(savePath)
                    }
                    ?: run {
                        cryptedImageExtractor.extract(manga = mangaExtractorContext.manga, chapter = mangaExtractorContext.chapter, scan = i)
                                ?.let {
                                    ImageIO.write(it, "png", File(savePath))
                                    log.info(savePath)
                                }
                    }
        }

        toCbz("$currentDirectory/${mangaExtractorContext.manga}/${mangaExtractorContext.prefix}${mangaExtractorContext.chapter}")

    }

    private fun createChapterDirectory(mangaExtractorContext: MangaExtractorContext) {
        File("$currentDirectory/${mangaExtractorContext.manga}/${mangaExtractorContext.prefix}${mangaExtractorContext.chapter}").mkdirs()
    }

    private fun Int.toCbzScanNumber(): String = when (this.length()) {
        1 -> "00$this"
        2 -> "0$this"
        3 -> "$this"
        else -> "$this"
    }
}

fun Document.numberOfScans(): Int = this.select("option").size