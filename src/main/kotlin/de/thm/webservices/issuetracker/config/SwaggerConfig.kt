/*
package de.thm.webservices.issuetracker.config

import org.springframework.context.annotation.Bean
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.regex

@EnableSwagger2
class SwaggerConfig {
    @Bean
    fun docket(apiInfo: ApiInfo?): Docket? {
        return Docket(DocumentationType.SWAGGER_2)
                .groupName("user-api")
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo)
                .select().paths(regex("/api/.*"))
                .build()
    }

    @Bean
    fun apiInfo(): ApiInfo? {
        return ApiInfoBuilder()
                .title("User API")
                .description("API for fetching user related information")
                .version("1.0.0")
                .build()
    }

    @Bean
    fun uiConfiguration(): UiConfiguration? {
        return UiConfigurationBuilder.builder()
                .deepLinking(true)
                .validatorUrl(null)
                .build()
    }
}


*/
