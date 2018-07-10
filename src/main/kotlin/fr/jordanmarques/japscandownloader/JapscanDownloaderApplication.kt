package fr.jordanmarques.japscandownloader

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JapscanDownloaderApplication

fun main(args: Array<String>) {
    runApplication<JapscanDownloaderApplication>(*args)
}
