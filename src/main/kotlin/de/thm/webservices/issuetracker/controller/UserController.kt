package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.exception.ForbiddenException
import de.thm.webservices.issuetracker.model.UserModel
import de.thm.webservices.issuetracker.service.UserService
import org.springframework.web.bind.annotation.*
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
    fun get(@PathVariable id: UUID): Mono<UserModel> {
        return userService.get(id)
    }

    @PostMapping("/user")
    fun post(@RequestBody userModel:UserModel ) : Mono<UserModel>{
        return if (userModel.role == "admin") {
            userService.post(userModel)
        }

        else {
            Mono.error(ForbiddenException())
        }
        }
}
