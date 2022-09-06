package anissia.configruration

import org.elasticsearch.client.RestHighLevelClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.RestClients
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories


@EnableAutoConfiguration
@Configuration
@EnableElasticsearchRepositories(
    basePackages = ["anissia.elasticsearch.repository"],
    basePackageClasses = [ElasticsearchRepository::class]
)
class NDBConfiguration(
        @Value("\${anissia.ndb.host}")
        private val host: String,
        @Value("\${anissia.ndb.port}")
        private val port: Int
) : AbstractElasticsearchConfiguration() {
    @Bean
    override fun elasticsearchClient(): RestHighLevelClient
        = RestClients.create(ClientConfiguration.builder().connectedTo("$host:$port").build()).rest()
}
