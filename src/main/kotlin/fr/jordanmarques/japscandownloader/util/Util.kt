package fr.jordanmarques.japscandownloader.util

import java.io.InputStream
import java.net.URL

fun fetch(url: String): InputStream? {
    val connection = URL(url).openConnection()
    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11")
    connection.connect()

    return try {
        connection.getInputStream()
    } catch (e: Exception){
        null
    }
}

fun Int.length() = when(this) {
    0 -> 1
    else -> Math.log10(Math.abs(toDouble())).toInt() + 1
}