package anissia.configruration

import org.apache.http.client.HttpClient
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfiguration {
    @Bean
    fun restTemplate(): RestTemplate {
        val connectionManager = PoolingHttpClientConnectionManager()
        connectionManager.maxTotal = 100
        connectionManager.defaultMaxPerRoute = 50
        val requestConfig = RequestConfig
            .custom()
            .setConnectionRequestTimeout(2000)
            .setSocketTimeout(2000)
            .setConnectTimeout(2000)
            .build()
        val httpClient: HttpClient = HttpClientBuilder.create()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig).build()
        return RestTemplate(HttpComponentsClientHttpRequestFactory(httpClient))
    }
}