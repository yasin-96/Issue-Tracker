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

@Service
class UserService(
        private val userRepository: UserRepository,
        private val commentRepository: CommentRepository,
        private val passwordEncoder: BCryptPasswordEncoder,
        private val issueRepository: IssueRepository,
        private val securityContextRepository: SecurityContextRepository
) {

    /**
     * TODO
     * @return Mono<String>
     */
    fun checkIfUserIsAdmin(): Mono<String> {
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


    /**
     * TODO
     * @param id UUID
     * @return Mono<UserModel>
     */
    fun get(id: UUID): Mono<UserModel> {
        return userRepository.findById(id)
    }

    /**
     * TODO
     * @return Flux<UserModel>
     */
    fun getAll(): Flux<UserModel> {
        return userRepository.findAll()
    }

    /**
     * TODO
     * @param username String
     * @return Mono<UserModel>
     */
    fun getByUsername(username: String): Mono<UserModel> {
        return userRepository.findByUsername(username)
    }

    /**
     * TODO
     * @param userModel UserModel
     * @return Mono<UserModel>
     */
    fun post(userModel: UserModel): Mono<UserModel> {
        userModel.password = passwordEncoder.encode(userModel.password)
        return userRepository.save(userModel)
    }

    /**
     * TODO
     * @param id UUID
     * @return Mono<Void>
     */
    fun delete(id: UUID): Mono<Void> {
        return securityContextRepository.getAuthenticatedUser()
                .filter { it.hasRightsOrIsAdmin(id) }
                .switchIfEmpty(Mono.error(ForbiddenException("User can only delete his own account")))
                .flatMap {
                    userRepository.deleteById(id)
                }
    }

    /**
     * TODO
     * @param userId UUID
     * @return Flux<CommentModel>
     */
    fun getAllCommentsByUserId(userId: UUID): Flux<CommentModel> {
        return get(userId)
                .switchIfEmpty(Mono.error(NotFoundException("User not exist")))
                .flatMapMany {
                    commentRepository.findAllByUserId(userId)
                            .switchIfEmpty(Mono.error(NoContentException("There are no comments availiable")))
                }
    }

    /**
     * TODO
     * @param userId UUID
     * @return Mono<UserViewModel>
     */
    fun getAllDataFromUserId(userId: UUID): Mono<Optional<UserViewModel>> {

        val issueCreatedByUser = issueRepository.findByOwnerId(userId)
                .collectList()

        val commentsCreatedByUser = commentRepository.findAllByUserId(userId)
                .collectList()

        return Mono.zip(issueCreatedByUser, commentsCreatedByUser)
                .map {
                    Optional.of(UserViewModel(it.t1, it.t2))
                }
    }


}
