package anissia.infrastructure

import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@Profile("dev", "local")
@RestController("/dev")
class DevController(

) {

    @GetMapping("/install")
    fun install(): String {



        return "done";
    }
}

