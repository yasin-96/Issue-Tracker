package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.model.UserModel
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*


@Service
class TaggingService(private val userService: UserService) {

    fun tagging(text:String) : Mono<MutableSet<UUID>> {
        var words = text.split( " ")
                .filter {
                    it.startsWith("@")
                }

        val matches : MutableSet<UUID> = mutableSetOf()

        return userService.getAll().collectList()
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