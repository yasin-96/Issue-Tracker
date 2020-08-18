package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.exception.BadRequestException
import de.thm.webservices.issuetracker.exception.NoContentException
import de.thm.webservices.issuetracker.exception.NotFoundException
import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.model.UserModel
import de.thm.webservices.issuetracker.service.CommentService
import de.thm.webservices.issuetracker.service.UserService
import de.thm.webservices.issuetracker.util.checkUUID
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID


@RestController("UserController")
class UserController(
        private val userService: UserService,
        private val commentService: CommentService
) {

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

    /**
     * TODO
     * @return Flux<UserModel>
     */
    @GetMapping("/user/all")
    fun getAll(): Flux<UserModel> {
        return userService.getAll()
    }

    /**
     * TODO
     * @param userModel UserModel
     * @return Mono<UserModel>
     */
    @PostMapping("/user")
    fun post(@RequestBody userModel: UserModel): Mono<UserModel> {
        return userService.getCurrentUserRole()
                .switchIfEmpty(Mono.error(BadRequestException()))
                .flatMap {
                    userService.post(userModel)
                            .switchIfEmpty(Mono.error(NoContentException("User could not be created")))
                }
    }


    /**
     * TODO
     * @param id UUID
     * @return Mono<Void>
     */
    @DeleteMapping("/user/{id}")
    fun delete(
            @PathVariable id: UUID
    ): Mono<Void> {
        if (checkUUID(id)) {
            return userService.delete(id)
        }
        return Mono.error(NoContentException("Wrong id was sending. ID is not an UUIDv4"))
    }


    /**
     * TODO
     * @return Mono<String>
     */
    @GetMapping("/user/role")
    fun getRole(): Mono<String> {
        return userService.getCurrentUserRole()
    }


    /**
     * TODO
     *
     * @param userId Id of user
     * @return
     */
    @GetMapping("/user/comments/{userId}")
    fun getAllCommentsOfAnUser(@PathVariable userId: UUID?): Flux<CommentModel> {
        if (checkUUID(userId!!)) {
            return commentService.getAllCommentsByUserId(userId)
        }
        return Flux.from(Mono.error(NotFoundException("That user id is not existing")))
    }
}
