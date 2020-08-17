package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.exception.*
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
                    commentModel.user = authUser.name
                    commentRepository.save(commentModel)
                            .switchIfEmpty(Mono.error(NoContentException("Could not create new comment for issue")))
                }
    }

    /**
     *
     *
     * @param commentModel
     * @return
     */
    fun deleteComment(commentId: UUID, issueId: UUID): Mono<Void> {
        return ReactiveSecurityContextHolder.getContext()
                .map { securityContext ->
                    securityContext.authentication
                }
                .cast(AuthenticatedUser::class.java)
                .map { authUser ->
                    val issue = issueService.checkCurrentUserIsOwnerOfIssue(authUser.name, issueId)
                    val comment = checkCurrentUserIsOwnerOfComment(authUser.name, commentId)

                    Mono.zip(issue, comment)
                            .filter {
                                if(it.t1 && !it.t2 ||      // owner of issue but no from comment
                                    it.t1 && it.t2 ||      // owner of issue and comment
                                    !it.t1 && it.t2        // not owner of issue but from comment
                                ) {
                                    true
                                }
                                //no rights!!
                                false
                            }
                            .switchIfEmpty(Mono.error(ForbiddenException()))
                            .map { it }
                }
                .
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

}