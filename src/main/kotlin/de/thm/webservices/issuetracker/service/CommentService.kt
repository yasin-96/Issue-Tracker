package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.exception.*
import de.thm.webservices.issuetracker.exception.NoContentException
import de.thm.webservices.issuetracker.exception.NotFoundException
import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.model.event.CreateNewComment
import de.thm.webservices.issuetracker.repository.CommentRepository
import de.thm.webservices.issuetracker.repository.IssueRepository
import de.thm.webservices.issuetracker.security.SecurityContextRepository
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class CommentService(
        private val commentRepository: CommentRepository,
        private val issueRepository: IssueRepository,
        private val securityContextRepository: SecurityContextRepository,
        private val taggingService: TaggingService,
        private val commentTemplate: RabbitTemplate
) {

    private val topicPath = "amq.topic"
    private val postFixNews = ".news"

    /**
     * Request all comments by issue id
     *
     * @param issueId UUID Id of issue
     * @return Flux<CommentModel>
     */
    fun getAllCommentByIssueId(issueId: UUID): Flux<CommentModel> {
        return commentRepository.findAllByIssueId(issueId)
                .switchIfEmpty(Mono.error(NotFoundException("Id in comment for issue was not correct")))
    }

    /**
     * Create new comment for issue
     *
     * @param commentModel CommentModel Comment to create
     * @return Mono<CommentModel>
     */
    fun post(commentModel: CommentModel): Mono<CommentModel> {
        return securityContextRepository.getAuthenticatedUser()
                //check rights
                .filter { it.hasRightsOrIsAdmin(commentModel.userId) }
                .switchIfEmpty(Mono.error(ForbiddenException()))

                //check issue exist
                .zipWith(issueRepository.existsById(commentModel.issueId))
                .filter { it.t2 }
                .switchIfEmpty(Mono.error(NotFoundException("Issue with this is doesn't exist")))

                // save comment and send stomp message
                .flatMap { commentRepository.save(commentModel) }
                .switchIfEmpty(Mono.error(NoContentException("Could not create new comment for issue")))
                .zipWith(taggingService.tagging(commentModel.content))
                .doOnSuccess { tupleCT ->
                    tupleCT.t2.map { uuid ->
                        commentTemplate.convertAndSend(
                                topicPath,
                                uuid.toString() + postFixNews,
                                CreateNewComment(tupleCT.t1.issueId)
                        )
                    }
                }
                .map { it.t1 }
    }

    /**
     * Delete only comment if current user is owner of issue or owner of comment
     *
     * @param commentId UUID Id of Comment
     * @param issueId UUID Id of Issue
     * @return Mono<Void>
     */
    fun deleteComment(commentId: UUID, issueId: UUID): Mono<Void> {
        return Mono.zip(
                securityContextRepository.getAuthenticatedUser(),
                issueRepository.findById(issueId),
                commentRepository.findById(commentId)
        )
                .filter {
                    it.t1.hasRightsOrIsAdmin(it.t2.ownerId) ||
                            it.t1.hasRightsOrIsAdmin(it.t3.userId)
                }
                .switchIfEmpty(Mono.error(ForbiddenException()))
                .flatMap { commentRepository.deleteById(commentId) }
    }


    /**
     * Returns all comments written from user, searched by id
     * @param userId UUID
     * @return Flux<CommentModel>
     */
    fun getAllCommentsByUserId(userId: UUID): Flux<CommentModel> {
        return securityContextRepository.getAuthenticatedUser()
                .filter{it.hasRightsOrIsAdmin(userId)}
                .switchIfEmpty(Mono.error(ForbiddenException()))
                .flatMapMany {
                    commentRepository.findAllByUserId(userId)
                            .switchIfEmpty(Mono.error(NotFoundException("User has no comments written")))
                }
    }

    /**
     * Returns all comments written from user, searched by id
     * @param userId UUID
     * @return Flux<CommentModel>
     */
    fun getAllCommentsByUserIdForStats(userId: UUID): Flux<CommentModel> {
        return commentRepository.findAllByUserId(userId)
    }

    /**
     * TODO muss raus
     * @return Flux<CommentModel>
     */
    fun getAll(): Flux<CommentModel> {
        return commentRepository.findAll()
    }

}