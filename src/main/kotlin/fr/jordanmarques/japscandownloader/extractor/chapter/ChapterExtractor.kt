package fr.jordanmarques.japscandownloader.extractor.chapter

import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import fr.jordanmarques.japscandownloader.extractor.MangaExtractorContext
import fr.jordanmarques.japscandownloader.extractor.chapter.image.ImageExtractor
import fr.jordanmarques.japscandownloader.extractor.chapter.image.crypted.CryptedImageExtractor
import fr.jordanmarques.japscandownloader.util.JAPSCAN_URL
import fr.jordanmarques.japscandownloader.util.length
import fr.jordanmarques.japscandownloader.util.toCbz
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import java.io.File
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO




@Component
class ChapterExtractor(
        private val imageExtractor: ImageExtractor,
        private val cryptedImageExtractor: CryptedImageExtractor
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

        val client = WebClient(BrowserVersion.CHROME)

        with(client){
            options.isCssEnabled = false
            options.isJavaScriptEnabled = true
            options.isThrowExceptionOnFailingStatusCode = false
            options.isThrowExceptionOnScriptError = false
            options.isRedirectEnabled = true
            cache.maxSize = 0
            javaScriptTimeout = 10000
            waitForBackgroundJavaScript(10000)
            waitForBackgroundJavaScriptStartingBefore(10000)
        }

        val url = "$JAPSCAN_URL/manga/$mangaName/"

        client.getPage<HtmlPage>(url)
        TimeUnit.SECONDS.sleep(5)

        val streamedWebPage = client.getPage<HtmlPage>(url).webResponse.contentAsStream

        val availableChapters = Jsoup.parse(streamedWebPage, Charsets.UTF_8.name(), url)

        val htmlChapters = availableChapters.select(".chapters_list.text-truncate>a")
        return htmlChapters.map { Chapter(name = it.text(), url = it.select("a").attr("href")) }
    }

    private fun extractScansAndCreateChapterCbz(chapterUrl: String, mangaExtractorContext: MangaExtractorContext) {
        val chapter = Jsoup.connect(chapterUrl).get()
                ?: throw RuntimeException("No Chapter found for url : $chapterUrl")

        val chapterDirectoryPath = "$currentDirectory/${mangaExtractorContext.manga}/${mangaExtractorContext.manga}-${mangaExtractorContext.currentChapter}"

        createChapterDirectory(chapterDirectoryPath)

        val numberOfScans = chapter.numberOfScans()
        for (i in 1..numberOfScans) {
            mangaExtractorContext.scanDownloadProgression = (i * 100) / numberOfScans
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

fun Document.numberOfScans(): Int = this.select("option").size
