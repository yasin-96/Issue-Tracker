package de.thm.webservices.issuetracker.security

import io.jsonwebtoken.Claims
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.stream.Collectors

@Component
class AuthenticationManager(
        private val jwtUtil: JwtUtil
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.just(authentication)
                // get token from Authentication
                .map { authentication ->
                    authentication.credentials.toString()
                }
                // check if token is still valid
                // this also throws an error when the token can't be read
                .filter { token: String ->
                    !jwtUtil.isTokenExpired(token)
                }
                // catch error and fall back to an empty Mono for error handling in API
                .onErrorResume { Mono.empty() }
                // if no error occured, load the user object
                .map { token: String ->
                    val userId: String = jwtUtil.getUsernameFromToken(token)?: ""
                    val claims: Claims = jwtUtil.getAllClaimsFromToken(token)
                    val roles: List<String> = claims.get<MutableList<*>>(JwtUtil.CLAIM_KEY, MutableList::class.java) as List<String>
                    val authorities = roles.stream().map { role: String? -> SimpleGrantedAuthority(role) }.collect(Collectors.toList())
                    val auth = AuthenticatedUser(userId, authorities)
                    auth
                }
    }
}
