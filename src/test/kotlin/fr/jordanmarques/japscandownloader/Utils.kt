package fr.jordanmarques.japscandownloader

import java.awt.image.BufferedImage
import java.lang.Math.abs
import kotlin.reflect.KClass

fun getResourceAsString(name: String, kClass: KClass<out Any>): String {
    return kClass::class.java.classLoader.getResource(name).readText()
}

fun getResourcePath(name: String, kClass: KClass<out Any>): String {
    return kClass::class.java.classLoader.getResource(name).path
}

infix fun BufferedImage.percentageOfDifferencesWith(img2: BufferedImage): Double {
    val width = this.width
    val height = this.height
    val width2 = img2.width
    val height2 = img2.height
    if (width != width2 || height != height2) {
        val f = "(%d,%d) vs. (%d,%d)".format(width, height, width2, height2)
        throw IllegalArgumentException("Images must have the same dimensions: $f")
    }
    var diff = 0L
    for (y in 0 until height) {
        for (x in 0 until width) {
            diff += pixelDiff(this.getRGB(x, y), img2.getRGB(x, y))
        }
    }
    val maxDiff = 3L * 255 * width * height
    return 100.0 * diff / maxDiff
}

private fun pixelDiff(rgb1: Int, rgb2: Int): Int {
    val r1 = (rgb1 shr 16) and 0xff
    val g1 = (rgb1 shr 8)  and 0xff
    val b1 =  rgb1         and 0xff
    val r2 = (rgb2 shr 16) and 0xff
    val g2 = (rgb2 shr 8)  and 0xff
    val b2 =  rgb2         and 0xff
    return abs(r1 - r2) + abs(g1 - g2) + abs(b1 - b2)
}