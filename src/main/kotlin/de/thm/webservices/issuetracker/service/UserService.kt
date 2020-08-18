package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.exception.ForbiddenException
import de.thm.webservices.issuetracker.exception.NoContentException
import de.thm.webservices.issuetracker.exception.NotFoundException
import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.model.UserModel
import de.thm.webservices.issuetracker.model.UserViewModel
import de.thm.webservices.issuetracker.repository.CommentRepository
import de.thm.webservices.issuetracker.repository.IssueRepository
import de.thm.webservices.issuetracker.repository.UserRepository
import de.thm.webservices.issuetracker.security.SecurityContextRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import javax.naming.NotContextException

@Service
class UserService(
        private val userRepository: UserRepository,
        private val commentRepository: CommentRepository,
        private val passwordEncoder: BCryptPasswordEncoder,
        private val issueRepository: IssueRepository,
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
                    commentRepository.findAllByUserId(userId)
                            .switchIfEmpty(Mono.error(NoContentException("There are no comments availiable")))
                }
    }

    fun getAllDataFromUserId(userId: UUID): Mono<UserViewModel> {

        val issueCreatedByUser = issueRepository.findByOwnerId(userId)
                .switchIfEmpty(Mono.error(NotFoundException()))
                .collectList()

        val commentsCreatedByUser = commentRepository.findAllByUserId(userId)
                .switchIfEmpty(Mono.error(NoContentException("There are no comments availiable")))
                .collectList()

        return Mono.zip(issueCreatedByUser, commentsCreatedByUser)
                .switchIfEmpty(Mono.error(NotContextException()))
                .map {
                    UserViewModel(it.t1, it.t2)
                }
    }


}
