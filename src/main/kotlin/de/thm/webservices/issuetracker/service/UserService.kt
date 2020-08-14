package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.model.UserModel
import de.thm.webservices.issuetracker.repository.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class UserService(
        private val userRepository: UserRepository
) {
    fun get(id: UUID): Mono<UserModel> {
        return userRepository.findById(id)
    }

    fun getByUsername(username: String): Mono<UserModel> {
        return userRepository.findByUsername(username)
    }

    fun post(userModel:UserModel) : Mono<UserModel> {
        return userRepository.save(userModel)
    }
}
