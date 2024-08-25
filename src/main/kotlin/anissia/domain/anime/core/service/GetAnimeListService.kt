package anissia.domain.anime.core.service

import anissia.domain.account.core.model.SearchAnimeDocumentCommand
import anissia.domain.anime.core.model.AnimeItem
import anissia.domain.anime.core.model.GetAnimeListCommand
import anissia.domain.anime.core.ports.inbound.GetAnimeList
//import anissia.domain.anime.core.ports.outbound.AnimeDocumentRepository
import anissia.domain.anime.core.ports.outbound.AnimeRepository
import anissia.infrastructure.common.As
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class GetAnimeListService(
//    private val animeDocumentRepository: AnimeDocumentRepository,
    private val animeRepository: AnimeRepository,
): GetAnimeList {
    private val log = As.logger<GetAnimeList>()

    override fun handle(cmd: GetAnimeListCommand): Page<AnimeItem> {
        val q = cmd.q
        val page = cmd.page

        if (q.isNotBlank()) {
//            val keywords = ArrayList<String>()
//            val genres = ArrayList<String>()
//            val translators = ArrayList<String>()
//            val end = q.indexOf("/완결") != -1
//
//            q.lowercase(Locale.getDefault()).split("\\s+".toRegex()).stream().map { it.trim() }.filter { it.isNotEmpty() && it != "/완결" }.forEach { word ->
//                if (word[0] == '#' && word.length > 1) genres.add(word.substring(1))
//                else if (word[0] == '@' && word.length > 1) translators.add(word.substring(1))
//                else keywords.add(word)
//            }
//
//            val result = animeDocumentRepository.search(SearchAnimeDocumentCommand(
//                keywords = keywords,
//                genres = genres,
//                translators = translators,
//                end = end,
//                pageable = PageRequest.of(page, 30)
//            ))
//
//            log.info("anime search $keywords $genres $translators $end ${result.totalElements}")
//
//            return As.replacePage(result, animeRepository.findAllByAnimeNoInOrderByAnimeNoDesc(result.content).map { AnimeItem(it) })

            return animeRepository.findAllByOrderByAnimeNoDesc(PageRequest.of(page, 30)).map { AnimeItem(it) }
        } else {
            return animeRepository.findAllByOrderByAnimeNoDesc(PageRequest.of(page, 30)).map { AnimeItem(it) }
        }
    }

}
