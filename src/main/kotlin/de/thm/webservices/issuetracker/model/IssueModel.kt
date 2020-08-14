package de.thm.webservices.issuetracker.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("ISSUES")
data class IssueModel(
        @Id var id: UUID?,
        var title: String,
        var owner: String,
        var deadline : String
)
