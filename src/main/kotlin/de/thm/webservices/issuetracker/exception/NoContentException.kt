package de.thm.webservices.issuetracker.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class NoContentException : ResponseStatusException {
    constructor() : super(HttpStatus.NO_CONTENT)
    constructor(reason: String?) : super(HttpStatus.NO_CONTENT, reason)
    constructor(reason: String?, cause: Throwable?) : super(HttpStatus.NO_CONTENT, reason, cause)
}
