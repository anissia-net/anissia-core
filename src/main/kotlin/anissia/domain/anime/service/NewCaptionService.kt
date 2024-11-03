package anissia.domain.anime.service

import anissia.domain.activePanel.model.AddTextActivePanelCommand
import anissia.domain.activePanel.service.ActivePanelService
import anissia.domain.anime.AnimeCaption
import anissia.domain.anime.model.NewCaptionCommand
import anissia.domain.anime.repository.AnimeCaptionRepository
import anissia.domain.anime.repository.AnimeRepository
import anissia.domain.session.model.Session
import anissia.shared.ResultWrapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NewCaptionService(
    private val animeCaptionRepository: AnimeCaptionRepository,
    private val animeRepository: AnimeRepository,
    private val updateAnimeDocument: UpdateAnimeDocument,
    private val activePanelService: ActivePanelService
): NewCaption {
    @Transactional
    override fun handle(cmd: NewCaptionCommand, session: Session): ResultWrapper<Unit> {
        cmd.validate()
        session.validateAdmin()

        val animeNo = cmd.animeNo

        val anime = animeRepository.findByIdOrNull(animeNo)
            ?: return ResultWrapper.fail("존재하지 않는 애니메이션입니다.")

        if (animeCaptionRepository.findById(AnimeCaption.Key(animeNo, session.an)).isPresent) {
            return ResultWrapper.fail("이미 작업중인 작품입니다.")
        }

        animeCaptionRepository.save(AnimeCaption(anime = anime, an = session.an))
        animeRepository.updateCaptionCount(animeNo)
        updateAnimeDocument.handle(anime)
        activePanelService.addText(AddTextActivePanelCommand("[${session.name}]님이 [${anime.subject}] 자막을 시작하였습니다.", true), null)

        return ResultWrapper.of("ok", "자막을 추가하였습니다.\n자막메뉴에서 확인해주세요.")
    }
}
