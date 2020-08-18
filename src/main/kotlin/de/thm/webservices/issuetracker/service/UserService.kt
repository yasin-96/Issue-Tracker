package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.exception.ForbiddenException
import de.thm.webservices.issuetracker.exception.NotFoundException
import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.model.IssueModel
import de.thm.webservices.issuetracker.model.UserModel
import de.thm.webservices.issuetracker.model.UserView
import de.thm.webservices.issuetracker.repository.UserRepository
import de.thm.webservices.issuetracker.security.AuthenticatedUser
import de.thm.webservices.issuetracker.security.SecurityContextRepository
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import javax.naming.NotContextException

@Service
class UserService(
        private val userRepository: UserRepository,
        private val commentService: CommentService,
        private val passwordEncoder: BCryptPasswordEncoder,
        private val issueService: IssueService,
        private val securityContextRepository: SecurityContextRepository
) {

    fun getCurrentUserRole() : Mono<String> {
        return securityContextRepository.getAuthenticatedUser()
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
        return securityContextRepository.getAuthenticatedUser()
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

    fun getAllDataFromUserId(userId: UUID): Mono<UserView> {

        val issueCreatedByUser = issueService.getByOwnerId(userId).collectList()
        val commentsCreatedByUser = commentService.getAllCommentsByUserId(userId).collectList()
        var ud = UserView()
        return Mono.zip(issueCreatedByUser, commentsCreatedByUser)
                .switchIfEmpty(Mono.error(NotContextException()))
                .map {
                    UserView(it.t1, it.t2)
                }
    }


}
