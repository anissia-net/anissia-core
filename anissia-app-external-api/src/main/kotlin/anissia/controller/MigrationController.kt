package anissia.controller

import anissia.services.MigrationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MigrationController(
    val migrationService: MigrationService
) {

    @GetMapping("/mig")
    fun mig() = migrationService.migration().run { "OK" }
}