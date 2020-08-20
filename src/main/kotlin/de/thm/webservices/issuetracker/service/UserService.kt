package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.exception.ForbiddenException
import de.thm.webservices.issuetracker.exception.NotFoundException
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
     * Return user information based on id
     * @param id UUID Id of user
     * @return Mono<UserModel>
     */
    fun get(id: UUID): Mono<UserModel> {
        return securityContextRepository.getAuthenticatedUser()
                .filter { it.hasRightsOrIsAdmin(id) }
                .switchIfEmpty(Mono.error(ForbiddenException()))
                .flatMap {
                    userRepository.findById(id)
                }
    }

    fun getIdFromUsername(name:String) : Mono<UUID>{
        return securityContextRepository.getAuthenticatedUser()
                .filter { it.hasAdminRights() }
                .switchIfEmpty(Mono.error(ForbiddenException()))
                .flatMap {
                    userRepository.findByUsername(name)
                            .map { user -> user.id!!}
                            .switchIfEmpty(Mono.error(NotFoundException("The username you entered is not existing")))
                }
    }




    /**
     * TODO muss raus
     * @return Flux<UserModel>
     */
    fun getAll(): Flux<UserModel> {
        return userRepository.findAll()
    }

    /**
     * Create new user if the current loggedin user has rights
     * @param userModel UserModel Data of new user
     * @return Mono<UserModel>
     */
    fun post(userModel: UserModel): Mono<UserModel> {
        return securityContextRepository.getAuthenticatedUser()
                .filter { it.hasAdminRights() }
                .switchIfEmpty(Mono.error(ForbiddenException()))
                .flatMap {
                    userModel.password = passwordEncoder.encode(userModel.password)
                    userRepository.save(userModel)
                }
    }

    /**
     * Delete one user based on the id
     * @param id UUID Id of user
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
     * Get all written issues and comments from user
     * @param userId UUID Id of user
     * @return Mono<UserViewModel>
     */
    fun getAllDataFromUserId(userId: UUID): Mono<Optional<UserViewModel>> {
        return Mono.zip(
                securityContextRepository.getAuthenticatedUser(),
                issueRepository.findByOwnerId(userId).collectList(),
                commentRepository.findAllByUserId(userId).collectList()
        )
                .filter {
                    it.t1.hasRightsOrIsAdmin(userId)
                }
                .switchIfEmpty(Mono.error(ForbiddenException()))
                .map { Optional.of(UserViewModel(it.t2, it.t3)) }
    }
}
