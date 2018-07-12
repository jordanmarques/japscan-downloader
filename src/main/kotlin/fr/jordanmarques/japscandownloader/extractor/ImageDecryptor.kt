package fr.jordanmarques.japscandownloader.extractor

import java.awt.image.BufferedImage

fun decrypt(encryptedImage: BufferedImage):BufferedImage? {

    val width = encryptedImage.width
    val height = encryptedImage.height

    val subImageWidth = Math.floor(width/ 5.0).toInt()
    val subImageHeight = Math.floor(height/ 5.0).toInt()

    val subParts = extractSubParts(subImageWidth, subImageHeight, encryptedImage)

    return reorderSubParts(width, height, subImageHeight, subImageWidth, subParts)
}

private fun reorderSubParts(width: Int, height: Int, subImageHeight: Int, subImageWidth: Int, subParts: MutableList<BufferedImage>): BufferedImage {
    var decryptedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    var g = decryptedImage.graphics

    var arrayIncrement = 0
    for (i in 0..4) {
        var y = i * subImageHeight
        for (j in 0..4) {
            var x = j * subImageWidth
            g.drawImage(subParts[arrayIncrement], x, y, null)
            arrayIncrement += 1
        }
    }
    return decryptedImage
}

private fun extractSubParts(subImageWidth: Int, subImageHeight: Int, encryptedImage: BufferedImage): MutableList<BufferedImage> {
    val xCoordinates = intArrayOf(subImageWidth * 2, subImageWidth * 4, 0, subImageWidth * 3, subImageWidth)
    val yCoordinates = intArrayOf(subImageHeight * 4, subImageHeight * 3, subImageHeight * 2, subImageHeight, 0)

    val images = mutableListOf<BufferedImage>()

    for (i in 0..4) {
        val y = yCoordinates[i]
        for (j in 0..4) {
            val x = xCoordinates[j]
            val subImage = encryptedImage.getSubimage(x, y, subImageWidth, subImageHeight)
            images.add(subImage)
        }
    }

    return images
}