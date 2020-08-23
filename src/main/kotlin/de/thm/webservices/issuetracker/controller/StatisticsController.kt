package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.model.StatsModel
import de.thm.webservices.issuetracker.model.event.TagStatsModel
import de.thm.webservices.issuetracker.service.CommentService
import de.thm.webservices.issuetracker.service.IssueService
import de.thm.webservices.issuetracker.service.TaggingService
import de.thm.webservices.issuetracker.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
class StatisticsController(
        private val commentService: CommentService,
        private val issueService: IssueService,
        private val userService: UserService,
        var taggingService: TaggingService
) {

    /**
     *
     * @param userIds List<UUID>
     * @return Flux<Optional<StatsModel>>
     */
    @GetMapping("/_stats")
    fun getStatsFromIds(@RequestParam userIds: List<UUID>): Flux<Optional<StatsModel>> {
        return Flux.fromIterable(userIds)
                .flatMap { userId ->
                    Flux.zip(
                            issueService.getAllIssuesFromOwnerByIdForStats(userId)
                                    .collectList(),
                            commentService.getAllCommentsByUserIdForStats(userId)
                                    .collectList()
                    )
                            .map {
                                Optional.of(StatsModel(
                                        userId,
                                        it.t1.count(),
                                        it.t2.count()
                                ))
                            }
                }
    }

    /**
     *
     * @param issueId UUID
     * @return Mono<Optional<TagStatsModel>>
     */
    @GetMapping("/_stat/tags")
    fun getNumberOfTaggedUsers(@RequestParam issueId: UUID): Mono<Optional<TagStatsModel>> {
        return commentService.getAllCommentByIssueId(issueId).collectList()
                .flatMap { taggingService.countAllTaggedUsersInComments(it)}
                .map {
                    Optional.of( TagStatsModel(issueId, it)) }
    }

    /**
     *
     * @return Mono<Map<String, Int>>
     */
    @GetMapping("/_stats/registered")
    fun getAllRegisteredUser(): Mono<Map<String, Int>> {
        return userService.getNumberOfRegistertedUsers()
                .map {
                    mapOf("registeredUser" to it)
                }
    }
}


