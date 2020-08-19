package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.exception.BadRequestException
import de.thm.webservices.issuetracker.exception.NoContentException
import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.service.CommentService
import de.thm.webservices.issuetracker.util.checkMultiplyRequestParamForDeletingComment
import de.thm.webservices.issuetracker.util.checkUUID
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
     * @param id UUID? Id of issue
     * @return Flux<CommentModel>
     */
    @GetMapping("/comments/issue/{id}")
    fun getAllCommentsByIssueId(@PathVariable id: UUID?): Flux<CommentModel> {

        if (checkUUID(id)) {
            return commentService.getAllCommentByIssueId(id!!)
                    //TODO NoContent or NoFound
                    .switchIfEmpty(Mono.error(NoContentException("No comments found for this issue")))
        }
        return Flux.from(Mono.error(BadRequestException("Wrong id ")))
    }

    /**
     * Create new comment
     *
     * @param commentModel CommentModel? Commente to create
     * @return Mono<CommentModel>
     */
    @PostMapping("/comment")
    fun addNewComment(@RequestBody commentModel: CommentModel?): Mono<CommentModel> {
        //TODO wollen wir hier das noch gegen prüfen? also auf das Model selbst
        return commentService.post(commentModel!!)
    }

    /**
     * Deletes a comment in an issue
     *
     * @param cId UUID? Id of comment
     * @param iId UUID? Id of issue
     * @return Mono<Void>
     */
    @DeleteMapping("/comment")
    fun deleteComment(@RequestParam cId: UUID?, @RequestParam iId: UUID?): Mono<Void> {
        if (checkMultiplyRequestParamForDeletingComment(cId, iId)) {
            return commentService.deleteComment(cId!!, iId!!)
        }
        return Mono.error(BadRequestException())

    }

    //TODO löschen??
    @GetMapping("/comment/{id}")
    fun getOneComment(@PathVariable id: UUID?): Mono<CommentModel> {
        if (checkUUID(id)) {
            return commentService.getCommentById(id!!)
        }
        return Mono.error(BadRequestException())
    }

    /**
     * TODO löschen ??
     * @return Flux<CommentModel>
     */
    @GetMapping("/comment/allcomments")
    fun getAllComment(): Flux<CommentModel> {
        return commentService.getAll()
    }
}