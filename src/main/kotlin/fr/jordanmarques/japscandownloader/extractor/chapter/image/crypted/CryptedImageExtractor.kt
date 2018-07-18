package fr.jordanmarques.japscandownloader.extractor.chapter.image.crypted

import fr.jordanmarques.japscandownloader.util.fetch
import fr.jordanmarques.japscandownloader.util.length
import org.springframework.stereotype.Component
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


@Component
class CryptedImageExtractor {

    fun extract(chapter: String, scan: Int, manga: String): BufferedImage? {
        return fetch("https://cdn.japscan.cc/cr_images/${manga.capitalizeEachWords()}/$chapter/${scan.format()}.jpg")
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

