package fr.jordanmarques.japscandownloader.launcher.front

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage


class Front: Application() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Front::class.java)
        }
    }

    override fun start(primaryStage: Stage) {
        primaryStage.title = "JAPSCAN DOWNLOADER"

        val root = FXMLLoader.load<Parent>(javaClass.getResource("/view/sample.fxml"))
        primaryStage.scene = Scene(root, 300.0, 250.0)
        primaryStage.show()
    }

}

