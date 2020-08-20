package de.thm.webservices.issuetracker.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Table("comments")
data class CommentModel(
        @Id var id: UUID?,
        var content: String,
        var userId: UUID,
        var issueId: UUID
) {
    val creation: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}