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
import java.util.*

@SpringBootApplication
class IssuetrackerApplication {

	companion object {
		final var topicExchangeName:String = "spring-boot-exchange"
		final var queueName : String = "spring-boot"
	}

	@Bean
	fun queue() : Queue {
		return Queue(queueName,false)
	}

	@Bean
	fun exchange():TopicExchange {
		return TopicExchange(topicExchangeName)
	}

	@Bean
	fun binding(queue: Queue) : Binding {
		return BindingBuilder.bind(queue).to(exchange()).with("foo.bar.#")
	}

	@Bean
	fun container(connectionFactory:ConnectionFactory,
				  listenerAdapter: MessageListenerAdapter) : SimpleMessageListenerContainer {

		var container : SimpleMessageListenerContainer = SimpleMessageListenerContainer()
		container.connectionFactory = connectionFactory
		container.setQueueNames(queueName)
		container.setMessageListener(listenerAdapter)
		return container
	}

	@Bean
	fun listenerAdapter(receiver:Receiver) : MessageListenerAdapter {
		return MessageListenerAdapter(receiver,"receiveMessage")
	}

	fun main(args: Array<String>) {
		runApplication<IssuetrackerApplication>(*args)
	}
}
