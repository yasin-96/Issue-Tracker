package de.thm.webservices.issuetracker.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class FlywayConfig(env: Environment) {
    //private val env: Environment

//    @Bean(initMethod = "migrate")
//    fun flyway(): Flyway {
//        return Flyway(Flyway.configure()
//                .baselineOnMigrate(true)
//                .dataSource(
//                        env.getRequiredProperty("spring.flyway.url"),
//                        env.getRequiredProperty("spring.flyway.user"),
//                        env.getRequiredProperty("spring.flyway.password"))
//        )
//    }
//
//    init {
//        this.env = env
//    }
}