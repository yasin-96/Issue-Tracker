package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.model.IssueModel
import de.thm.webservices.issuetracker.service.IssueService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.util.*

@RestController
class IssueViewController(
        private val issueService: IssueService
) {

    @GetMapping("_view/issueboard")
    fun getIssueData(@RequestParam issues: List<UUID>): Flux<Map<IssueModel, List<CommentModel>>>{

        return Flux.just()
    }
}