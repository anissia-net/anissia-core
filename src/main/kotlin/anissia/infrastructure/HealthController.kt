package anissia.infrastructure

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class HealthController {
    @GetMapping("/actuator/health")
    fun health(): Mono<String> = Mono.just("ok")
}
