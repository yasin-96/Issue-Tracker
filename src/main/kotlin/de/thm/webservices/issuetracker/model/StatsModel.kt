package de.thm.webservices.issuetracker.model

import java.util.*

data class StatsModel (
        val userId: UUID,
        var numberOfIssues: Int,
        var numberOfComments: Int
)