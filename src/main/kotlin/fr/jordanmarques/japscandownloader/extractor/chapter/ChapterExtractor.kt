package fr.jordanmarques.japscandownloader.extractor.chapter

import fr.jordanmarques.japscandownloader.common.service.CloudflareService
import fr.jordanmarques.japscandownloader.common.util.JAPSCAN_URL
import fr.jordanmarques.japscandownloader.common.util.length
import fr.jordanmarques.japscandownloader.common.util.toCbz
import fr.jordanmarques.japscandownloader.extractor.MangaExtractorContext
import fr.jordanmarques.japscandownloader.extractor.chapter.image.ImageExtractor
import fr.jordanmarques.japscandownloader.extractor.chapter.image.crypted.CryptedImageExtractor
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import java.io.File
import javax.imageio.ImageIO


@Component
class ChapterExtractor(
        private val imageExtractor: ImageExtractor,
        private val cryptedImageExtractor: CryptedImageExtractor,
        private val clouflareService: CloudflareService
) {
    private val currentDirectory = System.getProperty("user.dir")

    fun extract(mangaExtractorContext: MangaExtractorContext) {

        mangaExtractorContext.chaptersToDownload.forEach { chapter ->
            run {
                mangaExtractorContext.currentChapter = chapter.url.extractChapterName()
                extractScansAndCreateChapterCbz("$JAPSCAN_URL${chapter.url}", mangaExtractorContext)
            }
        }

    }

    fun extractAvailableChapters(mangaName: String): List<Chapter> {

        val availableChapters = clouflareService.fetchAsDocument("$JAPSCAN_URL/manga/$mangaName/") ?:throw RuntimeException("No chapters found for this manga")

        val htmlChapters = availableChapters.select(".chapters_list.text-truncate>a")
        return htmlChapters.map { Chapter(name = it.text(), url = it.select("a").attr("href")) }
    }



    private fun extractScansAndCreateChapterCbz(chapterUrl: String, mangaExtractorContext: MangaExtractorContext) {
        val chapter = clouflareService.fetchAsDocument(chapterUrl) ?:throw RuntimeException("Chapter not found")

        val chapterDirectoryPath = "$currentDirectory/${mangaExtractorContext.manga}/${mangaExtractorContext.manga}-${mangaExtractorContext.currentChapter}"

        createChapterDirectory(chapterDirectoryPath)

        val numberOfScans = chapter.numberOfScans()
        for (i in 1..numberOfScans) {
            mangaExtractorContext.scanDownloadProgression = (i * 100) / numberOfScans
            val scanDoc = clouflareService.fetchAsDocument("$chapterUrl$i.html")
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

    private fun createChapterDirectory(chapterPath: String) {
        File(chapterPath).mkdirs()
    }

    private fun Int.toCbzScanNumber(): String = when (this.length()) {
        1 -> "00$this"
        2 -> "0$this"
        3 -> "$this"
        else -> "$this"
    }
}

fun String.extractChapterName(): String {
    return this.split("/").last { it.isNotEmpty() }
}

fun Document.numberOfScans(): Int = this.select("#pages option").size
