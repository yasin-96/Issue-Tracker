package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.exception.*
import de.thm.webservices.issuetracker.model.IssueModel
import de.thm.webservices.issuetracker.model.IssueViewModel
import de.thm.webservices.issuetracker.model.event.CreateNewComment
import de.thm.webservices.issuetracker.model.event.CreateNewIssue
import de.thm.webservices.issuetracker.repository.CommentRepository
import de.thm.webservices.issuetracker.repository.IssueRepository
import de.thm.webservices.issuetracker.security.SecurityContextRepository
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*


@Service
class IssueService(
        private val issueRepository: IssueRepository,
        private val commentRepository: CommentRepository,
        private val issueTemplate: RabbitTemplate,
        private val taggingService: TaggingService,
        private val securityContextRepository: SecurityContextRepository
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

    /**
     * TODO raus nehmen
     * @return Flux<IssueModel>
     */
    fun getAllIssues(): Flux<IssueModel> {
        return issueRepository.findAll()
    }


    /**
     * Here, after the issue has been reviewed again,
     * the data is stored in the database.
     *
     * @param newIssueModel new issue to create
     * @return if it works then returns the id, else null
     */
    fun addNewIssue(newIssueModel: IssueModel): Mono<UUID> {

        return securityContextRepository.getAuthenticatedUser()
                .filter { it.hasRightsOrIsAdmin(newIssueModel.ownerId) }
                .switchIfEmpty(Mono.error(ForbiddenException()))
                .flatMap {
                    issueRepository.save(newIssueModel)
                            .switchIfEmpty(Mono.error(NoContentException("Could not create new issue")))
                            .zipWith(taggingService.tagging(newIssueModel.title))
                }
                .doOnSuccess { tuple ->
                    tuple.t2.map { uuid ->
                        issueTemplate.convertAndSend(
                                "amq.topic",
                                uuid.toString() + ".news",
                                CreateNewIssue(tuple.t1.id!!)
                        )
                    }
                }
                .map{
                    it.t1.id!!
                }
    }

    /**
     * Here, after the Id has been checked, the system checks
     * whether an issue exists in the database and if so, deletes it.
     *
     * @param issueId UUID id of issue
     * @return Mono<Void> if works returns true, else false
     */
    fun deleteIssue(issueId: UUID): Mono<Void> {
        return securityContextRepository.getAuthenticatedUser()
                .flatMap { authUser ->
                    getIssueById(issueId)
                        //.switchIfEmpty(Mono.error(NotFoundException("")))
                        //.filter { authUser.hasRightsOrIsAdmin(it.ownerId)}
                }
                .switchIfEmpty(Mono.error(ForbiddenException("You are not the owner of the issue")))
                .flatMap {
                    issueRepository.deleteById(issueId)
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
        return securityContextRepository.getAuthenticatedUser()
                .filter { it.hasRightsOrIsAdmin(issueModelToUpdate.ownerId) }
                .switchIfEmpty(Mono.error(ForbiddenException("You are not the owner of the issue")))
                .flatMap {
                    getIssueById(idOfIssue)
                            .switchIfEmpty(Mono.error(NotFoundException()))
                            .flatMap {
                                issueRepository.save(IssueModel(it.id, issueModelToUpdate.title, issueModelToUpdate.ownerId, issueModelToUpdate.deadline))
                                        .switchIfEmpty(Mono.error(NotModifiedException("Id was not found and issue was not modified")))
                            }
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
    fun changeAttrFromIssue(idOfIssue: UUID, issueAttr: Map<String, String?>?): Mono<IssueModel> {

        return securityContextRepository.getAuthenticatedUser()
                .flatMap { authUser ->
                    getIssueById(idOfIssue)
                            .switchIfEmpty(Mono.error(NotFoundException()))
                            .filter { authUser.hasRightsOrIsAdmin(it.ownerId) }
                }
                .switchIfEmpty(Mono.error(ForbiddenException("You are not the owner of the issue")))
                .map { issue ->
                    for (k in issueAttr!!) {
                        when (k.key) {
                            "title" -> issue.title = k.value ?: issue.title
                            "ownerId" -> issue.ownerId = UUID.fromString(k.value) ?: issue.ownerId
                        }
                    }
                    issue
                }
                .flatMap { issue ->
                    issueRepository.save(IssueModel(issue.id, issue.title, issue.ownerId, issue.deadline))
                            .switchIfEmpty(Mono.error(NotModifiedException("Could not update prop from Issue ")))
                }
    }


    /**
     * Returns all issues based  on user id
     * @param ownerId UUID Id of owner
     * @return Flux<IssueModel>
     */
    fun getAllIssuesFromOwnerById(ownerId: UUID): Flux<IssueModel> {
        return issueRepository.findByOwnerId(ownerId)
                .switchIfEmpty(Mono.error(NotFoundException()))
    }


    /**
     * TODO
     * @param issueId UUID
     * @return Mono<IssueViewModel>
     */
    fun getIssueWithAllComments(issueId: UUID): Mono<IssueViewModel> {
        val issue = issueRepository.findById(issueId)
        val comments = commentRepository.findAllByIssueId(issueId).collectList()
                .switchIfEmpty(Mono.error(NoContentException("Id in comment for issue was not correct")))

        return Mono.zip(issue, comments)
                .map {
                    IssueViewModel(it.t1, it.t2)
                }
    }
}