package anissia.domain.anime.core.service

import anissia.domain.anime.core.model.GetAnimeAutocorrectCommand
import anissia.domain.anime.core.ports.inbound.GetAnimeAutocorrect
import anissia.domain.anime.core.ports.outbound.AnimeRepository
import me.saro.kit.lang.KoreanKit
import me.saro.kit.service.CacheStore
import org.springframework.stereotype.Service

@Service
class GetAnimeAutocorrectService(
    private val animeRepository: AnimeRepository,
): GetAnimeAutocorrect {

    private val autocorrectStore = CacheStore<String, List<String>>(60 * 60000)

    override fun handle(cmd: GetAnimeAutocorrectCommand): List<String> {
        val q = cmd.q
        return if (q.length < 3) autocorrectStore.find(q) { getAnimeAutocorrectPrivate(q) }
        else  getAnimeAutocorrectPrivate(q)
    }
    private fun getAnimeAutocorrectPrivate(q: String): List<String> =
        q.replace("%", "").trim()
            .takeIf { it.isNotEmpty() }
            ?.let { animeRepository.findTop10ByAutocorrectStartsWith(KoreanKit.toJasoAtom(it)) }
            ?: listOf()
}
