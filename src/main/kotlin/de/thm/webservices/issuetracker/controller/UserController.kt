package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.exception.BadRequestException
import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.model.UserModel
import de.thm.webservices.issuetracker.service.CommentService
import de.thm.webservices.issuetracker.service.UserService
import de.thm.webservices.issuetracker.util.checkNewUserModel
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
     * Get the user information according to the id
     *
     * @param id UUID Id of User
     * @return Mono<UserModel>
     */
    @GetMapping("/user/{id}")
    fun get(@PathVariable id: UUID): Mono<UserModel> {
            return userService.get(id)
                    .switchIfEmpty(Mono.error(BadRequestException("Wrong id was sending. ID is not an UUIDv4")))
    }

    /**
     * TODO raus for abgabe
     * Only for testing
     * @return Flux<UserModel>
     */
    @GetMapping("/user/all")
    fun getAll(): Flux<UserModel> {
        return userService.getAll()
    }

    /**
     * Creates a new user
     *
     * @param userModel UserModel New user to create
     * @return Mono<UserModel>
     */
    @PostMapping("/user")
    fun post(@RequestBody userModel: UserModel): Mono<UserModel> {
        return Mono.zip(checkNewUserModel(userModel), userService.post(userModel))
                .filter { it.t1 }
                .switchIfEmpty(Mono.error(BadRequestException()))
                .map { it.t2 }
    }

    /**
     * Delete one user by id
     * @param id UUID Id of User
     * @return Mono<Void>
     */
    @DeleteMapping("/user/{id}")
    fun delete(@PathVariable id: UUID): Mono<Void> {
            return userService.delete(id)
    }

    /**
     * Fetches all written comments of a user according to the id
     *
     * @param userId UUID? Id of User
     * @return Flux<CommentModel>
     */
    @GetMapping("/user/comments/{userId}")
    fun getAllCommentsOfAnUser(@PathVariable userId: UUID): Flux<CommentModel> {
            return commentService.getAllCommentsByUserId(userId)
    }
}
