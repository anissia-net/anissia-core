package anissia.domain.anime.service

import anissia.domain.anime.Anime
import anissia.domain.anime.command.UpdateAnimeDocumentCommand
import anissia.domain.anime.repository.AnimeCaptionRepository
import anissia.domain.anime.repository.AnimeDocumentRepository
import anissia.domain.anime.repository.AnimeRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AnimeDocumentServiceImpl(
    private val animeRepository: AnimeRepository,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val animeDocumentRepository: AnimeDocumentRepository,
): AnimeDocumentService {
    private val index = "anissia_anime"

    @Transactional
    override fun update(cmd: UpdateAnimeDocumentCommand) {
        if (cmd.isDelete) {
            update(Anime(animeNo = cmd.animeNo), true)
        } else {
            animeRepository.findByIdOrNull(cmd.animeNo)
                ?.let { update(it) }
                ?: update(Anime(animeNo = cmd.animeNo), true)
        }
    }

    @Transactional
    override fun update(anime: Anime, isDelete: Boolean) {
        if (isDelete) {
            animeDocumentRepository.deleteByAnimeNo(anime.animeNo)
        } else {
            animeDocumentRepository.update(anime, animeCaptionRepository.findAllTranslatorByAnimeNo(anime.animeNo))
        }
    }

    @Transactional
    override fun reset() {
        animeDocumentRepository.dropAndCreateIndex()
        animeRepository.findAll().forEach { update(it) }
    }
}
