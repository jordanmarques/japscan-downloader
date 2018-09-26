package fr.jordanmarques.japscandownloader.front.main

import fr.jordanmarques.japscandownloader.extractor.Extractor
import fr.jordanmarques.japscandownloader.extractor.MangaExtractorContext
import fr.jordanmarques.japscandownloader.extractor.Range
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.IOException
import java.io.OutputStream
import java.io.PrintStream


@Component
class MainController {

    @Autowired
    private lateinit var extractors: List<Extractor>

    private lateinit var downloadThread: Thread

    lateinit var mode: ToggleGroup

    lateinit var nameInput: TextField
    lateinit var prefixInput: TextField
    lateinit var fromInput: TextField
    lateinit var toInput: TextField
    lateinit var chapterInput: TextField

    lateinit var stopButton: Button
    lateinit var downloadButton: Button

    lateinit var range: RadioButton
    lateinit var chapter: RadioButton

    lateinit var console: TextArea

    lateinit var infoNameImageView: ImageView
    lateinit var infoPrefixImageView: ImageView

    fun initialize() {
        createTooltips()
    }

    fun download(mouseEvent: MouseEvent) {
        appendConsoleToView()

        val mangaExtractorContext = buildMangaExtractorContext(nameInput, chapterInput, prefixInput, fromInput, toInput)
        val extractor = extractors.first { it.mode().equals(mode.selectedMode(), ignoreCase = true) }

        val downloadTask = object : Task<Void>() {
            public override fun call(): Void? {
                extractor.extract(mangaExtractorContext)
                return null
            }
        }

        downloadThread = Thread(downloadTask)
        downloadThread.start()
        stopButton.isDisable = false
    }

    fun stop() {
        downloadThread.stop()
        stopButton.isDisable = true
        println("Stop Download")
    }

    fun onNameInputKeyPressed(){
        downloadButton.isDisable = nameInput.text.isEmpty()
    }

    fun onModeRadioButtonClicked(){
        fromInput.isVisible = range.isSelected
        toInput.isVisible = range.isSelected
        chapterInput.isVisible = chapter.isSelected
    }

    fun ToggleGroup.selectedMode(): String {
        return (this.selectedToggle as RadioButton).id
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


    private fun createTooltips() {
        val mangaNameImage = Image(MainController::class.java.getResourceAsStream("/images/manga-name-info.png"))
        val prefixImage = Image(MainController::class.java.getResourceAsStream("/images/prefix-info.png"))

        val mangaNameTooltip = Tooltip()
        addImageToTooltip(mangaNameTooltip, mangaNameImage)

        val prefixTooltip = Tooltip()
        addImageToTooltip(prefixTooltip, prefixImage)

        Tooltip.install(infoNameImageView, mangaNameTooltip)
        Tooltip.install(infoPrefixImageView, prefixTooltip)
    }

    private fun appendConsoleToView() {
        val ps = PrintStream(Console(console))
        System.setOut(ps)
        System.setErr(ps)
    }

    private fun addImageToTooltip(tooltip: Tooltip, image: Image) {
        tooltip.graphic = ImageView(image)
        infoNameImageView.isPickOnBounds = true
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
