package dev.dhev.realworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import dev.dhev.realworld.security.JwtProperties;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(JwtProperties.class)
public class RealworldApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealworldApplication.class, args);
    }

}
