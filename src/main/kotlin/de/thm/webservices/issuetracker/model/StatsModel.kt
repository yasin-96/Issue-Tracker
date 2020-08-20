package de.thm.webservices.issuetracker.model

import java.util.*

data class StatsModel (
        var uuid: UUID,
        var numberOfIssues : Int ,
        var numberOfComments : Int
)