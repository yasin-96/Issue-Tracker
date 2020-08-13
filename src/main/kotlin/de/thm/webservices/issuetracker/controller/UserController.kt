package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.model.User
import de.thm.webservices.issuetracker.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.UUID

@RestController("UserController")
class UserController(private val userService: UserService) {

    /**
     * TODO
     *
     * @param id
     * @return
     */
    @GetMapping("/user/{id}")
    fun get(@PathVariable id: UUID): Mono<User> {
        return userService.get(id)
    }
}
