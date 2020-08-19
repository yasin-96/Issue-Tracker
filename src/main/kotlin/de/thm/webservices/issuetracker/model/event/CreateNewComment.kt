package de.thm.webservices.issuetracker.model.event

import java.util.*

data class CreateNewComment(
        val issueId: UUID
)