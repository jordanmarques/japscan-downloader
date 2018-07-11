package fr.jordanmarques.japscandownloader.util

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun toCbz(sourceDirPath: String) {
    val zipFilePath = "$sourceDirPath.cbz"
    File(zipFilePath).let { if (it.exists()) it.delete() }

    val zipFile = Files.createFile(Paths.get(zipFilePath))

    ZipOutputStream(Files.newOutputStream(zipFile)).use {
        stream ->
        val sourceDir = Paths.get(sourceDirPath)
        Files.walk(sourceDir).filter { path -> !Files.isDirectory(path) }.forEach { path ->
            val zipEntry = ZipEntry(path.toString().substring(sourceDir.toString().length + 1))

            stream.putNextEntry(zipEntry)
            stream.write(Files.readAllBytes(path))
            stream.closeEntry()
        }
    }

    File(sourceDirPath).let { if (it.exists()) it.delete() }
}