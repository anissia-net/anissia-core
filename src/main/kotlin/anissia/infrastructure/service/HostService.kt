package anissia.infrastructure.service

import com.ecwid.consul.v1.health.model.Check
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.cloud.consul.discovery.ConsulServiceInstance
import org.springframework.stereotype.Service
import java.net.URI

@Service
class HostService(
    private val discoveryClient: DiscoveryClient
) {
    fun instanceAll(id: String): List<ServiceInstance> =
        discoveryClient.getInstances(id)
    fun instanceList(id: String): List<ServiceInstance> =
        discoveryClient.getInstances(id)
            .filter { (it as ConsulServiceInstance).healthService.checks.last().status == Check.CheckStatus.PASSING }

    fun instance(id: String): ServiceInstance =
        instanceList(id).random()

    fun uri(id: String): URI =
        instance(id).uri

    fun uri(id: String, path: String): String =
        "${instance(id).uri}$path"
}
