package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.repository.CommentRepository
import org.springframework.stereotype.Service

@Service
class CommentService(private val commentRepository: CommentRepository) {
}