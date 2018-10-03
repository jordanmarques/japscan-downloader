package fr.jordanmarques.japscandownloader.extractor.chapter

import fr.jordanmarques.japscandownloader.extractor.Extractor
import fr.jordanmarques.japscandownloader.extractor.MangaExtractorContext
import fr.jordanmarques.japscandownloader.extractor.chapter.image.ImageExtractor
import fr.jordanmarques.japscandownloader.extractor.chapter.image.crypted.CryptedImageExtractor
import fr.jordanmarques.japscandownloader.util.length
import fr.jordanmarques.japscandownloader.util.toCbz
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import java.io.File
import javax.imageio.ImageIO

@Component
class ChapterExtractor(
        private val imageExtractor: ImageExtractor,
        private val cryptedImageExtractor: CryptedImageExtractor
): Extractor {
    private val currentDirectory = System.getProperty("user.dir")

    override fun mode() ="chapter"

    override fun extract(mangaExtractorContext: MangaExtractorContext) {
        if (mangaExtractorContext.currentChapter.isEmpty()) {
            val message = """
                            In Chapter mode, a number of Chapter should be provided'
                            """
            println(message)
            throw Exception(message)
        }

        val chapterUrl = "${mangaExtractorContext.japscanUrl}/${mangaExtractorContext.manga}/${mangaExtractorContext.prefix}${mangaExtractorContext.currentChapter}"
        val chapter = Jsoup.connect(chapterUrl).get()
                ?: throw RuntimeException("No Chapter found for url : $chapterUrl")

        val chapterDirectoryPath = "$currentDirectory/${mangaExtractorContext.manga}/${mangaExtractorContext.manga}-${mangaExtractorContext.prefix}${mangaExtractorContext.currentChapter}"
        createChapterDirectory(mangaExtractorContext, chapterDirectoryPath)

        val numberOfScans = chapter.numberOfScans()
        for (i in 1..numberOfScans) {
            mangaExtractorContext.scanDownloadProgression = (i*100)/numberOfScans
            val scanDoc = Jsoup.connect("$chapterUrl/$i.html").get()
                    ?: throw RuntimeException("No Scan found for url : $chapterUrl/$i.html")

            val scanPath = "$chapterDirectoryPath/${i.toCbzScanNumber()}.png"

            imageExtractor.extract(scanDoc)
                    ?.let {
                        ImageIO.write(it, "png", File(scanPath))
                    }
                    ?: run {
                        cryptedImageExtractor.extract(manga = mangaExtractorContext.manga, chapter = mangaExtractorContext.currentChapter, scan = i)
                                ?.let {
                                    ImageIO.write(it, "png", File(scanPath))
                                }
                    }
        }

        toCbz(chapterDirectoryPath)

    }

    private fun createChapterDirectory(mangaExtractorContext: MangaExtractorContext, path: String) {
        File("$currentDirectory/${mangaExtractorContext.manga}/${mangaExtractorContext.manga}-${mangaExtractorContext.prefix}${mangaExtractorContext.currentChapter}").mkdirs()
    }

    private fun Int.toCbzScanNumber(): String = when (this.length()) {
        1 -> "00$this"
        2 -> "0$this"
        3 -> "$this"
        else -> "$this"
    }
}

fun Document.numberOfScans(): Int = this.select("option").size
