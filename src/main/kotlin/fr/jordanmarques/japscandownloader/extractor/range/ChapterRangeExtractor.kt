package fr.jordanmarques.japscandownloader.extractor.range

import fr.jordanmarques.japscandownloader.extractor.Extractor
import fr.jordanmarques.japscandownloader.extractor.MangaExtractorContext
import fr.jordanmarques.japscandownloader.extractor.chapter.ChapterExtractor
import org.springframework.stereotype.Component

@Component
class ChapterRangeExtractor(private val chapterExtractor: ChapterExtractor): Extractor {

    override fun mode()= "range"

    override fun extract(mangaExtractorContext: MangaExtractorContext) {

        val range = mangaExtractorContext.range

        if(range.to <= range.from){
            val message = """
                The range is incorrect, the first number should be greater than the second.
                 """
            println(message)
            throw Exception(message)
        }

        mangaExtractorContext.lastChapterToDownload = range.to

        for (i in range.from..range.to) {
            mangaExtractorContext.currentChapter = i.toString()
            chapterExtractor.extract(mangaExtractorContext)
        }
    }
}
