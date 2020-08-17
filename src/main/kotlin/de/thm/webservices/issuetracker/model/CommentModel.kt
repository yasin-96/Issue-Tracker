package de.thm.webservices.issuetracker.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("comments")
data class CommentModel(
        @Id var id: UUID?,
        var content: String,
        var userId: UUID?,
        var issueId: UUID?,
        var creation: String
)