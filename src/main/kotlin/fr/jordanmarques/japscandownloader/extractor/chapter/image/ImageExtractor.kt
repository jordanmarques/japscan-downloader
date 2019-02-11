package fr.jordanmarques.japscandownloader.extractor.chapter.image

import fr.jordanmarques.japscandownloader.common.service.CloudflareService
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


@Component
class ImageExtractor(
        private val cloudflareService: CloudflareService
) {

    fun extract(document: Document): BufferedImage? {
        val imageUrl = document.select("#parImg").attr("data-src") or document.select("#image").attr("data-src")

        return imageUrl?.let { cloudflareService.fetchImageAsInputStream(it)?.let { ImageIO.read(it) } }
    }

    infix fun String.or(src: String): String? {
        return when {
            this.isNotEmpty() -> this
            src.isNotEmpty() -> src
            else -> null
        }
    }
}
