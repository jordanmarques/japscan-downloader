package fr.jordanmarques.japscandownloader.extractor.image.crypted

import fr.jordanmarques.japscandownloader.extractor.chapter.image.crypted.decrypt
import fr.jordanmarques.japscandownloader.getResourcePath
import fr.jordanmarques.japscandownloader.percentageOfDifferencesWith
import org.assertj.core.api.Assertions
import org.junit.Test
import java.io.File
import javax.imageio.ImageIO.read

class ImageDecryptorKtTest {

    @Test
    fun `should decrypt an image`() {

        val excpectedDecryptedImage = read(File(getResourcePath("decrypted-image.png", this::class)))
        val decryptedImage = decrypt(read(File(getResourcePath("crypted-image.jpg", this::class))))

        val differences = decryptedImage!! percentageOfDifferencesWith excpectedDecryptedImage

        Assertions.assertThat(differences).isEqualTo(0.0)
    }
}