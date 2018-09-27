package fr.jordanmarques.japscandownloader.front.main

import com.jfoenix.controls.JFXProgressBar
import fr.jordanmarques.japscandownloader.extractor.Extractor
import fr.jordanmarques.japscandownloader.extractor.MangaExtractorContext
import fr.jordanmarques.japscandownloader.extractor.Range
import fr.jordanmarques.japscandownloader.listener.CurrentChapterListener
import fr.jordanmarques.japscandownloader.listener.MangaNameListener
import fr.jordanmarques.japscandownloader.listener.ScanDownloadProgressionListener
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import org.omg.CORBA.portable.Delegate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.IOException
import java.io.OutputStream
import java.io.PrintStream
import kotlin.properties.Delegates


@Component
class MainController : ScanDownloadProgressionListener, MangaNameListener, CurrentChapterListener {

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

    lateinit var infoNameImageView: ImageView
    lateinit var infoPrefixImageView: ImageView

    lateinit var progressBar: JFXProgressBar

    lateinit var mangaLabel: Label
    lateinit var chapterLabel: Label

    fun initialize() {
        createTooltips()
        MangaExtractorContext.listenForChapterChange(this)
        MangaExtractorContext.listenForMangaName(this)
        MangaExtractorContext.listenForDownloadProgression(this)
    }

    fun download(mouseEvent: MouseEvent) {

        val mangaExtractorContext = buildMangaExtractorContext(nameInput, chapterInput, prefixInput, fromInput, toInput)
        val extractor = extractors.first { it.mode().equals(mode.selectedMode(), ignoreCase = true) }

        val downloadTask = object : Task<Void>() {
            public override fun call(): Void? {
                extractor.extract(mangaExtractorContext)
                return null
            }

            public override fun succeeded() {
                super.succeeded()
                Platform.runLater {
                    progressBar.progress = 0.0
                    stopButton.isDisable = true
                    mangaLabel.text = ""
                    chapterLabel.text = ""
                }
            }
        }

        downloadThread = Thread(downloadTask)
        downloadThread.start()
        stopButton.isDisable = false
    }

    fun stop() {
        downloadThread.stop()
        stopButton.isDisable = true
        progressBar.progress = 0.0
        mangaLabel.text = ""
        chapterLabel.text = ""
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

    override fun scanDownloadProgressionChange(progression: Int) {
        Platform.runLater {
            progressBar.progress = (progression.toDouble() / 100)
        }
    }

    override fun currentChapterChange(chapterNumber: String, numberOfMangaChapters: String) {
        Platform.runLater {
            if(numberOfMangaChapters == "null"){
                chapterLabel.text = chapterNumber
            } else {
                chapterLabel.text = "$chapterNumber/$numberOfMangaChapters"
            }
        }
    }

    override fun mangaNameChange(mangaName: String) {
        val cleanName = mangaName.replace("-","  ").capitalize()
        Platform.runLater {
            mangaLabel.text = cleanName
        }
    }

    private fun buildMangaExtractorContext(nameInput: TextField, chapterInput: TextField, prefixInput: TextField, fromInput: TextField, toInput: TextField): MangaExtractorContext {
        val from = if (fromInput.text == "") 0 else fromInput.text.toInt()
        val to = if (toInput.text == "") 0 else toInput.text.toInt()

        return MangaExtractorContext(
                manga = nameInput.text,
                currentChapter = chapterInput.text,
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

    private fun addImageToTooltip(tooltip: Tooltip, image: Image) {
        tooltip.graphic = ImageView(image)
        infoNameImageView.isPickOnBounds = true
    }

}
