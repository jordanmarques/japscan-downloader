package fr.jordanmarques.japscandownloader.extractor.image

import fr.jordanmarques.japscandownloader.util.fetch
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


@Component
class ImageExtractor {

    fun extract(document: Document): BufferedImage? {
        val imageUrl = document.select("#parImg").attr("src") or document.select("#image").attr("src")

        return imageUrl?.let { fetch(it)?.let { ImageIO.read(it) } }
    }

    infix fun String.or(src: String): String? {
        return when {
            this.isNotEmpty() -> this
            src.isNotEmpty() -> src
            else -> null
        }
    }
}