package fr.jordanmarques.japscandownloader.launcher.front.main

import fr.jordanmarques.japscandownloader.extractor.Extractor
import fr.jordanmarques.japscandownloader.extractor.MangaExtractorContext
import fr.jordanmarques.japscandownloader.extractor.Range
import javafx.application.Platform
import javafx.scene.control.RadioButton
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.ToggleGroup
import javafx.scene.input.MouseEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.IOException
import java.io.OutputStream
import java.io.PrintStream
import java.lang.System.console






@Component
class MainController {

    @Autowired
    private lateinit var extractors: List<Extractor>

    lateinit var mode: ToggleGroup

    lateinit var nameInput: TextField
    lateinit var prefixInput: TextField
    lateinit var fromInput: TextField
    lateinit var toInput: TextField
    lateinit var chapterInput: TextField

    lateinit var console: TextArea

    fun download(mouseEvent: MouseEvent) {
        appendConsoleToView()
        val mangaExtractorContext = buildMangaExtractorContext(nameInput, chapterInput, prefixInput, fromInput, toInput)
        val mode = mode.selectedMode()

        extractors.forEach {
            when {
                it.mode().equals(mode, ignoreCase = true) -> it.extract(mangaExtractorContext)
            }
        }



    }

    private fun buildMangaExtractorContext(nameInput: TextField, chapterInput: TextField, prefixInput: TextField, fromInput: TextField, toInput: TextField): MangaExtractorContext {
        val from = if (fromInput.text == "") 0 else fromInput.text.toInt()
        val to = if (toInput.text == "") 0 else toInput.text.toInt()

        return MangaExtractorContext(
                manga = nameInput.text,
                chapter = chapterInput.text,
                prefix = prefixInput.text,
                range = Range(from = from, to = to))
    }

    private fun appendConsoleToView() {
        val ps = PrintStream(Console(console))
        System.setOut(ps)
        System.setErr(ps)
    }

    fun ToggleGroup.selectedMode(): String {
        return (this.selectedToggle as RadioButton).id
    }

    inner class Console(private val console: TextArea) : OutputStream() {

        private fun appendText(valueOf: String) {
            Platform.runLater { console.appendText(valueOf) }
        }

        @Throws(IOException::class)
        override fun write(b: Int) {
            appendText(b.toChar().toString())
        }
    }

}
