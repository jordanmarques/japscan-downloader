package fr.jordanmarques.japscandownloader

import org.slf4j.LoggerFactory
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
                                ref<Main>().run()
                            }
                        }
                    }
            )
            .run(*args)
}