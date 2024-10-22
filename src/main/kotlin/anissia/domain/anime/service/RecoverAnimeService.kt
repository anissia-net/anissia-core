package anissia.domain.anime.service

import anissia.domain.account.repository.AccountRepository
import anissia.domain.activePanel.model.NewActivePanelTextCommand
import anissia.domain.activePanel.service.NewActivePanelText
import anissia.domain.agenda.repository.AgendaRepository
import anissia.domain.anime.Anime
import anissia.domain.anime.AnimeCaption
import anissia.domain.anime.AnimeStatus
import anissia.domain.anime.model.AnimeItem
import anissia.domain.anime.model.RecoverAnimeCommand
import anissia.domain.anime.repository.AnimeCaptionRepository
import anissia.domain.anime.repository.AnimeRepository
import anissia.domain.session.model.Session
import anissia.infrastructure.common.As
import anissia.shared.ResultWrapper
import com.fasterxml.jackson.core.type.TypeReference
import me.saro.kit.lang.KoreanKit
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class RecoverAnimeService(
    private val animeRepository: AnimeRepository,
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val updateAnimeDocument: UpdateAnimeDocument,
    private val newActivePanelText: NewActivePanelText,
    private val agendaRepository: AgendaRepository,
    private val accountRepository: AccountRepository,
): RecoverAnime {

    @Transactional
    override fun handle(cmd: RecoverAnimeCommand, session: Session): ResultWrapper<Long> {
        cmd.validate()
        session.validateAdmin()

        val agenda = agendaRepository
            .findByIdOrNull(cmd.agendaNo)?.takeIf { it.code == "ANIME-DEL" && it.status == "wait" }
            ?: return ResultWrapper.fail("이미 복원되었거나 존재하지 않는 애니메이션입니다.", -1)

        val animeItem = As.OBJECT_MAPPER.readValue(agenda.data1, object: TypeReference<AnimeItem>() {})

        if (animeRepository.existsById(animeItem.animeNo)) {
            return ResultWrapper.fail("이미 복원되었거나 존재하지 않는 애니메이션입니다.", -1)
        }
        if (animeRepository.existsBySubject(animeItem.subject)) {
            return ResultWrapper.fail("이미 해당 제목의 에니메이션이 있습니다.", -1)
        }

        val anime = animeRepository.save(
            Anime(
                status = AnimeStatus.valueOf(animeItem.status),
                week = animeItem.week,
                time = animeItem.time,
                subject = animeItem.subject,
                originalSubject = animeItem.originalSubject,
                autocorrect = KoreanKit.toJasoAtom(animeItem.subject),
                genres = animeItem.genres,
                startDate = animeItem.startDate,
                endDate = animeItem.endDate,
                website = animeItem.website,
                twitter = animeItem.twitter,
                captionCount = animeItem.captionCount
            )
        )

        animeItem.captions.forEach { caption ->
            val account = accountRepository.findWithRolesByName(caption.name)
            if (account?.isAdmin == true) {
                animeCaptionRepository.save(
                    AnimeCaption(
                        anime = anime,
                        an = account.an,
                        episode = caption.episode,
                        updDt = OffsetDateTime.parse(caption.updDt, As.DTF_ISO_YMDHMS),
                        website = caption.website
                    )
                )
            }
        }

        newActivePanelText.handle(NewActivePanelTextCommand("[${session.name}]님이 애니메이션 [${anime.subject}]을(를) 복원하였습니다."), null)

        updateAnimeDocument.handle(anime)
        animeRepository.updateCaptionCount(anime.animeNo)
        agendaRepository.save(agenda.apply { status = "recover" })

        return ResultWrapper.of("ok", "", anime.animeNo)
    }
}
