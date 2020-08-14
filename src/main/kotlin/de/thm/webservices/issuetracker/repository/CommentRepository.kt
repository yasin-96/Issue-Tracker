package de.thm.webservices.issuetracker.repository

import de.thm.webservices.issuetracker.model.CommentModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CommentRepository: ReactiveCrudRepository<CommentModel, UUID> {
}