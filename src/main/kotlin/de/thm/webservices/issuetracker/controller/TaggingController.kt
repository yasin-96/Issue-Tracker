package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.model.UserModel
import de.thm.webservices.issuetracker.service.TaggingService
import org.springframework.web.bind.annotation.RestController

@RestController
class TaggingController(private val taggingService: TaggingService) {

    fun tagging(text:String) : MutableList<UserModel>{
        val words = text.split( " ")
                .filter {
                    it.trim().startsWith("@")
                }


        return taggingService.tagging(words)

        }

    }
