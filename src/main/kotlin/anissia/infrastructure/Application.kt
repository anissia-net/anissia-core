package anissia.infrastructure

import anissia.infrastructure.common.logger
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
@EnableWebFlux
@ComponentScan("anissia", "anissia.*")
class Application: CommandLineRunner {
	var log = logger<Application>()
	companion object {
		@JvmStatic
        fun main(args: Array<String>) {
			runApplication<Application>(*args)
		}
	}
	override fun run(vararg args: String) {
		log.info("start server")
	}
}
