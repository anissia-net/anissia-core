package anissia.infrastructure.configuration

import anissia.domain.account.repository.AccountRecoverAuthRepository
import anissia.domain.account.repository.AccountRegisterAuthRepository
import anissia.domain.activePanel.repository.ActivePanelRepository
import anissia.domain.agenda.service.AgendaService
import anissia.domain.anime.service.AnimeRankService
import anissia.domain.session.repository.JwtKeyPairRepository
import anissia.domain.session.repository.LoginFailRepository
import anissia.domain.session.repository.LoginPassRepository
import anissia.domain.session.repository.LoginTokenRepository
import anissia.domain.session.service.JwtService
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import reactor.core.publisher.Mono


@Configuration
@EnableScheduling
class ScheduleConfiguration(
    private val jwtService: JwtService,
    private val animeRankService: AnimeRankService,
    private val agendaService: AgendaService,
    private val jwtKeyPairRepository: JwtKeyPairRepository,
    private val activePanelRepository: ActivePanelRepository,
    private val loginPassRepository: LoginPassRepository,
    private val loginFailRepository: LoginFailRepository,
    private val loginTokenRepository: LoginTokenRepository,
    private val accountRecoverAuthRepository: AccountRecoverAuthRepository,
    private val accountRegisterAuthRepository: AccountRegisterAuthRepository,
) {
    // 애니메이션 순위 업데이트
    // 매일 1:00 에 실행
    @Scheduled(cron = "0 1 * * * ?")
    fun animeRankBatch(): Mono<String> =
        animeRankService.renew()

    // jwt 키 갱신
    // 매 10분마다 실행
    @PostConstruct
    @Scheduled(cron = "0 0/10 * * * ?")
    fun registerNewJwtKey(): Mono<String> =
        jwtService.registerNewJwtKey()

    // jwt 키 싱크
    // 매 10분 10초마다 실행
    @PostConstruct
    @Scheduled(cron = "10 0/10 * * * ?")
    fun syncJwtKeyList(): Mono<String> =
        jwtService.renewKeyStore()

    // 오래된 jwt 키 삭제
    // 매시간 2분에 실행
    @Scheduled(cron = "0 2 * * * ?")
    fun deleteOldJwtKey() {
        jwtKeyPairRepository.deleteAllByKidBefore()
    }

    // 삭제 예정 애니메이션 삭제
    // 매일 20시에 실행
    @Scheduled(cron = "0 0 20 * * ?")
    fun deletePaddingDeleteAnime(): Mono<Int> =
        agendaService.deleteDeletePaddingAnime()

    // 오래된 활동이력 삭제
    // 매일 10시에 실행
    @Scheduled(cron = "0 0 10 * * ?")
    fun deleteOldActivePanelList() {
        activePanelRepository.deleteAllByRegDtBefore()
    }

    // 오래된 로그인 이력 삭제
    // 매일 10시 30분에 실행
    @Scheduled(cron = "0 30 10 * * ?")
    fun deleteOldLoginHistory() {
        loginPassRepository.deleteAllByPassDtBefore()
        loginFailRepository.deleteAllByFailDtBefore()
        loginTokenRepository.deleteAllByExpDtBefore()
        accountRecoverAuthRepository.deleteAllByExpDtBefore()
        accountRegisterAuthRepository.deleteAllByExpDtBefore()
    }
}
