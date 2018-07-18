package fr.jordanmarques.japscandownloader.extractor

import fr.jordanmarques.japscandownloader.util.JAPSCAN_URL

interface Extractor {
    fun extract(mangaExtractorContext: MangaExtractorContext)
    fun mode(): String
}

data class MangaExtractorContext(val japscanUrl: String = JAPSCAN_URL,
                                 val manga: String,
                                 val chapter: String,
                                 val prefix: String,
                                 val range: String)
