package de.thm.webservices.issuetracker.model.event

import java.util.*

data class TagStatsModel (
    var issue: UUID,
    var numberOfTags: Int
)