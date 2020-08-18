package de.thm.webservices.issuetracker.model

data class UserView (
    var issues: MutableList<IssueModel> = mutableListOf(),
    var comments: MutableList<CommentModel> = mutableListOf()
)