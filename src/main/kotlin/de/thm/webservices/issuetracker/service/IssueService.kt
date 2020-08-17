package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.exception.*
import de.thm.webservices.issuetracker.model.IssueModel
import de.thm.webservices.issuetracker.repository.IssueRepository
import de.thm.webservices.issuetracker.security.AuthenticatedUser
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*


@Service
class IssueService(private val issueRepository: IssueRepository) {

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
                    authenticatedUser.name == newIssueModel.owner
                }
                .switchIfEmpty(Mono.error(ForbiddenException()))
                .flatMap {
                    issueRepository.save(newIssueModel)
                            .switchIfEmpty(Mono.error(NoContentException("Could not create new issue")))
                            .map{ it.id!! }
                }
    }

    fun deleteIssue(issue:IssueModel) : Mono<Void> {
        return ReactiveSecurityContextHolder.getContext()
                .map { securityContext ->
                    securityContext.authentication
                }
                .cast(AuthenticatedUser::class.java)
                .filter { authenticatedUser ->
                    authenticatedUser.credentials == issue.owner
                }
                .switchIfEmpty(Mono.error(ForbiddenException("You are not the owner of the issue")))
                .flatMap {
                    issueRepository.delete(issue)
                            .switchIfEmpty(Mono.error(NoContentException("Could not delete issue")))
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
                    issueRepository.save(IssueModel(it.id, issueModelToUpdate.title, issueModelToUpdate.owner, issueModelToUpdate.deadline))
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
                            "owner" -> it.owner = if (!k.value?.toString().isNullOrEmpty()) {
                                k.value.toString()
                            } else {
                                it.owner
                            }
                        }
                    }

                    issueRepository.save(IssueModel(it.id, it.title, it.owner,it.deadline))
                            .switchIfEmpty(Mono.error(NotModifiedException("Could not update prop from Issue ")))
                }

    }


    fun getByOwner(ownerId: String): Flux<IssueModel> {
        return issueRepository.findByOwner(ownerId)
    }

    /**
     * TODO
     *
     * @param currentUser
     * @param issueId
     * @return
     */
    fun checkCurrentUserIsOwnerOfIssue(currentUser: String, issueId: UUID): Mono<Boolean>{
        return issueRepository.findById(issueId)
                .switchIfEmpty(Mono.error(NotFoundException("Issue id was not found")))
                .map {
                    var check = if( it.owner == currentUser ) true else false
                    check
                }
    }
}