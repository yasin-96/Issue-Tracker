package de.thm.webservices.issuetracker.repository

import de.thm.webservices.issuetracker.model.CommentModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface CommentRepository: ReactiveCrudRepository<CommentModel, UUID> {
    fun findAllByIssue(issueId: UUID): Flux<CommentModel>

    fun findAllByUser(userId: UUID): Flux<CommentModel>
}