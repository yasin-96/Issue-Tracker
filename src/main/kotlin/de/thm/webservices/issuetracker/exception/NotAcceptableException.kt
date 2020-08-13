package de.thm.webservices.issuetracker.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class NotAcceptableException : ResponseStatusException {
    constructor() : super(HttpStatus.NOT_ACCEPTABLE)
    constructor(reason: String?) : super(HttpStatus.NOT_ACCEPTABLE, reason)
    constructor(reason: String?, cause: Throwable?) : super(HttpStatus.NOT_ACCEPTABLE, reason, cause)
}
