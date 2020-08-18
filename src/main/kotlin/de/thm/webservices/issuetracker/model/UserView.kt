package de.thm.webservices.issuetracker.model

data class UserView (
    val issues: MutableList<IssueModel> = mutableListOf(),
    val comments: MutableList<CommentModel> = mutableListOf()
)