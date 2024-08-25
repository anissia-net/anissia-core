package anissia.infrastructure.service

import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.elasticsearch.client.RestClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ElasticsearchService(
    @Value("\${anissia.ndb.host}")
    private val host: String,
    @Value("\${anissia.ndb.port}")
    private val port: Int,
    @Value("\${anissia.ndb.username}")
    private val username: String,
    @Value("\${anissia.ndb.password}")
    private val password: String,
) {
    private val credentialsProvider = BasicCredentialsProvider()
        .apply { setCredentials(AuthScope.ANY, UsernamePasswordCredentials(username, password)) }

    fun open(): RestClient = RestClient
        .builder(HttpHost(host, port))
        .setHttpClientConfigCallback { it.setDefaultCredentialsProvider(credentialsProvider) }
        .build()
}
