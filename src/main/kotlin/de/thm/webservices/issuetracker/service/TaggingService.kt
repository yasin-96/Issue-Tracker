package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.model.CommentModel
import de.thm.webservices.issuetracker.model.UserModel
import de.thm.webservices.issuetracker.repository.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class TaggingService(
        private val userRepository: UserRepository
) {

    /**
     * Filter all usernames from text and match this will available user in database
     * @param text String Text to search tagged users
     * @return Mono<MutableSet<UUID>>
     */
    fun tagging(text: String): Mono<MutableSet<UUID>> {
        val words = text.split(" ")
                .filter { it.startsWith("@") }
        val matches: MutableSet<UUID> = mutableSetOf()

        //TODO user prüfen??
        return userRepository.findAll().collectList()
                .map { userList ->
                    for (word in words) {
                        userList.map { user ->
                            if (user.username == word.substring(1)) {
                                matches.add(user.id!!)
                            }
                        }
                    }
                    matches
                }
    }

    /**
     * Filter all usernames from text and match this will available user in database
     * @param text String Text to search tagged users
     * @return Mono<List<UserModel>>
     */
    fun taggingV2(text: String): Mono<List<UserModel>> {
        val words = text.split(" ")
                .filter { it.startsWith("@") }.toSet()
                .map { it.substring(1) }.toString()

        //TODO user prüfen??
        return userRepository.findAll().collectList()
                .map { listOfUser -> listOfUser.filter { user -> words.contains(user.username) } }
    }

    /**
     * Counts the number of linked persons within an issue
     * @param comments List<CommentModel>
     * @return Mono<Int>
     */
    fun countAllTaggedUsersInComments(comments: List<CommentModel>): Mono<Int> {
        return Flux.fromIterable(comments)
                .flatMap { taggingV2(it.content) }
                .map { list -> list.toSet() }.collectList()
                .map {
                    it.flatMap { setOfUser ->
                        setOfUser.map { user -> user }
                    }.toSet()
                }
                .map { it.count() }
    }
}