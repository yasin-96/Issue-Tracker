package de.thm.webservices.issuetracker.security

import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class SecurityContextRepository(
        private val authenticationManager: AuthenticationManager
) : ServerSecurityContextRepository {
    companion object {
        val TOKEN_PREFIX = "Bearer "
    }

    override fun save(swe: ServerWebExchange?, sc: SecurityContext?): Mono<Void?>? {
        throw UnsupportedOperationException("Not supported yet.")
    }

    override fun load(swe: ServerWebExchange): Mono<SecurityContext> {
        val authHeader = swe.request.headers.getFirst(HttpHeaders.AUTHORIZATION)?: ""
        return Mono.just(authHeader)
                // filter for Bearer Authorization Header
                .filter { potentialHeader: String ->
                    potentialHeader.startsWith(TOKEN_PREFIX)
                }
                // remove the Bearer Prefix
                .map { bearerHeader: String ->
                    bearerHeader.replace(TOKEN_PREFIX, "")
                }
                // create Authentication object
                .map { token: String ->
                    UsernamePasswordAuthenticationToken(token, token)
                }
                // authenticate (a.k.a. verify token)
                .flatMap { authentication: Authentication ->
                    authenticationManager.authenticate(authentication)
                }
                // create SecurityContext from authenticated Authentication
                .map { authentication: Authentication ->
                    SecurityContextImpl(authentication)
                }
    }

    fun getAuthenticatedUser(): Mono<AuthenticatedUser> {
        return ReactiveSecurityContextHolder.getContext()
                .map { securityContext ->
                    securityContext.authentication
                }
                .cast(AuthenticatedUser::class.java)
    }
}