package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.exception.ForbiddenException
import de.thm.webservices.issuetracker.exception.NotFoundException
import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.model.IssueModel
import de.thm.webservices.issuetracker.model.UserModel
import de.thm.webservices.issuetracker.model.UserView
import de.thm.webservices.issuetracker.repository.UserRepository
import de.thm.webservices.issuetracker.security.AuthenticatedUser
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import reactor.kotlin.core.publisher.toMono
import javax.naming.NotContextException

@Service
class UserService(
        private val userRepository: UserRepository,
        private val commentService: CommentService,
        private val issueService: IssueService,
        private val passwordEncoder: BCryptPasswordEncoder
) {

    fun getCurrentUserRole() : Mono<String> {
        return ReactiveSecurityContextHolder.getContext()
                .map { securityContext ->
                    securityContext.authentication
                }
                .cast(AuthenticatedUser::class.java)
                .filter { authenticatedUser ->
                    authenticatedUser.authorities.all {
                        it!!.authority == "ADMIN"
                    }
                }
                .switchIfEmpty(Mono.error(ForbiddenException()))
                .map { authenticatedUser ->
                    authenticatedUser.authorities.toString()
                }

    }


    fun get(id: UUID): Mono<UserModel> {
        return userRepository.findById(id)
    }

    fun getAll(): Flux<UserModel> {
        return userRepository.findAll()
    }

    fun getByUsername(username: String): Mono<UserModel> {
        return userRepository.findByUsername(username)
    }

    fun post(userModel: UserModel): Mono<UserModel> {
        userModel.password = passwordEncoder.encode(userModel.password)
        return userRepository.save(userModel)
    }

    fun delete(id: UUID): Mono<Void> {
        return ReactiveSecurityContextHolder.getContext()
                .map { securityContext ->
                    securityContext.authentication
                }
                .cast(AuthenticatedUser::class.java)
                .filter { authenticatedUser ->
                    authenticatedUser.name == id.toString()
                }
                .switchIfEmpty(Mono.error(ForbiddenException("User can only delete his own account")))
                .flatMap {
                    userRepository.deleteById(id)
                }
    }

    fun getAllCommentsByUserId(userId: UUID) : Flux<CommentModel> {
        return get(userId)
                .switchIfEmpty(Mono.error(NotFoundException("User not exist")))
                .flatMapMany {
                    commentService.getAllCommentsByUserId(userId)
                            .switchIfEmpty(Mono.error(NotFoundException("There are no comments availiable")))
                }
    }

    fun getAllDataFromUser(userId: UUID): Flux<UserView> {

        val issueCreatedByUser = issueService.getByOwnerId(userId)
        val commentsCreatedByUser = commentService.getAllCommentsByUserId(userId)
        var ud = UserView()
        return Flux.zip(issueCreatedByUser, commentsCreatedByUser)
                .switchIfEmpty(Mono.error(NotContextException()))
                .map{
                    ud.issues.add(it.t1)
                    ud.comments.add(it.t2)
                    ud
                }
    }
}
