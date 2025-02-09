package anissia.domain.anime.service

import anissia.domain.anime.Anime
import anissia.domain.anime.command.UpdateAnimeDocumentCommand
import anissia.domain.anime.repository.AnimeCaptionRepository
import anissia.domain.anime.repository.AnimeDocumentRepository
import anissia.domain.anime.repository.AnimeRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AnimeDocumentServiceImpl(
    private val animeRepository: AnimeRepository,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val animeDocumentRepository: AnimeDocumentRepository,
): AnimeDocumentService {
    @Transactional
    override fun update(cmd: UpdateAnimeDocumentCommand): Mono<String> =
        Mono.defer {
            cmd.validate()
            if (!cmd.isDelete) {
                val anime = animeRepository.findByIdOrNull(cmd.animeNo)
                if (anime != null) {
                    return@defer update(anime, false)
                }
            }
            return@defer update(Anime(animeNo = cmd.animeNo), true)
        }

    @Transactional
    override fun update(anime: Anime, isDelete: Boolean): Mono<String> =
        Mono.defer {
            if (isDelete) {
                animeDocumentRepository.deleteByAnimeNo(anime.animeNo)
                return@defer Mono.just("")
            } else {
                val translators = animeCaptionRepository.findAllTranslatorByAnimeNo(anime.animeNo)
                return@defer animeDocumentRepository.update(anime, translators).thenReturn("")
            }
        }


    @Transactional
    override fun reset(drop: Boolean): Mono<String> =
        Mono.defer {
            if (drop) {
                animeDocumentRepository.dropAndCreateIndex().thenReturn("")
            } else {
                Mono.just("")
            }
        }
            .flatMapMany {
                animeRepository.updateCaptionCountAll()
                Flux.just(*animeRepository.findAll().toTypedArray())
            }
            .flatMap { anime -> update(anime, false) }
            .collectList().thenReturn("")
}
