package anissia

import anissia.configruration.logger
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication(
	exclude = [
		ElasticsearchDataAutoConfiguration::class,
		JpaRepositoriesAutoConfiguration::class
	]
)
@ComponentScan("anissia", "anissia.*")
class AnissiaCoreApplication: CommandLineRunner {

	var log = logger<AnissiaCoreApplication>()

	companion object {
		@JvmStatic fun main(args: Array<String>) {
			runApplication<AnissiaCoreApplication>(*args)
		}
	}

	override fun run(vararg args: String) {
		log.info("start server")
	}
}
