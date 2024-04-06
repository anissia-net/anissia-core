package anissia.domain.anime.core.service

import anissia.domain.activePanel.core.ActivePanel
import anissia.domain.activePanel.core.ports.outbound.ActivePanelRepository
import anissia.domain.anime.core.model.AnimeItem
import anissia.domain.anime.core.model.EditAnimeCommand
import anissia.domain.anime.core.ports.inbound.EditAnime
import anissia.domain.anime.core.ports.inbound.UpdateAnimeDocument
import anissia.domain.anime.core.ports.outbound.AnimeGenreRepository
import anissia.domain.anime.core.ports.outbound.AnimeRepository
import anissia.domain.session.core.model.Session
import anissia.domain.translator.core.ports.inbound.GetPassedDate
import anissia.infrastructure.common.As
import anissia.shared.ResultWrapper
import me.saro.kit.lang.Koreans
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class EditAnimeService(
    private val animeRepository: AnimeRepository,
    private val updateAnimeDocument: UpdateAnimeDocument,
    private val animeGenreRepository: AnimeGenreRepository,
    private val activePanelRepository: ActivePanelRepository,
    private val getPassedDate: GetPassedDate
): EditAnime {

    @Transactional
    override fun handle(cmd: EditAnimeCommand, session: Session): ResultWrapper<Long> {
        cmd.validate()
        session.validateAdmin()
        getPassedDate.handle(session.an)
            ?.takeIf { it.isBefore(OffsetDateTime.now().minusDays(90)) }
            ?: return ResultWrapper.fail("애니메이션 편집은 권한 취득일로부터 90일 후에 가능합니다.", -1)

        val animeNo = cmd.animeNo

        if (animeGenreRepository.countByGenreIn(cmd.genresList).toInt() != cmd.genresList.size) {
            return ResultWrapper.fail("장르 입력이 잘못되었습니다.", -1)
        }

        if (animeRepository.existsBySubjectAndAnimeNoNot(cmd.subject, animeNo)) {
            return ResultWrapper.fail("이미 동일한 이름의 작품이 존재합니다.", -1)
        }

        val activePanel = ActivePanel(
            published = true,
            code = "ANIME",
            status = "U",
            an = session.an,
            data1 = "[${session.name}]님이 애니메이션 [${cmd.subject}]을(를) 수정하였습니다."
        )

        val anime = animeRepository.findByIdOrNull(animeNo)
            ?.also {
                if (
                    it.week == cmd.week &&
                    it.status == cmd.statusEnum &&
                    it.time == cmd.time &&
                    it.subject == cmd.subject &&
                    it.originalSubject == cmd.originalSubject &&
                    it.genres == cmd.genres &&
                    it.startDate == cmd.startDate &&
                    it.endDate == cmd.endDate &&
                    it.website == cmd.website &&
                    it.twitter == cmd.twitter
                ) {
                    return ResultWrapper.fail("변경사항이 없습니다.", -1)
                }
            }
            ?.also { activePanel.data2 = As.toJsonString(AnimeItem(it, false), mapOf("note" to "")) }
            ?.apply {
                status = cmd.statusEnum
                week = cmd.week
                time = cmd.time
                subject = cmd.subject
                originalSubject = cmd.originalSubject
                autocorrect = Koreans.toJasoAtom(cmd.subject)
                genres = cmd.genres
                startDate = cmd.startDate
                endDate = cmd.endDate
                website = cmd.website
                twitter = cmd.twitter
            }
            ?.also { activePanel.data3 = As.toJsonString(AnimeItem(it, false), mapOf("note" to cmd.note)) }
            ?: return ResultWrapper.fail("존재하지 않는 애니메이션입니다.", -1)

        animeRepository.save(anime)
        activePanelRepository.save(activePanel)
        updateAnimeDocument.handle(anime)

        return ResultWrapper.of("ok", "", anime.animeNo)
    }
}
