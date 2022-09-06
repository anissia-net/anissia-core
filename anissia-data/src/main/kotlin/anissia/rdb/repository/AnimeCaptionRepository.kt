package anissia.rdb.repository

import anissia.rdb.entity.AnimeCaption
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface AnimeCaptionRepository : JpaRepository<AnimeCaption, AnimeCaption.Key> {

    @EntityGraph(attributePaths = ["account"])
    @Query("SELECT a FROM AnimeCaption a WHERE a.anime.animeNo = :animeNo ORDER BY a.updDt DESC")
    fun findAllWithAccountByAnimeNoOrderByUpdDtDesc(animeNo: Long): List<AnimeCaption>

    @EntityGraph(attributePaths = ["anime"])
    @Query("SELECT a FROM AnimeCaption a JOIN a.anime b WHERE a.an = :an AND b.status <> anissia.rdb.entity.AnimeStatus.END ORDER BY a.updDt DESC")
    fun findAllWithAnimeForAdminCaptionActiveList(an: Long, pageable: Pageable): Page<AnimeCaption>

    @EntityGraph(attributePaths = ["anime"])
    @Query("SELECT a FROM AnimeCaption a JOIN a.anime b WHERE a.an = :an AND b.status = anissia.rdb.entity.AnimeStatus.END ORDER BY a.updDt DESC")
    fun findAllWithAnimeForAdminCaptionEndList(an: Long, pageable: Pageable): Page<AnimeCaption>

    @Modifying
    @Query("DELETE FROM AnimeCaption a WHERE a.anime.animeNo = :animeNo")
    fun deleteByAnimeNo(animeNo: Long): Int

    //@EntityGraph(attributePaths = ["account"])
    @Query("SELECT a.name FROM Account a WHERE a.an in (SELECT b.an FROM AnimeCaption b where b.anime.animeNo = :animeNo)")
    fun findAllTranslatorByAnimeNo(animeNo: Long): List<String>

    fun findAllByAn(an: Long): List<AnimeCaption>

    @EntityGraph(attributePaths = ["account", "anime"])
    fun findTop20ByUpdDtBeforeAndWebsiteNotOrderByUpdDtDesc(updDt: LocalDateTime = LocalDateTime.now().plusMinutes(10), website: String = ""): List<AnimeCaption>

//
//    @EntityGraph(attributePaths = ["anime"])
//    fun findAllWithAnimeByUnOrderByUpdDtDesc(un: Long, pageable: Pageable): Page<AnimeCaption>
//
//    fun findByAnAndUn(an: Long, un: Long): AnimeCaption?
//
//    @EntityGraph(attributePaths = ["anime"])
//    fun findWithAnimeByAnAndUn(an: Long, un: Long): AnimeCaption?
}
