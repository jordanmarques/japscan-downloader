package fr.jordanmarques.japscandownloader

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport
import fr.jordanmarques.japscandownloader.front.main.MainView
import fr.jordanmarques.japscandownloader.front.splashscreen.MySplashScreen
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class JapscanDownloaderApplication: AbstractJavaFxApplicationSupport()

    fun main(vararg args: String) {
        AbstractJavaFxApplicationSupport.launch(JapscanDownloaderApplication::class.java, MainView::class.java, MySplashScreen(), args)
    }




