package fr.jordanmarques.japscandownloader.extractor.chapter

import fr.jordanmarques.japscandownloader.util.JAPSCAN_URL
import fr.jordanmarques.japscandownloader.extractor.image.ImageExtractor
import fr.jordanmarques.japscandownloader.extractor.image.crypted.CryptedImageExtractor
import fr.jordanmarques.japscandownloader.extractor.manga.MangaExtractor
import fr.jordanmarques.japscandownloader.util.length
import fr.jordanmarques.japscandownloader.util.toCbz
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import javax.imageio.ImageIO

@Component
class ChapterExtractor constructor(
        private val imageExtractor: ImageExtractor,
        private val cryptedImageExtractor: CryptedImageExtractor
) {
    private val log = LoggerFactory.getLogger(MangaExtractor::class.java)
    private val currentDirectory = System.getProperty("user.dir")

    fun extract(japscanUrl: String = JAPSCAN_URL, manga: String, chapter: String, prefix: String = "") {
        log.info("Start downloading chapter $prefix$chapter")

        val chapterUrl = "$japscanUrl/$manga/$prefix$chapter"
        val document = Jsoup.connect(chapterUrl).get()
                ?: throw RuntimeException("No Chapter found for url : $chapterUrl")

        createChapterDirectory(manga, chapter, prefix)

        for (i in 1..numberOfScansInChapter(document)) {
            val scanDoc = Jsoup.connect("$chapterUrl/$i.html").get()
                    ?: throw RuntimeException("No Scan found for url : $chapterUrl/$i.html")

            val savePath = "$currentDirectory/$manga/$prefix$chapter/${i.toCbzScanNumber()}.png"

            imageExtractor.extract(scanDoc)
                    ?.let {
                        ImageIO.write(it, "png", File(savePath))
                        log.info(savePath)
                    }
                    ?: run {
                        cryptedImageExtractor.extract(manga = manga, chapter = chapter, scan = i)
                                ?.let {
                                    ImageIO.write(it, "png", File(savePath))
                                    log.info(savePath)
                                }
                    }
        }

        toCbz("$currentDirectory/$manga/$prefix$chapter")

    }

    private fun createChapterDirectory(manga: String, chapter: String, prefix: String) {
        File("$currentDirectory/$manga/$prefix$chapter").mkdirs()
    }

    private fun numberOfScansInChapter(document: Document): Int {
        return document.select("option").size
    }

    private fun Int.toCbzScanNumber(): String = when (this.length()) {
        1 -> "00$this"
        2 -> "0$this"
        3 -> "$this"
        else -> "$this"
    }
}