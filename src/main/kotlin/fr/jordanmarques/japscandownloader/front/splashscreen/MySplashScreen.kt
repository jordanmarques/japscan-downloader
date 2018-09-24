package fr.jordanmarques.japscandownloader.front.splashscreen

import de.felixroske.jfxsupport.SplashScreen

class MySplashScreen: SplashScreen() {

    override fun visible(): Boolean {
        return true
    }

    override fun getImagePath(): String {
        return "/splash.jpg"
    }
}
