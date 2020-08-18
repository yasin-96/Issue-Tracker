package de.thm.webservices.issuetracker

import de.thm.webservices.issuetracker.receiver.Receiver
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
class IssuetrackerApplication {


}

fun main(args: Array<String>) {
	runApplication<IssuetrackerApplication>(*args)
}
