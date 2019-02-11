package fr.jordanmarques.japscandownloader.extractor.chapter.image.crypted

import fr.jordanmarques.japscandownloader.common.service.CloudflareService
import fr.jordanmarques.japscandownloader.common.util.JAPSCAN_CRYPTED_IMAGES_URL
import fr.jordanmarques.japscandownloader.common.util.length
import org.springframework.stereotype.Component
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


@Component
class CryptedImageExtractor (
        private val cloudflareService: CloudflareService
){

    fun extract(chapter: String, scan: Int, manga: String): BufferedImage? {
        return cloudflareService.fetchAsInputStream("$JAPSCAN_CRYPTED_IMAGES_URL/clel/${manga.capitalizeEachWords()}/$chapter/${scan.format()}.jpg")
                ?.let { decrypt(ImageIO.read(it)) }
    }

    private fun String.capitalizeEachWords(): String {
        return this.split("-").joinToString("-") { it.capitalize() }
    }

    private fun Int.format(): String =
            when {
                this.length() == 1 -> "0$this"
                else -> "$this"
            }
}

