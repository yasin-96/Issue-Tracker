package de.thm.webservices.issuetracker.service

import de.thm.webservices.issuetracker.model.UserModel
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TaggingService(private val userService: UserService) {

    fun tagging(words:List<String>) : Mono<MutableList<UserModel>>{
        val matches : MutableList<UserModel> = mutableListOf()

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