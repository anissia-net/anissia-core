package anissia.domain.anime.core.service

import anissia.domain.activePanel.core.ActivePanel
import anissia.domain.activePanel.core.ports.outbound.ActivePanelRepository
import anissia.domain.anime.core.Anime
import anissia.domain.anime.core.model.NewAnimeCommand
import anissia.domain.anime.core.ports.inbound.NewAnime
import anissia.domain.anime.core.ports.inbound.UpdateAnimeDocument
import anissia.domain.anime.core.ports.outbound.AnimeGenreRepository
import anissia.domain.anime.core.ports.outbound.AnimeRepository
import anissia.domain.session.core.model.Session
import anissia.shared.ResultWrapper
import me.saro.kit.lang.Koreans
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NewAnimeService(
    private val animeRepository: AnimeRepository,
    private val updateAnimeDocument: UpdateAnimeDocument,
    private val animeGenreRepository: AnimeGenreRepository,
    private val activePanelRepository: ActivePanelRepository
): NewAnime {

    @Transactional
    override fun handle(cmd: NewAnimeCommand, session: Session): ResultWrapper<Long> {
        cmd.validate()
        session.validateAdmin()

        if (animeGenreRepository.countByGenreIn(cmd.genresList).toInt() != cmd.genresList.size) {
            return ResultWrapper.fail("장르 입력이 잘못되었습니다.", -1)
        }

        if (animeRepository.existsBySubject(cmd.subject)) {
            return ResultWrapper.fail("이미 동일한 이름의 작품이 존재합니다.", -1)
        }

        val anime = Anime(
            status = cmd.statusEnum,
            week = cmd.week,
            time = cmd.time,
            subject = cmd.subject,
            originalSubject = cmd.originalSubject,
            autocorrect = Koreans.toJasoAtom(cmd.subject),
            genres = cmd.genres,
            startDate = cmd.startDate,
            endDate = cmd.endDate,
            website = cmd.website,
            twitter = cmd.twitter,
        )

        val activePanel = ActivePanel(
            published = true,
            code = "ANIME",
            status = "C",
            an = session.an,
            data1 = "[${session.name}]님이 애니메이션 [${anime.subject}]을(를) 추가하였습니다."
        )

        animeRepository.save(anime)
        activePanelRepository.save(activePanel)
        updateAnimeDocument.handle(anime)

        return ResultWrapper.ok(anime.animeNo)
    }
}
