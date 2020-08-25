package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.exception.BadRequestException
import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.service.CommentService
import de.thm.webservices.issuetracker.util.checkNewCommentModel
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController("CommentController")
class CommentController(
        private val commentService: CommentService
) {

    /**
     * Returns the comments based on the Id
     *
     * @param id UUID Id of issue
     * @return Flux<CommentModel>
     */
    @GetMapping("/comment/issue/{id}")
    fun getAllCommentsByIssueId(@PathVariable id: UUID): Flux<CommentModel> {
            return commentService.getAllCommentByIssueId(id)
    }

    /**
     * Create new comment
     *
     * @param commentModel CommentModel? Commente to create
     * @return Mono<CommentModel>
     */
    @PostMapping("/comment")
    fun addNewComment(@RequestBody commentModel: CommentModel): Mono<CommentModel> {
        return Mono.zip(checkNewCommentModel(commentModel), commentService.post(commentModel))
                .switchIfEmpty(Mono.error(BadRequestException()))
                .map { it.t2 }
    }

    /**
     * Deletes a comment in an issue
     *
     * @param cId UUID? Id of comment
     * @param iId UUID? Id of issue
     * @return Mono<Void>
     */
    @DeleteMapping("/comment")
    fun deleteComment(@RequestParam cId: UUID, @RequestParam iId: UUID): Mono<Void> {
        return commentService.deleteComment(cId, iId)
    }
}