package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.exception.ForbiddenException
import de.thm.webservices.issuetracker.exception.NoContentException
import de.thm.webservices.issuetracker.exception.NotFoundException
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
import reactor.core.publisher.Mono
import java.lang.NullPointerException
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
        val testUUID = UUID.randomUUID()
        val testOwnerID = UUID.randomUUID()
        const val testUserId: String = "22222222-1111-1111-1111-111111111111"
        const val otherUserId: String = "33333333-1111-1111-1111-111111111111"
        const val testTitle = "test Title"
        const val testDeadline = "2020-12-01"

        val authUser= AuthenticatedUser(
                testUUID.toString(),
                listOf()
        )

        val otherAuthUser= AuthenticatedUser(
                otherUserId,
                listOf()
        )
    }

    private val issueService = IssueService(
            issueRepository,
            commentRepository,
            issueTemplate,
            taggingService,
            securityContextRepository
    )

    @Test
    fun testShouldGetIssueById(){
        val expectedIssue = IssueModel(testUUID, testTitle, testOwnerID, testDeadline)

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

        val expectedIssue = IssueModel(testUUID, testTitle, testOwnerID, testDeadline)

        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.just(authUser))
        given(issueRepository.findById(testUUID)).willReturn(Mono.just(expectedIssue))

        given(issueRepository.deleteById(testUUID)).willReturn(Mono.empty())

        issueService.deleteIssue(testUUID).subscribe{
            Mockito.verify(issueRepository).findById(testUUID)
            Mockito.verify(issueRepository).deleteById(testUUID)
        }
    }

    @Test()
    fun testShouldDeleteIssueThrowForbiddenException(){

        val expectedIssue = IssueModel(testUUID, testTitle, testOwnerID, testDeadline)

        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.just(otherAuthUser))
        given(issueRepository.findById(testUUID)).willReturn(Mono.just(expectedIssue))
        given(issueRepository.deleteById(testUUID)).willReturn(Mono.empty())

        issueService.deleteIssue(testUUID)
                .onErrorResume { exception ->
                    assert(exception is ForbiddenException)
                    Mono.empty()
                }
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

    }


}