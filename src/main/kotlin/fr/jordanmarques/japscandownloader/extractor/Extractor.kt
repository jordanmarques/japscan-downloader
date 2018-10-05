package fr.jordanmarques.japscandownloader.extractor

import fr.jordanmarques.japscandownloader.listener.CurrentChapterListener
import fr.jordanmarques.japscandownloader.listener.MangaNameListener
import fr.jordanmarques.japscandownloader.listener.ScanDownloadProgressionListener
import kotlin.properties.Delegates

import fr.jordanmarques.japscandownloader.util.JAPSCAN_URL

interface Extractor {
    fun extract(mangaExtractorContext: MangaExtractorContext)
    fun mode(): String
}

class MangaExtractorContext{

    var lastChapterToDownload: Int? = null

    var chaptersToDownload: List<String> = mutableListOf()

    var manga: String by Delegates.observable ("") {
        _, old, new ->

        MangaExtractorContext.mangaNameListeners.forEach { it.mangaNameChange(new) }
    }
    var currentChapter: String by Delegates.observable ("") {
        _, old, new ->
        MangaExtractorContext.currentChapterListeners.forEach { it.currentChapterChange(new, lastChapterToDownload.toString()) }
    }

    var scanDownloadProgression: Int by Delegates.observable(0) {
        _, old, new ->
        MangaExtractorContext.scanDownloadProgressionListeners.forEach { it.scanDownloadProgressionChange(new) }
    }

    constructor(manga: String, currentChapter: String = "", chaptersToDownload: List<String>) {
        this.manga = manga
        this.currentChapter = currentChapter
        this.chaptersToDownload = chaptersToDownload
    }

    companion object {
        var currentChapterListeners: MutableList<CurrentChapterListener> = mutableListOf()
        var mangaNameListeners: MutableList<MangaNameListener> = mutableListOf()
        var scanDownloadProgressionListeners: MutableList<ScanDownloadProgressionListener> = mutableListOf()

        fun listenForChapterChange(listener: CurrentChapterListener){
            currentChapterListeners.add(listener)
        }
        fun listenForMangaName(listener: MangaNameListener){
            mangaNameListeners.add(listener)
        }
        fun listenForDownloadProgression(listener: ScanDownloadProgressionListener){
            scanDownloadProgressionListeners.add(listener)
        }
    }

}
