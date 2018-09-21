package fr.jordanmarques.japscandownloader

import fr.jordanmarques.japscandownloader.launcher.front.Front
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.support.beans

@SpringBootApplication
class JapscanDownloaderApplication

fun main(args: Array<String>) {
    SpringApplicationBuilder()
            .sources(JapscanDownloaderApplication::class.java)
            .initializers(
                    beans {
                        bean {
                            ApplicationRunner {
                                Front.main(args)
                            }
                        }
                    }
            )
            .run(*args)
}
