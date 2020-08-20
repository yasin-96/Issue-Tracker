package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.exception.NotFoundException
import de.thm.webservices.issuetracker.model.UserViewModel
import de.thm.webservices.issuetracker.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

@RestController("UserViewController")
class UserViewController(
        private val userService: UserService
) {

    /**
     * Gets all the data created by the user.
     * A separate list for the issues and comments
     *
     * @param id UUID? Id of User
     * @return Mono<Optional<UserViewModel>> Fetched data
     */
    @GetMapping("/_view/userdata/{id}")
    fun getUserData(@PathVariable id: UUID): Mono<Optional<UserViewModel>> {
            return userService.getAllDataFromUserId(id)
                    .switchIfEmpty(Mono.error(NotFoundException()))
    }
}