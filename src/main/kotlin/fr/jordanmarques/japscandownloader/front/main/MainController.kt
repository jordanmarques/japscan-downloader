package fr.jordanmarques.japscandownloader.front.main

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXProgressBar
import fr.jordanmarques.japscandownloader.extractor.MangaExtractorContext
import fr.jordanmarques.japscandownloader.extractor.chapter.Chapter
import fr.jordanmarques.japscandownloader.extractor.chapter.ChapterExtractor
import fr.jordanmarques.japscandownloader.listener.CurrentChapterListener
import fr.jordanmarques.japscandownloader.listener.MangaNameListener
import fr.jordanmarques.japscandownloader.listener.ScanDownloadProgressionListener
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.springframework.stereotype.Component


@Component
class MainController(
        private val chapterExtractor: ChapterExtractor
): ScanDownloadProgressionListener, MangaNameListener, CurrentChapterListener {

    private var DOWNLOAD_ALL_LABEL = "Download All"

    private lateinit var downloadThread: Thread
    private lateinit var availableChapters: List<Chapter>
    private lateinit var checkboxes: List<CheckBox>

    private var mangaNameFormated: String = ""

    lateinit var gridPane: GridPane
    lateinit var scrollpane: ScrollPane

    lateinit var nameInput: TextField

    lateinit var stopButton: Button
    lateinit var downloadButton: Button
    lateinit var selectChaptersButton: Button

    lateinit var progressBar: JFXProgressBar

    lateinit var mangaLabel: Label
    lateinit var chapterLabel: Label

    fun initialize() {
        MangaExtractorContext.listenForChapterChange(this)
        MangaExtractorContext.listenForMangaName(this)
        MangaExtractorContext.listenForDownloadProgression(this)

        nameInput.textProperty().addListener({ _, _, newValue -> mangaNameFormated = newValue.toLowerCase().replace(" ", "-") })
    }

    fun download(mouseEvent: MouseEvent) {

        val downloadTask = object : Task<Void>() {

            public override fun call(): Void? {

                val selectedCheckBoxes = checkboxes.filter { it.isSelected }

                val chaptersToDownload = selectedCheckBoxes
                        .map { it.text }
                        .map { title -> availableChapters.first { it.name == title } }

                chapterExtractor.extract(MangaExtractorContext(manga = mangaNameFormated, chaptersToDownload = chaptersToDownload))
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

    fun selectChapters() {
        availableChapters = chapterExtractor.extractAvailableChapters(mangaNameFormated)
        buildAndDisplayCheckboxes(availableChapters)
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

    private fun buildAndDisplayCheckboxes(availableChapters: List<Chapter>) {

        if (availableChapters.isEmpty()){
            var noChaptersFound = Label("No Chapters Found")
            val vbox = VBox()
            vbox.children.addAll(noChaptersFound)
            scrollpane.content = vbox
            return
        }

        val chaptersCheckboxes = availableChapters.map { CheckBox(it.name) }

        val selectAll = JFXButton("select all")
        selectAll.setOnMouseClicked { chaptersCheckboxes.forEach { it.isSelected = true } }

        val unselectAll = JFXButton("unselect all")
        unselectAll.setOnMouseClicked { chaptersCheckboxes.forEach { it.isSelected = false } }

        val buttons = HBox(selectAll, unselectAll)
        buttons.spacing = 10.0

        checkboxes = mutableListOf<CheckBox>().plus(chaptersCheckboxes)

        val vbox = VBox()
        vbox.children.addAll(buttons)
        vbox.children.addAll(checkboxes)
        vbox.spacing = 10.0
        vbox.padding = Insets(10.0)

        scrollpane.content = vbox
        scrollpane.isPannable = true
    }
}
