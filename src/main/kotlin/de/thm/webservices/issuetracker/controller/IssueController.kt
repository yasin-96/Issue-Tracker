package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.exception.BadRequestException
import de.thm.webservices.issuetracker.exception.NoContentException
import de.thm.webservices.issuetracker.exception.NotFoundException
import de.thm.webservices.issuetracker.model.IssueModel
import de.thm.webservices.issuetracker.model.IssueViewModel
import de.thm.webservices.issuetracker.service.IssueService
import de.thm.webservices.issuetracker.util.checkIssueModel
import de.thm.webservices.issuetracker.util.checkPatchObject
import de.thm.webservices.issuetracker.util.checkUUID
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
     * @return Mono<UUID?> if success id as string, else null
     */
    @PostMapping("/issue")
    fun addNewIssue(@RequestBody newIssue: IssueModel?): Mono<UUID?> {

        if (checkIssueModel(newIssue)) {
            return issueService.addNewIssue(newIssue!!)
                    .switchIfEmpty(Mono.error(NoContentException("Issue could not created :(")))

        }
        return Mono.error(BadRequestException("Issue is not valid. Could be that issue has empty value -> not allowed"))
    }

    /**
     * Get an issue based on the Id
     *
     * @param id UUID? Id of issue
     * @return Mono<IssueModel> If success issue else null
     */
    @GetMapping("/issue/{id}")
    fun getIssue(@PathVariable id: UUID?): Mono<IssueModel> {
        if (checkUUID(id)) {
            return issueService.getIssueById(id!!)

        }
        //TODO  NoContent or BadRequest
        return Mono.error(NoContentException("Missing ID"))
    }

    /**
     * Update the while issue based on id
     *
     * @param id UUID Id of issue
     * @param issueToUpdate IssueModel? New content for issue
     * @return Mono<IssueModel>
     */
    @PutMapping("/issue/{id}")
    fun changeIssue(@PathVariable id: UUID?, @RequestBody issueToUpdate: IssueModel?): Mono<IssueModel> {
        if (checkUUID(id) && checkIssueModel(issueToUpdate)) {
            return issueService.updateIssue(id!!, issueToUpdate!!)
        }
        return Mono.error(BadRequestException("Issue is not valid. Could be that issue has empty value -> not allowed"))
    }

    /**
     * Delete issue based on the id
     * @param id UUID? Id of issue
     * @return Mono<Void>
     */
    @DeleteMapping("/issue/{id}")
    fun deleteIssue(@PathVariable id: UUID?): Mono<Void> {
        if (checkUUID(id!!)) {
            return issueService.deleteIssue(id)
        }
        //ToDo NotFoundException Or BadRequest
        return Mono.error(NotFoundException("The entered issue id is not existing"))
    }

    /**
     * Update some attributes of an issue
     *
     * @param id UUID? Id of issue
     * @param issueWithAttrChanges Map<String, Any?>? Key value part to determine which attribute is to update
     * @return Mono<IssueModel> If success issue, else null
     */
    @PatchMapping("/issue/{id}")
    fun updateIssue(@PathVariable id: UUID?, @RequestBody issueWithAttrChanges: Map<String, Any?>?): Mono<IssueModel> {
        if (checkUUID(id) && checkPatchObject(issueWithAttrChanges)) {
            return issueService.changeAttrFromIssue(id!!, issueWithAttrChanges)
                    .switchIfEmpty(Mono.error(NotFoundException()))

        }
        return Mono.error(BadRequestException("Wrong data was send. Id was not an UUIDV4 or path object are not valid"))
    }

    /**
     * Returns all issues that a user has created
     * @param id UUID? Id of issue
     * @return Flux<MutableList<IssueModel>>
     */
    @GetMapping("/issues/{id}")
    fun issuesOfAnUser(@PathVariable id: UUID?): Flux<MutableList<IssueModel>> {
        //TODO check UUID
        return issueService.getAllIssues()
                .map {
                    val issues: MutableList<IssueModel> = mutableListOf()
                    if (it.ownerId == id) {
                        issues.add(it)
                    }
                    issues
                }
    }

    /**
     * TODO löschen??
     * @return Flux<IssueModel>
     */
    @GetMapping("/issue/allIssues")
    fun allIssues(): Flux<IssueModel> {
        return issueService.getAllIssues()
    }


    /**
     * Returns the issue with all comments
     * @param id UUID? Id of issue
     * @return Mono<IssueViewModel>
     */
    @GetMapping("/issue/comment/{id}")
    fun getIssueWithComments(@PathVariable id: UUID?): Mono<IssueViewModel> {
        if (checkUUID(id)) {
            return issueService.getIssueWithAllComments(id!!)

        }
        //TODO NoContent or BadRequest?
        return Mono.error(NoContentException("Missing ID"))
    }
}