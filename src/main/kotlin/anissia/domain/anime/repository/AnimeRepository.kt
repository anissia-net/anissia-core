package anissia.domain.anime.repository

import anissia.domain.anime.Anime
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface AnimeRepository : ReactiveCrudRepository<Anime, Long> { //, QuerydslPredicateExecutor<Anime> {

    @Query("SELECT A FROM Anime A WHERE A.status <> anissia.domain.anime.AnimeStatus.END AND A.week = :week")
    fun findAllSchedule(week: String): Flux<Anime>

    fun findAllByOrderByAnimeNoDesc(pageable: Pageable): Mono<Page<Anime>>

    fun findAllByAnimeNoInOrderByAnimeNoDesc(animeNo: Collection<Long>): Flux<Anime>

    fun existsBySubject(subject: String): Mono<Boolean>

    fun existsBySubjectAndAnimeNoNot(subject: String, animeNo: Long): Mono<Boolean>

    @Query("SELECT A FROM Anime A WHERE A.animeNo IN :animeNo")
    fun findAllByIds(animeNo: List<Long>): Flux<Anime>

    @EntityGraph(attributePaths = ["captions"])
    fun findWithCaptionsByAnimeNo(animeNo: Long): Mono<Anime>

    @Modifying
    @Query("UPDATE Anime A SET A.captionCount = size(A.captions) WHERE A.animeNo = :animeNo")
    fun updateCaptionCount(animeNo: Long): Mono<Int>

    @Modifying
    @Query("UPDATE Anime A SET A.captionCount = size(A.captions) WHERE A.animeNo IN :animeNo")
    fun updateCaptionCountByIds(animeNo: List<Long>): Mono<Int>

    @Modifying
    @Query("UPDATE Anime A SET A.captionCount = size(A.captions)")
    fun updateCaptionCountAll(): Mono<Int>

    @Query("SELECT concat(a.anime_no, ' ', a.subject) FROM anime a WHERE a.autocorrect LIKE concat(:autocorrect, '%')", nativeQuery = true)
    fun findTop10ByAutocorrectStartsWith(autocorrect: String, pageable: Pageable = PageRequest.of(0, 10)): Flux<String>
}
