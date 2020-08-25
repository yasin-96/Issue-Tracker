package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.exception.ForbiddenException
import de.thm.webservices.issuetracker.exception.NoContentException
import de.thm.webservices.issuetracker.exception.NotFoundException
import de.thm.webservices.issuetracker.exception.NotModifiedException
import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.model.IssueModel
import de.thm.webservices.issuetracker.model.event.CreateNewIssue
import de.thm.webservices.issuetracker.repository.CommentRepository
import de.thm.webservices.issuetracker.repository.IssueRepository
import de.thm.webservices.issuetracker.security.AuthenticatedUser
import de.thm.webservices.issuetracker.security.SecurityContextRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.any
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
        const val newTitle = "My new title"

        val testIssueUUID = UUID.randomUUID()!!
        val testOwnerUUID = UUID.randomUUID()!!
        val testUserUUID = UUID.randomUUID()!!
    }

    val authUser = AuthenticatedUser(
            testUserUUID.toString(),
            listOf()
    )

    val otherAuthUser = AuthenticatedUser(
            otherUserId,
            listOf()
    )
    val expectedIssue = IssueModel(testIssueUUID, testTitle, testUserUUID, testDeadline)
    val newIssueModel = IssueModel(null, testTitle, testUserUUID, testDeadline)
    val returnedIssue = IssueModel(UUID.randomUUID(), testTitle, testUserUUID, testDeadline)

    val issueService = IssueService(
            issueRepository,
            commentRepository,
            issueTemplate,
            taggingService,
            securityContextRepository
    )


    @Test
    fun testGetIssueById() {
        given(issueRepository.findById(testIssueUUID)).willReturn(Mono.just(expectedIssue))

        val serviceReturned = issueService.getIssueById(testIssueUUID)

        Mockito.verify(issueRepository).findById(testIssueUUID)

        serviceReturned.map {
            assert(it.id == expectedIssue.id)
        }
    }

    @Test
    fun testGetIssueByIdNotFoundException() {
        given(issueRepository.findById(testIssueUUID)).willReturn(Mono.empty())

        issueService.getIssueById(testIssueUUID)
                .onErrorResume { exception ->
                    assert(exception is NotFoundException)
                    Mono.empty()
                }.subscribe()
    }

    @Test
    fun testAddNewIssue() {

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
    fun testAddNewIssueForbiddenException() {
        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.empty())

        issueService.addNewIssue(newIssueModel)
                .onErrorResume { exception ->
                    assert(exception is ForbiddenException)
                    Mono.empty()
                }.subscribe()
    }

    //@Test
    fun testAddNewIssueNoContentException() {
        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.just(authUser))
        given(issueRepository.save(newIssueModel)).willReturn(Mono.empty())

        // TODO does not work as wished
        issueService.addNewIssue(newIssueModel)
                .onErrorResume { exception ->
                    assert(exception is NoContentException)
                    Mono.empty()
                }.subscribe()
    }

    @Test
    fun testDeleteIssue() {
        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.just(authUser))
        given(issueRepository.findById(testIssueUUID)).willReturn(Mono.just(expectedIssue))

        given(issueRepository.deleteById(testIssueUUID)).willReturn(Mono.empty())

        issueService.deleteIssue(testIssueUUID).subscribe {
            Mockito.verify(issueRepository).findById(testIssueUUID)
            Mockito.verify(issueRepository).deleteById(testIssueUUID)
        }
    }

    @Test
    fun testDeleteIssueThrowsNotFoundException() {
        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.just(authUser))
        given(issueRepository.findById(testIssueUUID)).willReturn(Mono.empty())

        issueService.deleteIssue(testIssueUUID)
                .onErrorResume { exception ->
                    assert(exception is NotFoundException)
                    Mono.empty()
                }.subscribe()
    }

    @Test
    fun testDeleteIssueThrowForbiddenException() {
        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.just(otherAuthUser))
        given(issueRepository.findById(testIssueUUID)).willReturn(Mono.just(expectedIssue))
        given(issueRepository.deleteById(testIssueUUID)).willReturn(Mono.empty())

        issueService.deleteIssue(testIssueUUID)
                .onErrorResume { exception ->
                    assert(exception is ForbiddenException)
                    Mono.empty()
                }.subscribe()
    }


    @Test
    fun testUpdateIssue() {
        val toSaveIssue = IssueModel(testIssueUUID, testTitle, testUserUUID, testDeadline)

        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.just(authUser))
        given(issueRepository.findById(testIssueUUID)).willReturn(Mono.just(expectedIssue))
        given(issueRepository.save(toSaveIssue)).willReturn(Mono.just(expectedIssue))

        issueService.updateIssue(toSaveIssue.id!!, toSaveIssue).subscribe {
            Mockito.verify(issueRepository).save(expectedIssue)
            assert(toSaveIssue.id == expectedIssue.id)
        }
    }

    @Test
    fun testUpdateIssueThrowsForbiddenException() {
        val toSaveIssue = IssueModel(testIssueUUID, testTitle, testUserUUID, testDeadline)

        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.empty())

        issueService.updateIssue(toSaveIssue.id!!, toSaveIssue)
                .onErrorResume { exception ->
                    assert(exception is ForbiddenException)
                    Mono.empty()
                }.subscribe()
    }

    @Test
    fun testUpdateIssueThrowsNotFoundException() {
        val toSaveIssue = IssueModel(testIssueUUID, testTitle, testUserUUID, testDeadline)

        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.just(authUser))
        given(issueRepository.findById(testIssueUUID)).willReturn(Mono.empty())

        issueService.updateIssue(toSaveIssue.id!!, toSaveIssue)
                .onErrorResume { exception ->
                    assert(exception is NotFoundException)
                    Mono.empty()
                }.subscribe()
    }

    @Test
    fun testUpdateIssueThrowsNotModifiedException() {
        val toSaveIssue = IssueModel(testIssueUUID, testTitle, testUserUUID, testDeadline)

        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.just(authUser))
        given(issueRepository.findById(testIssueUUID)).willReturn(Mono.just(expectedIssue))
        given(issueRepository.save(toSaveIssue)).willReturn(Mono.empty())

        issueService.updateIssue(toSaveIssue.id!!, toSaveIssue)
                .onErrorResume { exception ->
                    assert(exception is NotModifiedException)
                    Mono.empty()
                }.subscribe()
    }

    @Test
    fun testChangeAttrFromIssue() {
        val toSaveIssue = IssueModel(testIssueUUID, testTitle, testUserUUID, testDeadline)
        var myChanges = mutableMapOf("title" to newTitle, "ownerId" to otherUserId)

        val changedIssue = IssueModel(toSaveIssue.id, newTitle, UUID.fromString(otherUserId), toSaveIssue.deadline)

        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.just(authUser))
        given(issueRepository.findById(toSaveIssue.id!!)).willReturn(Mono.just(toSaveIssue))
        given(issueRepository.save(any(IssueModel::class.java))).willReturn(Mono.just(changedIssue))

        issueService.changeAttrFromIssue(toSaveIssue.id!!, myChanges).subscribe {
            Mockito.verify(issueRepository).save(toSaveIssue)
            assert(toSaveIssue.id == it.id)
            assert(newTitle == it.title)
            assert(UUID.fromString(otherUserId) == it.ownerId)
        }
    }

    @Test
    fun testChangeAttrFromIssueThrowsNotFoundException() {
        val toSaveIssue = IssueModel(testIssueUUID, testTitle, testUserUUID, testDeadline)
        var myChanges = mutableMapOf("title" to newTitle, "ownerId" to otherUserId)

        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.just(authUser))
        given(issueRepository.findById(toSaveIssue.id!!)).willReturn(Mono.empty())

        issueService.changeAttrFromIssue(toSaveIssue.id!!, myChanges)
                .onErrorResume { exception ->
                    assert(exception is NotFoundException)
                    Mono.empty()
                }.subscribe()
    }

    @Test
    fun testChangeAttrFromIssueThrowsForbiddenException() {
        val toSaveIssue = IssueModel(testIssueUUID, testTitle, testUserUUID, testDeadline)
        var myChanges = mutableMapOf("title" to newTitle, "ownerId" to otherUserId)

        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.empty())

        issueService.changeAttrFromIssue(toSaveIssue.id!!, myChanges)
                .onErrorResume { exception ->
                    assert(exception is ForbiddenException)
                    Mono.empty()
                }.subscribe()
    }

    @Test
    fun testChangeAttrFromIssueThrowsNotModifiedException() {
        val toSaveIssue = IssueModel(testIssueUUID, testTitle, testUserUUID, testDeadline)
        var myChanges = mutableMapOf("title" to newTitle, "ownerId" to otherUserId)

        val changedIssue = IssueModel(toSaveIssue.id, newTitle, UUID.fromString(otherUserId), toSaveIssue.deadline)

        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.just(authUser))
        given(issueRepository.findById(toSaveIssue.id!!)).willReturn(Mono.just(toSaveIssue))
        given(issueRepository.save(any(IssueModel::class.java))).willReturn(Mono.empty())

        issueService.changeAttrFromIssue(toSaveIssue.id!!, myChanges)
                .onErrorResume { exception ->
                    assert(exception is NotModifiedException)
                    Mono.empty()
                }.subscribe()
    }

    @Test
    fun testGetAllIssuesFromOwnerById() {
        val title1 = "title1"
        val title2 = "title2"
        val title3 = "title3"

        val issue1 = IssueModel(UUID.randomUUID(), title1, testOwnerUUID, testDeadline)
        val issue2 = IssueModel(UUID.randomUUID(), title2, testOwnerUUID, testDeadline)
        val issue3 = IssueModel(UUID.randomUUID(), title3, testOwnerUUID, testDeadline)

        var myIssues = mutableListOf(issue1, issue2, issue3)

        given(issueRepository.findByOwnerId(testOwnerUUID)).willReturn(Flux.fromIterable(myIssues))

        issueService.getAllIssuesFromOwnerById(testOwnerUUID).subscribe {
            assert(it.ownerId == testOwnerUUID)
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
    fun testGetAllIssuesFromOwnerByIdThrowsNotFoundException() {
        given(issueRepository.findByOwnerId(testOwnerUUID)).willReturn(Flux.empty())

        issueService.getAllIssuesFromOwnerById(testOwnerUUID)
                .onErrorResume { exception ->
                    assert(exception is NotFoundException)
                    Mono.empty()
                }.subscribe()
    }

    @Test
    fun testGetIssueWithAllComments() {
        val title1 = "title1"
        val issue1 = IssueModel(testIssueUUID, title1, testOwnerUUID, testDeadline)

        val commentContent1 = "content1"
        val commentContent2 = "content2"
        val commentContent3 = "content3"

        val comment1 = CommentModel(UUID.randomUUID(), commentContent1, issue1.id!!, testOwnerUUID)
        val comment2 = CommentModel(UUID.randomUUID(), commentContent2, issue1.id!!, testOwnerUUID)
        val comment3 = CommentModel(UUID.randomUUID(), commentContent3, issue1.id!!, testOwnerUUID)

        val myComments = mutableListOf(comment1, comment2, comment3)

        given(issueRepository.findById(testIssueUUID)).willReturn(Mono.just(issue1))
        given(commentRepository.findAllByIssueId(testIssueUUID)).willReturn(Flux.fromIterable(myComments))

        issueService.getIssueWithAllComments(testIssueUUID).subscribe { issueView ->
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