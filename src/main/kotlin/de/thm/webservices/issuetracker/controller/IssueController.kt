package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.exception.BadRequestException
import de.thm.webservices.issuetracker.model.IssueModel
import de.thm.webservices.issuetracker.model.IssueViewModel
import de.thm.webservices.issuetracker.service.IssueService
import de.thm.webservices.issuetracker.util.checkIssueModel
import de.thm.webservices.issuetracker.util.checkNewIssueModel
import de.thm.webservices.issuetracker.util.checkPatchObject
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*


@RestController("IssueController")
class IssueController(
        private val issueService: IssueService
) {

    /**
     * Create new issue
     *
     * @param newIssue IssueModel? Issue to create
     * @return Mono<UUID> if success id as string, else null
     */
    @PostMapping("/issue")
    fun addNewIssue(@RequestBody newIssue: IssueModel): Mono<UUID> {
        return Mono.zip(checkNewIssueModel(newIssue), issueService.addNewIssue(newIssue))
                .filter { it.t1 }
                .switchIfEmpty(Mono.error(BadRequestException()))
                .map { it.t2 }
    }

    /**
     * Get an issue based on the Id
     *
     * @param id UUID Id of issue
     * @return Mono<IssueModel> If success issue else null
     */
    @GetMapping("/issue/{id}")
    fun getIssue(@PathVariable id: UUID): Mono<IssueModel> {
            return issueService.getIssueById(id)
    }

    /**
     * Update the while issue based on id
     *
     * @param id UUID Id of issue
     * @param issueToUpdate IssueModel? New content for issue
     * @return Mono<IssueModel>
     */
    @PutMapping("/issue/{id}")
    fun changeIssue(@PathVariable id: UUID, @RequestBody issueToUpdate: IssueModel): Mono<IssueModel> {

        return Mono.zip(checkIssueModel(issueToUpdate), issueService.updateIssue(id, issueToUpdate))
                .filter { it.t1 }
                .switchIfEmpty(Mono.error(BadRequestException("Issue is not valid. Could be that issue has empty value -> not allowed")))
                .map { it.t2 }
    }

    /**
     * Delete issue based on the id
     * @param id UUID Id of issue
     * @return Mono<Void>
     */
    @DeleteMapping("/issue/{id}")
    fun deleteIssue(@PathVariable id: UUID): Mono<Void> {
            return issueService.deleteIssue(id)
                    .switchIfEmpty(Mono.error(BadRequestException("The entered issue id is not existing")))
    }

    /**
     * Update some attributes of an issue
     *
     * @param id UUID Id of issue
     * @param issueWithAttrChanges Map<String, Any?>? Key value part to determine which attribute is to update
     * @return Mono<IssueModel> If success issue, else null
     */
    @PatchMapping("/issue/{id}")
    fun updateIssue(@PathVariable id: UUID, @RequestBody issueWithAttrChanges: Map<String, String>): Mono<IssueModel> {
        return Mono.zip(checkPatchObject(issueWithAttrChanges), issueService.changeAttrFromIssue(id, issueWithAttrChanges))
                .filter { it.t1 }
                .switchIfEmpty(Mono.error(BadRequestException("Wrong data was send. Id was not an UUIDV4 or path object are not valid")))
                .map { it.t2 }
    }

    /**
     * Returns all issues that a user has created
     * @param id UUID Id of issue
     * @return Flux<MutableList<IssueModel>>
     */
    @GetMapping("/issue/user/{id}")
    fun issuesOfAnUser(@PathVariable id: UUID): Flux<IssueModel> {
        return issueService.getAllIssues()
                .switchIfEmpty(Flux.from(Mono.error(BadRequestException("Wrong data was send. Id was not an UUIDV4"))))
                .filter {
                    it.ownerId == id
                }
                .map { it }
    }

    /** TODO rauswerfen
     * Only for Testing
     * @return Flux<IssueModel>
     */
    @GetMapping("/issue/allIssues")
    fun allIssues(): Flux<IssueModel> {
        return issueService.getAllIssues()
    }


    /**
     * Returns the issue with all comments
     * @param id UUID Id of issue
     * @return Mono<IssueViewModel>
     */
    @GetMapping("/issue/comment/{id}")
    fun getIssueWithComments(@PathVariable id: UUID): Mono<IssueViewModel> {
        return issueService.getIssueWithAllComments(id)
                .switchIfEmpty(Mono.error(BadRequestException("Missing ID")))
    }
}