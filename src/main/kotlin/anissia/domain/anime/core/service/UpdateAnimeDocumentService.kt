package anissia.domain.anime.core.service

import anissia.domain.anime.core.Anime
import anissia.domain.anime.core.AnimeDocument
import anissia.domain.anime.core.model.UpdateAnimeDocumentCommand
import anissia.domain.anime.core.ports.inbound.UpdateAnimeDocument
import anissia.domain.anime.core.ports.outbound.AnimeCaptionRepository
import anissia.domain.anime.core.ports.outbound.AnimeDocumentRepository
import anissia.domain.anime.core.ports.outbound.AnimeRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateAnimeDocumentService(
    private val animeRepository: AnimeRepository,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val animeDocumentRepository: AnimeDocumentRepository,
): UpdateAnimeDocument {
    @Transactional
    override fun handle(cmd: UpdateAnimeDocumentCommand): AnimeDocument? {
        return if (cmd.isDelete) {
            handle(Anime(animeNo = cmd.animeNo), true)
        } else {
            animeRepository.findByIdOrNull(cmd.animeNo)
                ?.let { handle(it) }
                ?: handle(Anime(animeNo = cmd.animeNo), true)
        }
    }


    @Transactional
    override fun handle(anime: Anime, isDelete: Boolean): AnimeDocument? {
        if (isDelete) {
            animeDocumentRepository.deleteById(anime.animeNo)
            return null
        } else {
            return animeDocumentRepository
                .findById(anime.animeNo)
                .orElseGet { AnimeDocument(animeNo = anime.animeNo) }
                .also {
                    it.animeNo = anime.animeNo
                    it.week = anime.week
                    it.subject = anime.subject + " " + anime.originalSubject
                    it.status = anime.status.name
                    it.genres = anime.genres.split(",".toRegex())
                    it.translators = animeCaptionRepository.findAllTranslatorByAnimeNo(anime.animeNo)
                    it.endDate = anime.endDate.replace("-", "").run { if (isEmpty()) 0L else toLong() }
                    animeDocumentRepository.save(it)
                }
        }
    }
}
