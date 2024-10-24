package anissia.domain.anime.service

import anissia.domain.anime.Anime
import anissia.domain.anime.model.UpdateAnimeDocumentCommand
import anissia.domain.anime.repository.AnimeCaptionRepository
import anissia.domain.anime.repository.AnimeDocumentRepository
import anissia.domain.anime.repository.AnimeRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateAnimeDocumentService(
    private val animeRepository: AnimeRepository,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val animeDocumentRepository: AnimeDocumentRepository,
    private val objectMapper: ObjectMapper,
): UpdateAnimeDocument {
    private val index = "anissia_anime"

    @Transactional
    override fun handle(cmd: UpdateAnimeDocumentCommand) {
        if (cmd.isDelete) {
            handle(Anime(animeNo = cmd.animeNo), true)
        } else {
            animeRepository.findByIdOrNull(cmd.animeNo)
                ?.let { handle(it) }
                ?: handle(Anime(animeNo = cmd.animeNo), true)
        }
    }

    @Transactional
    override fun handle(anime: Anime, isDelete: Boolean) {
        if (isDelete) {
            animeDocumentRepository.deleteByAnimeNo(anime.animeNo)
        } else {
            animeDocumentRepository.update(anime, animeCaptionRepository.findAllTranslatorByAnimeNo(anime.animeNo))
        }
    }

    @Transactional
    override fun reset() {
        animeDocumentRepository.dropAndCreateIndex()
        animeRepository.findAll().forEach { handle(it) }
    }
}
