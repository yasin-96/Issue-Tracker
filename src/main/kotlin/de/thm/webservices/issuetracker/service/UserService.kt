package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.exception.NoContentException
import de.thm.webservices.issuetracker.model.UserModel
import de.thm.webservices.issuetracker.repository.UserRepository
import de.thm.webservices.issuetracker.security.AuthenticatedUser
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class UserService(
        private val userRepository: UserRepository
) {
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
                .switchIfEmpty(Mono.error(Throwable("Wrong ID")))
                .flatMap {
                    userRepository.deleteById(id)
                }
    }
}
