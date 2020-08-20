package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.model.UserModel
import de.thm.webservices.issuetracker.repository.UserRepository
import org.springframework.stereotype.Service
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
    fun tagging(text:String) : Mono<MutableSet<UUID>> {
        var words = text.split( " ")
                .filter { it.startsWith("@")}
        val matches : MutableSet<UUID> = mutableSetOf()

        return userRepository.findAll().collectList()
                .map { userList ->
                    for(word in words){
                        userList.map {user ->
                            if(user.username == word.substring(1)) {
                                matches.add(user.id!!)
                            }
                        }
                    }
                    matches
                }
    }
}