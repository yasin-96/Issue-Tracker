package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.exception.ForbiddenException
import de.thm.webservices.issuetracker.model.User
import de.thm.webservices.issuetracker.service.UserService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.UUID

@RestController("UserController")
class UserController(
        private val userService: UserService
) {
    @GetMapping("/user/{id}")
    fun get(
            @PathVariable id: UUID
    ): Mono<User> {
        return userService.get(id)
    }

    @PostMapping("/user")
    fun post(@RequestBody user:User ) : Mono<User>{
        return if (user.role == "admin") {
            userService.post(user)
        }

        else {
            Mono.error(ForbiddenException())
        }
        }
}
