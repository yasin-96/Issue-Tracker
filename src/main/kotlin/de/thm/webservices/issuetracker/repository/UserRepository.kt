package de.thm.webservices.issuetracker.repository

import de.thm.webservices.issuetracker.model.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono
import java.util.UUID

interface UserRepository : ReactiveCrudRepository<User, UUID> {
    fun findByUsername(username: String): Mono<User>
}
