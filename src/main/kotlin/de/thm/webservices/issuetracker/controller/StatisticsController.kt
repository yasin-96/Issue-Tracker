package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.model.StatsModel
import de.thm.webservices.issuetracker.model.event.TagStatsModel
import de.thm.webservices.issuetracker.service.CommentService
import de.thm.webservices.issuetracker.service.IssueService
import de.thm.webservices.issuetracker.service.TaggingService
import de.thm.webservices.issuetracker.service.UserService
import de.thm.webservices.issuetracker.service.StatisticService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
class StatisticsController(
        private val commentService: CommentService,
        private val userService: UserService,
        private val taggingService: TaggingService,
        private val statisticService: StatisticService
) {

    /**
     * Returns a statistic which user with the
     * corresponding ID has created how many issues and comments
     * @param userIds List<UUID> Id's from many users
     * @return Flux<Optional<StatsModel>>
     */
    @GetMapping("/_stats")
    fun getStatsFromIds(@RequestParam userIds: List<UUID>): Flux<Optional<StatsModel>> {
        return Flux.fromIterable(userIds)
                .flatMap { userId -> statisticService.getStatsFromId(userId) }
    }

    /**
     * Counts all users within an issue that were linked in comments
     * @param issueId UUID Id of issue
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
     * Counts all users registered in the system
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


