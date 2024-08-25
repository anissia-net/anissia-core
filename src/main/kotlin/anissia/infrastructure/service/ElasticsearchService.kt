package anissia.infrastructure.service

import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.elasticsearch.client.Request
import org.elasticsearch.client.Response
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

    fun request(method: String, endpoint: String, body: String? = null): Response = open().use {
        val req = Request(method, endpoint)
        if (body != null) {
            req.setJsonEntity(body)
        }
        it.performRequest(req)
    }

    fun existsIndex(index: String): Boolean =
        request("HEAD", "/$index").statusLine.statusCode == 200

    fun deleteIndex(index: String): Boolean =
        request("DELETE", "/$index").statusLine.statusCode == 200

    fun createIndex(index: String, body: String): Boolean =
        request("PUT", "/$index", body).statusLine.statusCode == 200

    fun updateIndex(forceCreate: Boolean, index: String, body: String): Boolean =
        request("PUT", "/$index/_mapping", body).statusLine.statusCode == 200
}
