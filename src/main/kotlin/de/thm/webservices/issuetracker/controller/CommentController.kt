package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.exception.BadRequestException
import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.service.CommentService
import de.thm.webservices.issuetracker.util.checkNewCommentModel
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
    fun addNewComment(@RequestBody commentModel: CommentModel?): Mono<CommentModel> {

        //TODO
        if(checkNewCommentModel(commentModel)){
            return commentService.post(commentModel!!)
        }
        return Mono.error(BadRequestException())
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

    /**
     * TODO raus vor der abgabe
     * Only for testing
     * @param id UUID? Id of comment
     * @return Mono<CommentModel>
     */
    @GetMapping("/comment/{id}")
    fun getOneComment(@PathVariable id: UUID): Mono<CommentModel> {
            return commentService.getCommentById(id)
    }

    /**
     * TODO raus vor der abgabe
     * Only for testing
     * @return Flux<CommentModel>
     */
    @GetMapping("/comment/allcomments")
    fun getAllComment(): Flux<CommentModel> {
        return commentService.getAll()
    }
}