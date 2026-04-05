package dev.dhev.realworld.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("RealWorld API")
                .description("""
                    Conduit — a RealWorld-spec backend built with Spring Boot 3 and Java 21.

                    **Authentication:** Protected endpoints require a JWT in the `Authorization` header:
                    ```
                    Authorization: Token <your-jwt>
                    ```
                    Obtain a token via **Login** or **Register**.
                    """)
                .version("1.0.0"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT obtained from /api/users/login or /api/users")))
            .tags(List.of(
                new Tag().name("Articles").description("Article CRUD, listing, and feed"),
                new Tag().name("Comments").description("Comments on articles"),
                new Tag().name("Favorites").description("Favorite and unfavorite articles"),
                new Tag().name("Profile").description("User profiles and follow relationships"),
                new Tag().name("Tags").description("Global tag list"),
                new Tag().name("User and Authentication").description("Register, login, and manage the current user")
            ));
    }
}
