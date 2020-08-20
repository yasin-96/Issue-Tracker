package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.model.StatsModel
import de.thm.webservices.issuetracker.repository.CommentRepository
import de.thm.webservices.issuetracker.repository.IssueRepository
import de.thm.webservices.issuetracker.service.CommentService
import de.thm.webservices.issuetracker.service.IssueService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.util.*

@RestController
class StatisticsController(var commentService: CommentService,var issueService: IssueService) {

    @GetMapping("/_stats")
    fun getStatsFromIds(@RequestParam userIds: List<UUID>): Flux<StatsModel> {
        return Flux.fromIterable(userIds).
            flatMap {
                getStats(it)
            }
    }


    fun getStats(uuid: UUID) : Flux<StatsModel>{
        return Flux.zip(commentService.getAllCommentsByUserId(uuid).collectList(),issueService.getAllIssuesFromOwnerById(uuid).collectList())
                .map {
                    StatsModel(uuid,it.t1.size,it.t2.size)
                }
    }
    }
