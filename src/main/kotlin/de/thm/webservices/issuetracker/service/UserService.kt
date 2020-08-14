package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.model.User
import de.thm.webservices.issuetracker.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class UserService(
        private val userRepository: UserRepository
) {
    fun get(id: UUID): Mono<User> {
        return userRepository.findById(id)
    }

    fun getByUsername(username: String): Mono<User> {
        return userRepository.findByUsername(username)
    }

    fun post(user:User) : Mono<User> {
        return userRepository.save(user)
    }
}
