package fr.jordanmarques.japscandownloader.util

import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

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

fun toCbz(sourceDirPath: String) {
    val zipFilePath = "$sourceDirPath.cbz"
    File(zipFilePath).let { if (it.exists()) it.delete() }

    val zipFile = Files.createFile(Paths.get(zipFilePath))

    ZipOutputStream(Files.newOutputStream(zipFile)).use {
        stream ->
        File(sourceDirPath).walk(direction = FileWalkDirection.TOP_DOWN)
                .filter { !it.isDirectory }
                .forEach {
                    val zipEntry = ZipEntry(it.toString().substring(sourceDirPath.length + 1))
                    stream.putNextEntry(zipEntry)
                    stream.write(Files.readAllBytes(it.toPath()))
                    stream.closeEntry()
                }
    }

    File(sourceDirPath).deleteRecursively()
}