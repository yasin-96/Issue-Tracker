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

    @GetMapping("/comments/issue/{id}")
    fun getAllCommentFromIssueById(@PathVariable id: UUID?): Flux<CommentModel> {

        if (checkUUID(id)) {
            return commentService.getAllCommentByIssueId(id!!)
                    .switchIfEmpty(Mono.error(NoContentException("No comments found for this issue")))
        }

        return Flux.from(Mono.error(BadRequestException("Wrong id ")))
    }

    @PostMapping("/comment")
    fun addNewComment(@RequestBody commentModel: CommentModel?): Mono<CommentModel> {
        return commentService.post(commentModel!!)
    }

    @DeleteMapping("/comment")
    fun deleteComment(
            @RequestParam cId: UUID?,
            @RequestParam iId: UUID?
    ): Mono<Void> {
        if (checkMultiplyRequestParamForDeletingComment(cId, iId)) {
            return commentService.deleteComment(cId!!, iId!!)
        }
        return Mono.error(BadRequestException())

    }

    @GetMapping("/comment/{id}")
    fun getOneComment(@PathVariable id: UUID?): Mono<CommentModel> {
        if(checkUUID(id)){
            return commentService.getCommentById(id!!)
        }
        return Mono.error(BadRequestException())
    }

    @GetMapping("/comment/allcomments")
    fun getAllComment(): Flux<CommentModel> {
        return commentService.getAll()
    }

}