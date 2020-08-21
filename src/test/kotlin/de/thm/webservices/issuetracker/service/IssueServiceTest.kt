package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.exception.ForbiddenException
import de.thm.webservices.issuetracker.exception.NotFoundException
import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.model.IssueModel
import de.thm.webservices.issuetracker.model.event.CreateNewIssue
import de.thm.webservices.issuetracker.repository.CommentRepository
import de.thm.webservices.issuetracker.repository.IssueRepository
import de.thm.webservices.issuetracker.security.AuthenticatedUser
import de.thm.webservices.issuetracker.security.SecurityContextRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@SpringBootTest
@ExtendWith(MockitoExtension::class)
class IssueServiceTest(
        @Mock val issueRepository: IssueRepository,
        @Mock val commentRepository: CommentRepository,
        @Mock val issueTemplate: RabbitTemplate,
        @Mock val taggingService: TaggingService,
        @Mock val securityContextRepository: SecurityContextRepository

) {

    companion object {
        const val testUserId: String = "22222222-1111-1111-1111-111111111111"
        const val otherUserId: String = "33333333-1111-1111-1111-111111111111"
        const val testTitle = "test Title"
        const val testDeadline = "2020-12-01"

        val testUUID = UUID.randomUUID()!!
        val testOwnerID = UUID.randomUUID()!!
    }

    val authUser= AuthenticatedUser(
            testUUID.toString(),
            listOf()
    )

    val otherAuthUser= AuthenticatedUser(
            otherUserId,
            listOf()
    )
    val expectedIssue = IssueModel(testUUID, testTitle, testOwnerID, testDeadline)

    val issueService = IssueService(
            issueRepository,
            commentRepository,
            issueTemplate,
            taggingService,
            securityContextRepository
    )

    @Test
    fun testShouldGetIssueById(){
        given(issueRepository.findById(testUUID)).willReturn(Mono.just(expectedIssue))

        val serviceReturned = issueService.getIssueById(testUUID)

        Mockito.verify(issueRepository).findById(testUUID)

        serviceReturned.map {
            assert(it.id==expectedIssue.id)
        }
    }

    @Test
    fun testShouldGetIssueByIdNotFoundException(){
        given(issueRepository.findById(testUUID)).willReturn(Mono.empty())

        issueService.getIssueById(testUUID)
                .onErrorResume { exception ->
            assert(exception is NotFoundException)
            Mono.empty()
        }
    }

    @Test
    fun testDeleteIssue(){
        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.just(authUser))
        given(issueRepository.findById(testUUID)).willReturn(Mono.just(expectedIssue))

        given(issueRepository.deleteById(testUUID)).willReturn(Mono.empty())

        issueService.deleteIssue(testUUID).subscribe{
            Mockito.verify(issueRepository).findById(testUUID)
            Mockito.verify(issueRepository).deleteById(testUUID)
        }
    }

    @Test
    fun testShouldDeleteIssueThrowForbiddenException(){
        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.just(otherAuthUser))
        given(issueRepository.findById(testUUID)).willReturn(Mono.just(expectedIssue))
        given(issueRepository.deleteById(testUUID)).willReturn(Mono.empty())

        issueService.deleteIssue(testUUID)
                .onErrorResume { exception ->
                    assert(exception is IllegalAccessError)
                    Mono.empty()
                }.subscribe(
                )
    }

    @Test
    fun testAddNewIssue(){

        val newIssueModel = IssueModel(
                null,
                testTitle,
                testUUID,
                testDeadline
        )

        val returnedIssue = IssueModel(
                UUID.randomUUID(),
                testTitle,
                testUUID,
                testDeadline
        )

        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.just(authUser))
        given(issueRepository.save(newIssueModel)).willReturn(Mono.just(returnedIssue))
        given(taggingService.tagging(newIssueModel.title)).willReturn(Mono.just(mutableSetOf(returnedIssue.id!!)))

        Mockito.doNothing().`when`(issueTemplate).convertAndSend("amq.topic", returnedIssue.id.toString() + ".news",
                CreateNewIssue(returnedIssue.id!!))

        issueService.addNewIssue(newIssueModel).subscribe { returnedIssueID: UUID ->
            Mockito.verify(issueRepository).save(newIssueModel)
            assert(returnedIssueID == returnedIssue.id)
        }
    }

    @Test
    fun testUpdateIssue(){
        val toSaveIssue = IssueModel(testUUID, testTitle, testOwnerID, testDeadline)

        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.just(authUser))
        given(issueRepository.findById(testUUID)).willReturn(Mono.just(expectedIssue))
        given(issueRepository.save(toSaveIssue)).willReturn(Mono.just(expectedIssue))

        issueService.updateIssue(toSaveIssue.id!!, toSaveIssue).subscribe{
            Mockito.verify(issueRepository).save(expectedIssue)
            assert(toSaveIssue.id == expectedIssue.id)
        }
    }

    @Test
    fun testChangeAttrFromIssue(){
        val toSaveIssue = IssueModel(testUUID, testTitle, testOwnerID, testDeadline)
        var myChanges = mutableMapOf<String, String?>()
        val newTitle = "My changed title"

        myChanges.put("title", newTitle)
        myChanges.put("ownerId", otherUserId)

        val changedIssue = IssueModel(toSaveIssue.id,newTitle, UUID.fromString(otherUserId), toSaveIssue.deadline)

        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.just(authUser))
        given(issueRepository.findById(toSaveIssue.id!!)).willReturn(Mono.just(toSaveIssue))
        given(issueRepository.save(changedIssue)).willReturn(Mono.just(changedIssue))

        issueService.changeAttrFromIssue(toSaveIssue.id!!, myChanges).subscribe{
            Mockito.verify(issueRepository).save(changedIssue)
            assert(toSaveIssue.id == it.id)
            assert(newTitle == it.title)
            assert(UUID.fromString(otherUserId) == it.ownerId)
        }
    }

    @Test
    fun testGetAllIssuesFromOwnerById() {
        val title1 = "title1"
        val title2 = "title2"
        val title3 = "title3"

        val issue1 = IssueModel(UUID.randomUUID(), title1, testOwnerID, testDeadline)
        val issue2 = IssueModel(UUID.randomUUID(), title2, testOwnerID, testDeadline)
        val issue3 = IssueModel(UUID.randomUUID(), title3, testOwnerID, testDeadline)

        var myIssues = mutableListOf(issue1, issue2, issue3)

        given(issueRepository.findByOwnerId(testOwnerID)).willReturn(Flux.fromIterable(myIssues))

        issueService.getAllIssuesFromOwnerById(testOwnerID).subscribe {
            assert(it.ownerId == testOwnerID)
            assert(it.id != UUID.randomUUID())
            if (it.id == issue1.id) {
                assert(it.title == issue1.title)
                assert(it.title != issue2.title)
            }
            if (it.id == issue2.id) {
                assert(it.title == issue2.title)
            }
        }
    }

    @Test
    fun testGetIssueWithAllComments() {
        val title1 = "title1"
        val issue1 = IssueModel(testUUID, title1, testOwnerID, testDeadline)

        val commentContent1 = "content1"
        val commentContent2 = "content2"
        val commentContent3 = "content3"

        val comment1 = CommentModel(UUID.randomUUID(),commentContent1, issue1.id, testOwnerID)
        val comment2 = CommentModel(UUID.randomUUID(),commentContent2, issue1.id, testOwnerID)
        val comment3 = CommentModel(UUID.randomUUID(),commentContent3, issue1.id, testOwnerID)

        val myComments = mutableListOf(comment1, comment2, comment3)

        given(issueRepository.findById(testUUID)).willReturn(Mono.just(issue1))
        given(commentRepository.findAllByIssueId(testUUID)).willReturn(Flux.fromIterable(myComments))

        issueService.getIssueWithAllComments(testUUID).subscribe { issueView ->
            assert(issueView.issue.id == issue1.id)
            assert(issueView.issue.id != UUID.randomUUID())

            issueView.comments.map {
                if (it.id == comment1.id) {
                    assert(it.content == comment1.content)
                    assert(it.content != comment2.content)
                }
            }

        }
        }

}