package anissia

import anissia.configruration.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("anissia", "anissia.*", "anissia.repository")
class AnissiaCoreApplication(
		@Value("\${env}") private val env: String
): CommandLineRunner {

	var log = logger<AnissiaCoreApplication>()

	companion object {
		@JvmStatic fun main(args: Array<String>) {
			runApplication<AnissiaCoreApplication>(*args)
		}
	}

	override fun run(vararg args: String) {
		log.info("start server")
		if (env == "local") {

		}
	}
}
