package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.exception.BadRequestException
import de.thm.webservices.issuetracker.model.UserView
import de.thm.webservices.issuetracker.service.UserService
import de.thm.webservices.issuetracker.util.checkUUID
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

@RestController("UserViewController")
class UserViewController(
        private val userService: UserService
) {

    @GetMapping("/_view/userdata/{id}")
    fun getUserData(@PathVariable id: UUID?): Mono<UserView> {
        if(checkUUID(id)){
            return userService.getAllDataFromUserId(id!!)
        }
        return Mono.error(BadRequestException())
    }
}