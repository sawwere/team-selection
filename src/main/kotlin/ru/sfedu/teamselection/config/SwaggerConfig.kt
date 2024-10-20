package ru.sfedu.teamselection.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun publicApi(): GroupedOpenApi? {
        return GroupedOpenApi.builder()
            .group("team-selection")
            .pathsToMatch("/api/**")
            .build()
    }

    @Bean
    fun springOpenApi(): OpenAPI? {
        return OpenAPI()
            .addServersItem(Server().url(""))
            .info(
                Info().title("Api сервиса по подбору команд")
                    .description("Описание api взаимодействия с подбором команд")
                    .version("v0.0.1")
            )
    }
}