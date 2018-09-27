package fr.jordanmarques.japscandownloader.listener

interface CurrentChapterListener {
    fun currentChapterChange(chapterNumber: String, numberOfMangaChapters: String)
}
interface MangaNameListener {
    fun mangaNameChange(mangaName: String)
}
interface ScanDownloadProgressionListener {
    fun scanDownloadProgressionChange(progression: Int)
}
