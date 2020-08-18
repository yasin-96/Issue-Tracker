package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.model.UserModel
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class TaggingService(private val userService: UserService) {

    fun tagging(text:String) : Mono<MutableSet<UserModel>>{
        var words = text.split( " ")
                .filter {
                    it.startsWith("@")
                }

        val matches : MutableSet<UserModel> = mutableSetOf()

        return userService.getAll().collectList()
                .map {
                    for(word in words){
                        it.map {
                            if(it.username == word.substring(1)) {
                                matches.add(it)
                            }
                        }

                    }
                    matches
                }

    }
}