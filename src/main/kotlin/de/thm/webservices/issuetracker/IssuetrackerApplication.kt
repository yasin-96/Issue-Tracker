package de.thm.webservices.issuetracker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class IssuetrackerApplication


fun main(args: Array<String>) {
	runApplication<IssuetrackerApplication>(*args)
}
