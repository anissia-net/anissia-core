package anissia.domain.anime.service

import anissia.domain.activePanel.ActivePanel
import anissia.domain.activePanel.repository.ActivePanelRepository
import anissia.domain.anime.Anime
import anissia.domain.anime.model.NewAnimeCommand
import anissia.domain.anime.repository.AnimeGenreRepository
import anissia.domain.anime.repository.AnimeRepository
import anissia.domain.session.model.Session
import anissia.domain.translator.service.GetPassedDate
import anissia.shared.ResultWrapper
import me.saro.kit.lang.KoreanKit
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class NewAnimeService(
    private val animeRepository: AnimeRepository,
    private val updateAnimeDocument: UpdateAnimeDocument,
    private val animeGenreRepository: AnimeGenreRepository,
    private val activePanelRepository: ActivePanelRepository,
    private val getPassedDate: GetPassedDate,
): NewAnime {

    @Transactional
    override fun handle(cmd: NewAnimeCommand, session: Session): ResultWrapper<Long> {
        cmd.validate()
        session.validateAdmin()
        getPassedDate.handle(session.an)
            ?.takeIf { it.isBefore(OffsetDateTime.now().minusDays(90)) }
            ?: return ResultWrapper.fail("애니메이션 등록은 권한 취득일로부터 90일 후에 가능합니다.", -1)

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
            autocorrect = KoreanKit.toJasoAtom(cmd.subject),
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
