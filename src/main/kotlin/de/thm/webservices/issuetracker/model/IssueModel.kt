package de.thm.webservices.issuetracker.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("issues")
data class IssueModel(
        @Id var id: UUID?,
        var title: String,
        var ownerId: UUID,
        var deadline : String
)
