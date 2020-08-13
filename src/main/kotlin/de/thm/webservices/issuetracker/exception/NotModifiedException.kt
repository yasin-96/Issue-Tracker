package de.thm.webservices.issuetracker.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class NotModifiedException : ResponseStatusException {
    constructor() : super(HttpStatus.NOT_MODIFIED)
    constructor(reason: String?) : super(HttpStatus.NOT_MODIFIED, reason)
    constructor(reason: String?, cause: Throwable?) : super(HttpStatus.NOT_MODIFIED, reason, cause)
}
