package de.thm.webservices.issuetracker.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("users")
data class User (
        @Id
        var id: UUID?,
        var username: String,
        var password: String,
        var role: String
)
