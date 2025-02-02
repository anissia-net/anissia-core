package anissia.domain.anime.service

import anissia.domain.anime.Anime
import anissia.domain.anime.command.UpdateAnimeDocumentCommand
import anissia.domain.anime.repository.AnimeCaptionRepository
import anissia.domain.anime.repository.AnimeDocumentRepository
import anissia.domain.anime.repository.AnimeRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class AnimeDocumentServiceImpl(
    private val animeRepository: AnimeRepository,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val animeDocumentRepository: AnimeDocumentRepository,
): AnimeDocumentService {
    private val index = "anissia_anime"

    @Transactional
    override fun update(cmd: UpdateAnimeDocumentCommand): Mono<String> =
        Mono.just(cmd)
            .flatMap {
                if (cmd.isDelete) {
                    Mono.just(Anime(animeNo = cmd.animeNo))
                } else {
                    animeRepository.findById(cmd.animeNo)
                }
            }
            .flatMap { update(it, cmd.isDelete) }
            .thenReturn("")

    @Transactional
    override fun update(anime: Anime, isDelete: Boolean): Mono<String> =
        Mono.fromCallable {
            if (isDelete) {
                animeDocumentRepository.deleteByAnimeNo(anime.animeNo)
            } else {
                animeCaptionRepository.findAllTranslatorByAnimeNo(anime.animeNo).collectList()
                    .flatMap { translators -> animeDocumentRepository.update(anime, translators) }
            }
        }.thenReturn("")


    @Transactional
    override fun reset(drop: Boolean): Mono<String> =
        Mono.fromCallable {
            if (drop) {
                animeDocumentRepository.dropAndCreateIndex().thenReturn(true)
            } else {
                Mono.just(true)
            }
        }
            .flatMap { animeRepository.updateCaptionCountAll() }
            .flatMapMany { animeRepository.findAll() }
            .flatMap { update(it, false) }
            .collectList().thenReturn("")
}
