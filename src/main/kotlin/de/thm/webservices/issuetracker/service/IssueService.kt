package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.exception.*
import de.thm.webservices.issuetracker.model.IssueModel
import de.thm.webservices.issuetracker.model.IssueViewModel
import de.thm.webservices.issuetracker.model.event.CreateNewIssue
import de.thm.webservices.issuetracker.repository.CommentRepository
import de.thm.webservices.issuetracker.repository.IssueRepository
import de.thm.webservices.issuetracker.security.AuthenticatedUser
import de.thm.webservices.issuetracker.security.SecurityContextRepository
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import java.util.*


@Service
class IssueService(
        private val issueRepository: IssueRepository,
        private val commentRepository: CommentRepository,
        private val issueTemplate: RabbitTemplate,
        private val taggingService: TaggingService,
        private val securityContextRepository: SecurityContextRepository
) {
    private val topicPath = "amq.topic"
    private val postFixNews = ".news"

    /**
     * Here the ID is checked again and if the validation has run through and
     * everything has worked, then the database is checked with
     * the id to see if an issue could be found
     *
     * @param idFromIssue UUID Id of issue
     * @return Mono<IssueModel>
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
     * @param newIssueModel IssueModel New issue to create
     * @return Mono<UUID>
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
                                topicPath,
                                uuid.toString() + postFixNews,
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
                .zipWith(issueRepository.findById(issueId).switchIfEmpty(Mono.error(NotFoundException())))
                .filter { tuple2: Tuple2<AuthenticatedUser, IssueModel> ->
                    tuple2.t1.authorities.contains(SimpleGrantedAuthority("ADMIN"))
                            || tuple2.t2.ownerId.toString() == tuple2.t1.name
                }
                .switchIfEmpty(Mono.error(ForbiddenException("You are not the owner of the issue")))
                .flatMap {
                    issueRepository.deleteById(issueId)
                }
    }

    /**
     * Here, after the issue has been checked again,
     * and then the existing issue is updated in the database
     * @param idOfIssue UUID  Id of issue
     * @param issueModelToUpdate IssueModel Issue to update
     * @return Mono<IssueModel>
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
     * @param idOfIssue UUID Id of issue
     * @param issueAttr Map<String, String?>? Attribute to change the value
     * @return Mono<IssueModel>
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
                            "deadline" -> issue.deadline = k.value ?: issue.deadline
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
     * Returns all issues based  on user id
     * @param ownerId UUID Id of owner
     * @return Flux<IssueModel>
     */
    fun getAllIssuesFromOwnerByIdForStats(ownerId: UUID): Flux<IssueModel> {
        return issueRepository.findByOwnerId(ownerId)
    }


    /**
     * Return a model with issue and his c
     * @param issueId UUID
     * @return Mono<IssueViewModel>
     */
    fun getIssueWithAllComments(issueId: UUID): Mono<IssueViewModel> {
        return Mono.zip(issueRepository.findById(issueId), commentRepository.findAllByIssueId(issueId).collectList())
                .map { IssueViewModel(it.t1, it.t2) }
    }
}