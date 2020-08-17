package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.exception.BadRequestException
import de.thm.webservices.issuetracker.exception.ForbiddenException
import de.thm.webservices.issuetracker.exception.NoContentException
import de.thm.webservices.issuetracker.model.UserModel
import de.thm.webservices.issuetracker.service.UserService
import de.thm.webservices.issuetracker.util.checkUUID
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID



@RestController("UserController")
class UserController(private val userService: UserService,
                     private val passwordEncoder: BCryptPasswordEncoder) {

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

    @GetMapping("/user/all")
    fun getAll(): Flux<UserModel> {
        return userService.getAll()
    }

    @PostMapping("/user")
    fun post(@RequestBody userModel: UserModel) : Mono<UserModel>{
        return userService.getCurrentUserRole()
                .switchIfEmpty(Mono.error(BadRequestException()))
                .flatMap {
                    userService.post(userModel)
                            .switchIfEmpty(Mono.error(NoContentException("User could not be created")))
                }
    }


    @DeleteMapping("/user/{id}")
    fun delete(
            @PathVariable id: UUID
    ): Mono<Void> {
        if(checkUUID(id)) {
            return userService.delete(id)
        }
        return Mono.error(NoContentException("Wrong id was sending. ID is not an UUIDv4"))
    }


    @GetMapping("/user/role")
    fun getRole(): Mono<String> {
        return userService.getCurrentUserRole()
    }




}
