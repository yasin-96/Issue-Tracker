package de.thm.webservices.issuetracker.service

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
        const val testUserId: String = "22222222-1111-1111-1111-111111111111"
        const val otherUserId: String = "33333333-1111-1111-1111-111111111111"
        const val testTitle = "test Title"
        const val testDeadline = "2020-12-01"
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
        val id = UUID.randomUUID()
        val ownerId = UUID.randomUUID()

        val expectedIssue = IssueModel(id, testTitle, ownerId, testDeadline)

        given(issueRepository.findById(id)).willReturn(Mono.just(expectedIssue))

        val serviceReturned = issueService.getIssueById(id)

        Mockito.verify(issueRepository.findById(id))

        assert(serviceReturned == Mono.just(expectedIssue))
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

        val authUser = AuthenticatedUser(
                testUserId,
                listOf()
        )

        given(securityContextRepository.getAuthenticatedUser()).willReturn(Mono.just(authUser))
        given(authUser.hasRightsOrIsAdmin(newIssueModel.ownerId)).willReturn(true)
        given(issueRepository.save(newIssueModel)).willReturn(Mono.just(returnedIssue))
        given(issueTemplate.convertAndSend("amq.topic",
                returnedIssue.id.toString() + ".news",
                CreateNewIssue(returnedIssue.id!!)))

    }

    @Test
    fun testDeleteIssue(){
        val testUUID = UUID.randomUUID()

        issueService.deleteIssue(testUUID)

    }
}