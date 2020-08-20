package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.model.IssueModel
import de.thm.webservices.issuetracker.model.StatsModel
import de.thm.webservices.issuetracker.model.event.TagStatsModel
import de.thm.webservices.issuetracker.repository.CommentRepository
import de.thm.webservices.issuetracker.repository.IssueRepository
import de.thm.webservices.issuetracker.service.CommentService
import de.thm.webservices.issuetracker.service.IssueService
import de.thm.webservices.issuetracker.service.TaggingService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.*

@RestController
class StatisticsController(var commentService: CommentService,var issueService: IssueService, var taggingService: TaggingService) {

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

    @GetMapping("/_stat/tags")
    fun getNumberOfTaggedUsers(@RequestParam issueId:UUID):Mono<TagStatsModel> {
        return commentService.getAllCommentByIssueId(issueId)
                .map {
                    taggingService.tagging(it.content).map {
                        it.count()
                    }
                }
                .count().map {
                    TagStatsModel(issueId,it.toInt())
                }
    }





                /*.map { it.flatMap {
                    taggingService.tagging(it.content).map {
                        it.count()
                    }
                } .map {

                }



                }


                }*/
                }


