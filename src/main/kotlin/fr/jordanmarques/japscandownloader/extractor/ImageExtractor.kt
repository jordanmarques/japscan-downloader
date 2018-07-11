package fr.jordanmarques.japscandownloader.extractor

import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import java.awt.image.BufferedImage
import java.io.InputStream
import java.net.URL
import javax.imageio.ImageIO


@Component
class ImageExtractor {

    fun extract(document: Document): BufferedImage? {
        val imageUrl = document.select("#parImg").attr("src") or document.select("#image").attr("src")

        return imageUrl?.let { fetch(it)?.let { ImageIO.read(it) } }
    }

    private fun fetch(url: String): InputStream? {
        val connection = URL(url).openConnection()
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11")
        connection.connect()

        return connection.getInputStream()
    }

    infix fun String.or(src: String): String? {
        return when {
            this.isNotEmpty() -> this
            src.isNotEmpty() -> src
            else -> null
        }
    }
}