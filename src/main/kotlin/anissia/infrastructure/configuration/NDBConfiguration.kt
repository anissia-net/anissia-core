package anissia.infrastructure.configuration

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories
import org.springframework.stereotype.Component


@EnableAutoConfiguration
@Configuration
@EnableElasticsearchRepositories(
    basePackages = ["anissia.domain.anime.core"],
    basePackageClasses = [ElasticsearchRepository::class]
)
@Component
class NDBConfiguration(
    @Value("\${anissia.ndb.host}")
    private val host: String,
    @Value("\${anissia.ndb.port}")
    private val port: Int
) {
    @Bean
    fun elasticsearchTemplate(): ElasticsearchTemplate {
        return ElasticsearchTemplate(ElasticsearchClient(RestClientTransport(RestClient.builder(HttpHost(host, port)).build(), JacksonJsonpMapper())))
    }
}
