package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.service.AuthenticationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController("AuthController")
class AuthController(
        private val authenticationService: AuthenticationService
) {

    /**
     * TODO
     *
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/auth/login")
    fun login(
            @RequestParam username: String,
            @RequestParam password: String
    ): Mono<String> {
        return authenticationService.login(username, password)
    }
}
