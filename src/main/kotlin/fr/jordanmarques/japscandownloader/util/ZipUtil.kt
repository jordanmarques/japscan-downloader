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