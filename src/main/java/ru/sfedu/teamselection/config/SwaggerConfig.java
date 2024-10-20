package ru.sfedu.teamselection.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "TeamSelection Api",
                description = "Api приложения для поиска команд для проектной деятельности ", version = "0.0.1"
        )
)
public class SwaggerConfig {
    @Server
    @Bean
    public List<io.swagger.v3.oas.models.servers.Server> servers(@Value("app.publicUrl") String url) {
        io.swagger.v3.oas.models.servers.Server server = new io.swagger.v3.oas.models.servers.Server();
        server.url(url);
        server.description("Main api");
        return List.of(server);
    }
}
