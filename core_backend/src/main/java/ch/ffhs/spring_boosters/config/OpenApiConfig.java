package ch.ffhs.spring_boosters.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        //Alles verfügbar über: http://localhost:8081/swagger-ui/index.html
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boosters API")
                        .description("API-Dokumentation für die Spring Boosters Impfplan-Anwendung")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FFHS Spring Boosters Team")
                                .email("support@springboosters.ch"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8000").description("API Gateway"),
                        new Server().url("http://localhost:8081").description("Core Backend direkt")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Authorization header mit Bearer Schema. Beispiel: \"Authorization: Bearer {token}\"")));
    }
}
