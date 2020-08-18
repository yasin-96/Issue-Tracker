package de.thm.webservices.issuetracker.model

data class IssueViewModel (
        val issue: IssueModel,
        val comments: MutableList<CommentModel> = mutableListOf()
)