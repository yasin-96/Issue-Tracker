package de.thm.webservices.issuetracker.model

data class UserViewModel (
    val issues: MutableList<IssueModel> = mutableListOf(),
    val comments: MutableList<CommentModel> = mutableListOf()
)