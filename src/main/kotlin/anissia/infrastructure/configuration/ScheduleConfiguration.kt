package anissia.infrastructure.configuration

import anissia.domain.session.core.JwtKeyPair
import anissia.domain.session.core.model.JwtKeyItem
import anissia.domain.session.core.ports.outbound.JwtKeyPairRepository
import anissia.domain.session.infrastructure.JwtService
import anissia.infrastructure.common.As
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.ScheduledTaskRegistrar


@Configuration
@EnableScheduling
class ScheduleConfiguration(
    private val jwtService: JwtService,
    private val jwtKeyPairRepository: JwtKeyPairRepository
) : SchedulingConfigurer {

    private val log = As.logger<ScheduleConfiguration>()
    private val alg get() = jwtService.algorithm
    private val timeMillis get() = System.currentTimeMillis().toString()


    // start 1 minute in every hour / anime rank
    //@Scheduled(cron = "0 1 * * * ?") fun animeRankBatch() = animeRankService.animeRankBatch()

    @Scheduled(cron = "0 0/10 * * * ?")
    fun registerNewJwtKey() {
        val item = JwtKeyItem(timeMillis, alg.newRandomJwtKey())
        jwtKeyPairRepository.save(JwtKeyPair(item.kid.toLong(), item.key.stringify()))
    }

    @PostConstruct
    @Scheduled(cron = "10 0/10 * * * ?")
    fun syncJwtKeyList() =
        jwtService.updateKeyStore()

    /** batch configuration */
    fun dev(taskRegistrar: ScheduledTaskRegistrar) = taskRegistrar.apply {
        //log.info("[DEV] all schedule disabled and apply manual configuration, because it is develop environment")
        log.info("[DEV] all schedule enabled")
        // example
        // add(CronTask({ /* execute... */ }, "0/5 * * * * ?"))
    }

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
//        when (env) {
//            "prod" -> log.info("[PROD] it is production environment: start batches")
//            "dev" -> dev(taskRegistrar)
//        }
    }
}
