package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.exception.ForbiddenException
import de.thm.webservices.issuetracker.exception.NotFoundException
import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.model.IssueModel
import de.thm.webservices.issuetracker.model.StatsModel
import de.thm.webservices.issuetracker.repository.CommentRepository
import de.thm.webservices.issuetracker.repository.IssueRepository
import de.thm.webservices.issuetracker.repository.UserRepository
import de.thm.webservices.issuetracker.security.SecurityContextRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class StatisticService(
        private val commentRepository: CommentRepository,
        private val issueRepository: IssueRepository,
        private val securityContextRepository: SecurityContextRepository
) {


    /**
     * Returns all issues based  on user id
     * @param ownerId UUID Id of owner
     * @return Flux<IssueModel>
     */
    fun getAllIssuesFromOwnerByIdForStats(ownerId: UUID): Flux<IssueModel> {
        return issueRepository.findByOwnerId(ownerId)

    }

    /**
     * Returns all comments written from user, searched by id
     * @param userId UUID Id from user
     * @return Flux<CommentModel>
     */
    fun getAllCommentsByUserIdForStats(userId: UUID): Flux<CommentModel> {
        return commentRepository.findAllByUserId(userId)
    }

    /**
     *
     * @param userId UUID
     * @return Flux<StatsModel>
     */
    fun getStatsFromId(userId: UUID): Flux<Optional<StatsModel>> {
        return Flux.zip(
                securityContextRepository.getAuthenticatedUser(),
                getAllIssuesFromOwnerByIdForStats(userId)
                        .collectList(),
                getAllCommentsByUserIdForStats(userId)
                        .collectList()
        )
                .filter{
                    it.t1.hasAdminRights()
                }
                .switchIfEmpty(Mono.error(ForbiddenException()))
                .map {
                    Optional.of(StatsModel(
                            userId,
                            it.t2.count(),
                            it.t3.count()
                    ))
                }
    }
}