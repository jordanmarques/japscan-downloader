package fr.jordanmarques.japscandownloader.front.main

import com.jfoenix.controls.JFXProgressBar
import fr.jordanmarques.japscandownloader.extractor.MangaExtractorContext
import fr.jordanmarques.japscandownloader.extractor.chapter.Chapter
import fr.jordanmarques.japscandownloader.extractor.chapter.ChapterExtractor
import fr.jordanmarques.japscandownloader.listener.CurrentChapterListener
import fr.jordanmarques.japscandownloader.listener.MangaNameListener
import fr.jordanmarques.japscandownloader.listener.ScanDownloadProgressionListener
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.GridPane
import org.springframework.stereotype.Component
import com.sun.javafx.robot.impl.FXRobotHelper.getChildren
import javafx.scene.layout.VBox
import javafx.scene.control.cell.CheckBoxListCell
import org.jboss.logging.MessageBundle
import javafx.beans.value.ObservableValue
import javafx.geometry.Insets
import javafx.scene.control.CheckBox
import javafx.scene.layout.Priority
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.ListChangeListener
import org.controlsfx.control.CheckListView






@Component
class MainController(
        private val chapterExtractor: ChapterExtractor
): ScanDownloadProgressionListener, MangaNameListener, CurrentChapterListener {

    private lateinit var downloadThread: Thread
    private lateinit var chaptersToDownload: List<Chapter>

    lateinit var gridPane: GridPane

    lateinit var nameInput: TextField

    lateinit var stopButton: Button
    lateinit var downloadButton: Button
    lateinit var selectChaptersButton: Button

    lateinit var infoNameImageView: ImageView

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

        val downloadTask = object : Task<Void>() {
            public override fun call(): Void? {
                chapterExtractor.extract(MangaExtractorContext(manga = nameInput.text, chaptersToDownload = chaptersToDownload))
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
        var availableChapters = chapterExtractor.extractAvailableChapters(MangaExtractorContext(manga = nameInput.text))

//        val strings = FXCollections.observableArrayList<String>()
//        availableChapters.forEach { strings.add(it.name) }
//
//        val checkListView = CheckListView(strings)
//
//        checkListView.checkModel.checkedItems.addListener(ListChangeListener { println(checkListView.checkModel.checkedItems) })
//
//        gridPane.add(checkListView, 0,2)
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

    private fun createTooltips() {
        val mangaNameImage = Image(MainController::class.java.getResourceAsStream("/images/manga-name-info.png"))
        val prefixImage = Image(MainController::class.java.getResourceAsStream("/images/prefix-info.png"))

        val mangaNameTooltip = Tooltip()
        addImageToTooltip(mangaNameTooltip, mangaNameImage)

        val prefixTooltip = Tooltip()
        addImageToTooltip(prefixTooltip, prefixImage)

        Tooltip.install(infoNameImageView, mangaNameTooltip)
    }

    private fun addImageToTooltip(tooltip: Tooltip, image: Image) {
        tooltip.graphic = ImageView(image)
        infoNameImageView.isPickOnBounds = true
    }

}
