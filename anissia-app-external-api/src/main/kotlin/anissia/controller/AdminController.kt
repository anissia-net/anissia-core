package anissia.controller

import anissia.services.AdminService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val adminService: AdminService
) {

    // - anime

    @GetMapping("/caption/list/{active}/{page}")
    fun getCaptionList(@PathVariable active: Int, @PathVariable page: Int) = adminService.getCaptionList(active, page)

}