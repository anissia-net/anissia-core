package anissia.configruration

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@EnableAutoConfiguration
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = ["anissia.rdb.repository"])
class DBConfiguration (

        @Value("\${anissia.rdb.driverClassName}")
        private val driverClassName: String,
        @Value("\${anissia.rdb.jdbcUrl}")
        private val jdbcUrl: String,
        @Value("\${anissia.rdb.username}")
        private val username: String,
        @Value("\${anissia.rdb.password}")
        private val password: String

) {
    @Bean("rdbDataSource")
    fun dataSource(): DataSource
            = HikariConfig()
            .let {
                it.driverClassName = driverClassName
                it.jdbcUrl = jdbcUrl
                it.username = username
                it.password = password
                HikariDataSource(it)
            }
}
