package de.thm.webservices.issuetracker.config

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FlywayConfig(
        @Value("spring.datasource.url") private val url: String,
        @Value("spring.datasource.username") private val user: String,
        @Value("spring.datasource.password=") private val password: String
) {
    @Bean(initMethod = "migrate")
    fun flyway(): Flyway {
        return Flyway(Flyway.configure()
                .baselineOnMigrate(true)
                .dataSource( url, user, password)
        )
    }
}