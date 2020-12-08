package anissia.configruration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.CronTask
import org.springframework.scheduling.config.ScheduledTaskRegistrar


@Configuration
@EnableScheduling
class ScheduleConfiguration(
        @Value("\${env}")
        private val env: String
) : SchedulingConfigurer {

    private val log = logger<ScheduleConfiguration>()


//    /** daily run batch */
//    // 05:00 / anime rank clear history
//    @Scheduled(cron = "0 0 5 * * ?") fun cleanHistory() = animeRankService.cleanHistory()
//    // 09:00 / update anime 7 days rank
//    @Scheduled(cron = "0 0 9 * * ?") fun animeRank7Days() = animeRankService.updateAnimeRank(AnimeRankService.AnimeHitRankType.ANIME_RANK_7_DAYS)
//    // 09:30 / update anime 35 days rank
//    @Scheduled(cron = "0 30 9 * * ?") fun animeRank35Days() = animeRankService.updateAnimeRank(AnimeRankService.AnimeHitRankType.ANIME_RANK_35_DAYS)
//
//
//    /** weekly run batch */
//
//
//    /** monthly run batch */
//
//
//    /** batches that run more than once a day */
//    // [*:00] / 1 hour / update anime 1 day rank
//    @Scheduled(cron = "0 0 * * * ?") fun animeRank1Day() = animeRankService.updateAnimeRank(AnimeRankService.AnimeHitRankType.ANIME_RANK_1_DAY)
//
//
    /** batch configuration */
    fun dev(cronTasks: MutableList<CronTask>) = cronTasks.apply {
        log.info("[DEV] all schedule disabled and apply manual configuration, because it is develop environment")
        // example
        // add(CronTask({ /* execute... */ }, "0/5 * * * * ?"))
    }

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        when (env) {
            "prod" -> log.info("[PROD] it is production environment: start batches")
            "dev" -> dev(ArrayList()).run { taskRegistrar.setCronTasksList(this) }
        }
    }
}