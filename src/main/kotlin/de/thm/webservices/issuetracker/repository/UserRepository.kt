package de.thm.webservices.issuetracker.repository

import de.thm.webservices.issuetracker.model.UserModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
interface UserRepository : ReactiveCrudRepository<UserModel, UUID> {
    fun findByUsername(username: String): Mono<UserModel>
}
