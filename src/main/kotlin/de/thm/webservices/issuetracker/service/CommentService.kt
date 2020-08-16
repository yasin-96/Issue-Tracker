package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.exception.NoContentException
import de.thm.webservices.issuetracker.exception.NotFoundException
import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.repository.CommentRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class CommentService(private val commentRepository: CommentRepository) {

    fun getAllCommentById(issueId: UUID): Flux<CommentModel> {
        return commentRepository.findAllByIssue(issueId)
                .switchIfEmpty(Mono.error(NoContentException("Id in comment for issue was not correct")))
    }

    /**
     * Create new comment for issue
     *
     * @param commentModel comment to create
     * @return
     */
    fun post(commentModel: CommentModel): Mono<CommentModel>{
        return commentRepository.save(commentModel)
                .switchIfEmpty(Mono.error(NoContentException("Could not create new comment for issue")))
    }

    fun getAllComments() : Flux<CommentModel> {
        return commentRepository.findAll()
                .switchIfEmpty(Mono.error(NotFoundException("There are no comments.")))
    }

}