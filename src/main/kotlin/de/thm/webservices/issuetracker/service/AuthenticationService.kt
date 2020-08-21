package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.exception.NotFoundException
import de.thm.webservices.issuetracker.model.UserModel
import de.thm.webservices.issuetracker.repository.UserRepository
import de.thm.webservices.issuetracker.security.JwtUtil
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthenticationService(
        private val userRepository: UserRepository,
        private val jwtUtil: JwtUtil,
        private val passwordEncoder: BCryptPasswordEncoder
) {

    fun login(username: String, password: String): Mono<String> {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(NotFoundException("Username does not exist")))
                .flatMap { userModel: UserModel ->
                    if (passwordEncoder.matches(password, userModel.password)) {
                    //if (password == user.password) {
                        Mono.just(jwtUtil.generateToken(userModel))
                    } else {
                        Mono.empty()
                    }
                }
    }


}
