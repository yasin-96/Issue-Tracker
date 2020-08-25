package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.exception.ForbiddenException
import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.model.UserModel
import de.thm.webservices.issuetracker.repository.UserRepository
import de.thm.webservices.issuetracker.security.SecurityContextRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class TaggingService(
        private val userRepository: UserRepository,
        private val securityContextRepository: SecurityContextRepository
) {

    /**
     * Filter all usernames from text and match this will available user in database
     * @param text String Text to search tagged users
     * @return Mono<List<UserModel>>
     */
    fun tagging(text: String): Mono<List<UserModel>> {
        val words = text.split(" ")
                .filter { it.startsWith("@") }.toSet()
                .map { it.substring(1) }.toString()

        return userRepository.findAll().collectList()
                .map { listOfUser -> listOfUser.filter { user -> words.contains(user.username) } }
    }

    /**
     * Counts the number of linked persons within an issue
     * @param comments List<CommentModel>
     * @return Mono<Int>
     */
    fun countAllTaggedUsersInComments(comments: List<CommentModel>): Mono<Int> {
        return Flux.zip(
                securityContextRepository.getAuthenticatedUser(),
                Flux.fromIterable(comments)
        )
                .filter{
                    it.t1.hasAdminRights()
                }
                .switchIfEmpty(Mono.error(ForbiddenException()))
                .map{
                    it.t2
                }
                .flatMap { tagging(it.content) }
                .map { list -> list.toSet() }.collectList()
                .map {
                    it.flatMap { setOfUser ->
                        setOfUser.map { user -> user }
                    }.toSet()
                }
                .map { it.count() }
    }
}