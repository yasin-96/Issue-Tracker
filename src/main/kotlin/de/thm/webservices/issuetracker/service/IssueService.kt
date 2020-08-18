package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.exception.*
import de.thm.webservices.issuetracker.model.IssueModel
import de.thm.webservices.issuetracker.model.IssueViewModel
import de.thm.webservices.issuetracker.repository.CommentRepository
import de.thm.webservices.issuetracker.repository.IssueRepository
import de.thm.webservices.issuetracker.security.AuthenticatedUser
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*


@Service
class IssueService(
        private val issueRepository: IssueRepository,
        private val commentRepository: CommentRepository
) {

    /**
     * Here the ID is checked again and if the validation has run through and
     * everything has worked, then the database is checked with
     * the id to see if an issue could be found
     *
     * @param idFromIssue UUID from issue
     * @return if found returns issue, else null
     */
    fun getIssueById(idFromIssue: UUID): Mono<IssueModel> {
        return issueRepository.findById(idFromIssue)
                .switchIfEmpty(Mono.error(NotFoundException("Id not found")))
    }

    fun getAllIssues() : Flux<IssueModel>{
        return issueRepository.findAll()
    }


    /**
     * Here, after the issue has been reviewed again,
     * the data is stored in the database.
     *
     * @param newIssueModel new issue to create
     * @return if it works then returns the id, else null
     */
    fun addNewIssue(newIssueModel: IssueModel): Mono<UUID?> {
        return ReactiveSecurityContextHolder.getContext()
                .map { securityContext ->
                    securityContext.authentication
                }
                .cast(AuthenticatedUser::class.java)
                .filter { authenticatedUser ->
                    authenticatedUser.name == newIssueModel.ownerId.toString()
                }
                .switchIfEmpty(Mono.error(ForbiddenException()))
                .flatMap { authenticatedUser ->
                    issueRepository.save(newIssueModel)
                            .switchIfEmpty(Mono.error(NoContentException("Could not create new issue")))
                            .map{ it.id!! }
                }
    }

    /**
     * Here, after the issue has been checked again,
     * and then the existing issue is updated in the database
     *
     * @param issueModelToUpdate issue to update
     * @return if it works issue as json, else null
     */
    fun updateIssue(idOfIssue: UUID, issueModelToUpdate: IssueModel): Mono<IssueModel> {
        return getIssueById(idOfIssue)
                .switchIfEmpty(Mono.error(NotFoundException()))
                .flatMap {
                    issueRepository.save(IssueModel(it.id, issueModelToUpdate.title, issueModelToUpdate.ownerId, issueModelToUpdate.deadline))
                            .switchIfEmpty(Mono.error(NotModifiedException("Id was not found and issue was not modified")))
                }
    }

    /**
     * Here, after the Id has been checked, the system checks
     * whether an issue exists in the database and if so, deletes it.
     *
     * @param issueWithIdToRemove id of issue
     * @return if works returns true, else false
     */
    fun removeIssueById(issueWithIdToRemove: UUID): Mono<Void> {

        return getIssueById(issueWithIdToRemove)
                .switchIfEmpty(Mono.error(NotFoundException("Id not found. Issue was not removed")))
                .flatMap {
                    issueRepository.delete(it)
                }
    }

    /**
     * Here the id and the attribute are checked and
     * then exactly these attributes are changed during the issue.
     *
     * @param idOfIssue id of issue
     * @param issueAttr attribute to change the value
     * @return if attribute was found and id is valid then returns issue as json, else null
     */
    fun changeAttrFromIssue(idOfIssue: UUID, issueAttr: Map<String, Any?>?): Mono<IssueModel> {

        return getIssueById(idOfIssue)
                .switchIfEmpty(Mono.error(NotModifiedException("Could not update prop from Issue ")))
                .flatMap {
                    for (k in issueAttr!!) {
                        when (k.key) {
                            "title" -> it.title = if (!k.value?.toString().isNullOrEmpty()) {
                                k.value.toString()
                            } else {
                                it.title
                            }
                            "ownerId" -> it.ownerId = if (!k.value?.toString().isNullOrEmpty()) {
                                k.value as UUID
                            } else {
                                it.ownerId
                            }
                        }
                    }

                    issueRepository.save(IssueModel(it.id, it.title, it.ownerId,it.deadline))
                            .switchIfEmpty(Mono.error(NotModifiedException("Could not update prop from Issue ")))
                }

    }

    fun getByOwner(ownerId: UUID): Flux<IssueModel> {
        return issueRepository.findByOwnerId(ownerId)
    }

    fun checkCurrentUserIsOwnerOfIssue(currentUser: String, issueId: UUID): Mono<Boolean>{
        return issueRepository.findById(issueId)
                .switchIfEmpty(Mono.error(NotFoundException("Issue id was not found")))
                .map {
                    var check = it.ownerId.toString() == currentUser
                    check
                }
    }

    fun getIssueWithAllComments(issueId: UUID): Mono<IssueViewModel>{
        val issue = issueRepository.findById(issueId)
        val comments = commentRepository.findByIssueId(issueId).collectList()
                .switchIfEmpty(Mono.error(NoContentException("Id in comment for issue was not correct")))

        return Mono.zip(issue,comments)
                .map {
                    IssueViewModel(it.t1, it.t2)
                }
    }
}