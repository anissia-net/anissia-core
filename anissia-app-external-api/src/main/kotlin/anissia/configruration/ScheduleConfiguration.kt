package anissia.configruration

import anissia.services.AnimeRankService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.ScheduledTaskRegistrar


@Configuration
@EnableScheduling
class ScheduleConfiguration(
        @Value("\${env}") private val env: String,
        private val animeRankService: AnimeRankService
) : SchedulingConfigurer {

    private val log = logger<ScheduleConfiguration>()

    /** daily batch */

    /** hourly batch */
    // start 1 minute in every hour / anime rank
    @Scheduled(cron = "0 1 * * * ?") fun animeRankBatch() = animeRankService.animeRankBatch()


    /** batch configuration */
    fun dev(taskRegistrar: ScheduledTaskRegistrar) = taskRegistrar.apply {
        //log.info("[DEV] all schedule disabled and apply manual configuration, because it is develop environment")
        log.info("[DEV] all schedule enabled")
        // example
        // add(CronTask({ /* execute... */ }, "0/5 * * * * ?"))
    }

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        when (env) {
            "prod" -> log.info("[PROD] it is production environment: start batches")
            "dev" -> dev(taskRegistrar)
        }
    }
}