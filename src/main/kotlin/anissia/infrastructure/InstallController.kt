package anissia.infrastructure

import org.springframework.context.annotation.Profile
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


/**
 * 이 컨트롤러는 개발환경에서만 사용할 수 있다.
 * 설치 전용 컨트롤러로 컨트롤러에 로직이 포함된다.
 */
@Profile("dev", "local")
@RestController
class InstallController(

) {
    @Transactional
    @GetMapping("/install")
    fun install(): String {



        return "done";
    }
}

