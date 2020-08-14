package de.thm.webservices.issuetracker.controller

import de.thm.webservices.issuetracker.service.CommentService
import org.springframework.web.bind.annotation.RestController

@RestController("CommentController")
class CommentController(
        private val commentService: CommentService
) {

}