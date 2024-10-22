package anissia.domain.anime.service

import anissia.domain.anime.model.CaptionRecentItem
import anissia.domain.anime.model.GetCaptionRecentCommand
import anissia.domain.anime.repository.AnimeCaptionRepository
import me.saro.kit.service.CacheStore
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class GetCaptionRecentService(
    private val animeCaptionRepository: AnimeCaptionRepository,
): GetCaptionRecent {

    private val recentListStore = CacheStore<Int, Page<CaptionRecentItem>>(5 * 60000)
    override fun handle(cmd: GetCaptionRecentCommand): Page<CaptionRecentItem> {
        cmd.validate()

        // page -1 인 경우 최근 12개만 가져온다.
        // page 가 0 이상인 경우 20개 단위로 페이징하여 가져온다.
        return if (cmd.page == -1) {
            recentListStore.find(cmd.page) {
                animeCaptionRepository.findAllByUpdDtAfterAndWebsiteNotOrderByUpdDtDesc(PageRequest.of(0, 12)).map { CaptionRecentItem(it) }
            }
        } else {
            animeCaptionRepository.findAllByUpdDtAfterAndWebsiteNotOrderByUpdDtDesc(PageRequest.of(cmd.page, 20)).map { CaptionRecentItem(it) }
        }
    }
}
