package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.exception.BadRequestException
import de.thm.webservices.issuetracker.exception.NoContentException
import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.service.CommentService
import de.thm.webservices.issuetracker.util.checkUUID
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController("CommentController")
class CommentController(
        private val commentService: CommentService
) {

    @GetMapping("/issue/comment/{id}")
    fun getAllCommentFromIssueById(@PathVariable id: UUID?): Flux<CommentModel> {

        if(checkUUID(id)) {
            return commentService.getAllCommentById(id!!)
                    .switchIfEmpty(Mono.error(NoContentException("No comments found for this issue")))
        }

        return Flux.from(Mono.error(BadRequestException("Wrong id ")))
    }
}