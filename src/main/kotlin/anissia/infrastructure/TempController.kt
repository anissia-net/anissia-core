//package anissia.infrastructure
//
//import anissia.infrastructure.service.HostService
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.RestController
//import org.springframework.web.client.RestTemplate
//import org.springframework.web.reactive.function.client.WebClient
//
//
//
//@RestController
//class TempController(
//    private val restTemplate: RestTemplate,
//    private val webClient: WebClient.Builder,
//    private val hostService: HostService
//) {
//
//    @GetMapping("/temp")
//    fun temp() =
//        hostService.instanceAll("anissia-email")
//
//    @GetMapping("/temp2")
//    fun temp2() = restTemplate.getForObject(hostService.uri("anissia-email", "/actuator/health"), String::class.java)
//
//    @GetMapping("/temp3")
//    fun temp3() = webClient.build().get().uri(hostService.uri("anissia-email", "/actuator/health"))
//        .retrieve().bodyToMono(String::class.java).block()?: "FAIL"
//
//    @GetMapping("/temp4")
//    fun temp4() =
//        hostService.instance("anissia-email")
//}
//
