package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.exception.BadRequestException
import de.thm.webservices.issuetracker.exception.NoContentException
import de.thm.webservices.issuetracker.exception.NotFoundException
import de.thm.webservices.issuetracker.model.IssueModel
import de.thm.webservices.issuetracker.model.IssueViewModel
import de.thm.webservices.issuetracker.service.IssueService
import de.thm.webservices.issuetracker.service.UserService
import de.thm.webservices.issuetracker.util.checkImportantProps
import de.thm.webservices.issuetracker.util.checkIssueModel
import de.thm.webservices.issuetracker.util.checkPatchObject
import de.thm.webservices.issuetracker.util.checkUUID
import org.springframework.data.relational.core.sql.Not
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import kotlin.collections.ArrayList


@RestController("IssueController")
//@RequestMapping("/api/")
class IssueController(private val issueService: IssueService) {


    /**
     * Creates a new issue
     *
     * @param newIssue issue to create
     * @return if success id as string, else null
     */
    @PostMapping("/issue")
    fun addNewIssue(@RequestBody newIssue: IssueModel?): Mono<UUID?> {

        if (checkImportantProps(newIssue)) {
            return issueService.addNewIssue(newIssue!!)
                    .switchIfEmpty(Mono.error(NoContentException("Issue could not created :(")))

        }
        return Mono.error(BadRequestException("Issue is not valid. Could be that issue has empty value -> not allowed"))
    }

    /**
     * Get an issue based on the Id
     *
     * @param id id of issue
     * @return null or issue as json
     */
    @GetMapping("/issue/{id}")
    fun getIssue(@PathVariable id: UUID?): Mono<IssueModel> {
        if (checkUUID(id)) {
            return issueService.getIssueById(id!!)

        }
        return Mono.error(NoContentException("Missing ID"))
    }

    /**
     * This REST point updates the whole object using the id
     *
     * @param issueToUpdate the whole issue to update
     * @return null or issue as json
     */
    @PutMapping("/issue/{id}")
    fun changeIssue(@PathVariable id: UUID, @RequestBody issueToUpdate: IssueModel?): Mono<IssueModel> {
        if (checkUUID(id) && checkIssueModel(issueToUpdate)) {
            return issueService.updateIssue(id, issueToUpdate!!)
        }
        return Mono.error(BadRequestException("Issue is not valid. Could be that issue has empty value -> not allowed"))
    }

    /**
     * This REST point is used to delete an issue based on the UUID of the issue
     *
     * @param id id of issue
     * @return status ok or bad-request if not worked
     */
    @DeleteMapping("/issue/{id}")
    fun deleteIssue(@PathVariable id: UUID?): Mono<Void> {
        if (checkUUID(id!!)) {
            return issueService.deleteIssue(id)
        }
        return Mono.error(NotFoundException("The entered issue id is not existing"))
    }

    /**
     * This REST point is used to change some attributes of an issue
     *
     * @param id id of issue
     * @param issueWithAttrChanges Key value part to determine which attribute is to update
     * @return null or issue as json
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
     * TODO
     * @param id UUID
     * @return Flux<MutableList<IssueModel>>
     */
    @GetMapping("/issues/{id}")
    fun issuesOfAnUser(@PathVariable id: UUID): Flux<MutableList<IssueModel>> {
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
     * TODO
     * @return Flux<IssueModel>
     */
    @GetMapping("/issue/allIssues")
    fun allIssues(): Flux<IssueModel> {
        return issueService.getAllIssues()
    }

    /**
     * TODO
     * @param id UUID
     * @return Mono<IssueViewModel>
     */
    @GetMapping("/issue/comment/{id}")
    fun getIssueWithComments(@PathVariable id: UUID): Mono<IssueViewModel> {
        if (checkUUID(id)) {
            return issueService.getIssueWithAllComments(id)

        }
        return Mono.error(NoContentException("Missing ID"))
    }
}