package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.exception.*
import de.thm.webservices.issuetracker.exception.NoContentException
import de.thm.webservices.issuetracker.exception.NotFoundException
import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.repository.CommentRepository
import de.thm.webservices.issuetracker.security.AuthenticatedUser
import org.springframework.data.util.CastUtils.cast
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.extra.bool.logicalAnd
import reactor.kotlin.extra.bool.logicalOr
import java.security.PrivateKey
import java.util.*

@Service
class CommentService(
        private val commentRepository: CommentRepository,
        private val issueService: IssueService
) {

    fun getCommentById(commentId: UUID): Mono<CommentModel> {
        return commentRepository.findById(commentId)
                .switchIfEmpty(Mono.error(NotFoundException("Id not found")))
    }


    /**
     * TODO
     *
     * @param issueId
     * @return
     */
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
    fun post(commentModel: CommentModel): Mono<CommentModel> {
        return ReactiveSecurityContextHolder.getContext()
                .map { securityContext ->
                    securityContext.authentication
                }
                .cast(AuthenticatedUser::class.java)
                .flatMap { authUser ->
                    //TODO: würde das nicht genau das abfangen Ticket -> #24
//                    commentModel.user = authUser.name
                    commentRepository.save(commentModel)
                            .switchIfEmpty(Mono.error(NoContentException("Could not create new comment for issue")))
                }
    }

    /**
     * Delete only comment if current user is owner of issue or owner of comment
     *
     * @param commentId Id of Comment
     * @param issueId Id of Issue
     * @return HttpStatus Code if worked 200OK, else 401
     */
    fun deleteComment(commentId: UUID, issueId: UUID): Mono<Void> {
        return ReactiveSecurityContextHolder.getContext()
                .map { securityContext ->
                    securityContext.authentication
                }
                .cast(AuthenticatedUser::class.java)
                .flatMap { authUser ->
                    val ownerOfIssue = issueService.checkCurrentUserIsOwnerOfIssue(authUser.name, issueId)
                    val ownerOfComment = checkCurrentUserIsOwnerOfComment(authUser.name, commentId)
                    Mono.zip(ownerOfIssue, ownerOfComment)
                            .filter {
                                it.t1 || it.t2
                            }
                            .switchIfEmpty(Mono.error(ForbiddenException("No rights to delete the comment")))
                }
                .switchIfEmpty(Mono.error(BadRequestException()))
                .flatMap {
                        removeCommentById(commentId)
                }
    }

    /**
     * TODO
     *
     * @param currentUser
     * @param commentId
     * @return
     */
    fun checkCurrentUserIsOwnerOfComment(currentUser: String, commentId: UUID): Mono<Boolean> {
        return commentRepository.findById(commentId)
                .switchIfEmpty(Mono.error(NotFoundException("Issue id was not found")))
                .map {
                    var check = if (it.user == currentUser) true else false
                    check
                }
    }

    /**
     * TODO
     *
     * @param commentId
     * @return
     */
    fun removeCommentById(commentId: UUID): Mono<Void> {
        return getCommentById(commentId)
                .switchIfEmpty(Mono.error(NotFoundException("Id not found. Comment was not removed")))
                .flatMap {
                    commentRepository.delete(it)
                }
    }

    fun getAllComments() : Flux<CommentModel> {
        return commentRepository.findAll()
                .switchIfEmpty(Mono.error(NotFoundException("There are no comments.")))
    }

}