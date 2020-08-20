package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.model.IssueModel
import de.thm.webservices.issuetracker.model.IssueViewModel
import de.thm.webservices.issuetracker.service.IssueService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
class IssueViewController(
        private val issueService: IssueService
) {

    /**
     * Gets all information about the issue with the corresponding comments anhander of the id
     *
     * @param issues List<UUID> Ids of issues
     * @return Flux<Optional<IssueViewModel>> Fetched data
     */
    @GetMapping("/_view/issueboard")
    fun getIssueData(@RequestParam issues: List<UUID>): Flux<Optional<IssueViewModel>> {
        return Flux.fromIterable(issues)
                .flatMap {
                    issueService.getIssueWithAllComments(it)
                            .map { Optional.of(it) }
                            .switchIfEmpty(Mono.just(Optional.empty()))
                }
    }
}