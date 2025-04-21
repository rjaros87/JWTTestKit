package io.github.rjaros87;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@OpenAPIDefinition(
    info = @Info(
        title = "JWT Test Kit API",
        version = "${api.version}",
        description = "API for generating and managing JWT tokens for testing purposes. " +
                "Provides endpoints for creating various types of JWT tokens including AWS Cognito, " +
                "Okta, and custom tokens, as well as managing RSA keys for token signing.",
        license = @License(name = "Apache 2.0", url = "https://github.com/rjaros87/JWTTestKit/blob/main/LICENSE"),
        contact = @Contact(
            name = "Radoslaw Jaros",
            url = "https://github.com/rjaros87/JWTTestKit"
        )
    ),
    tags = {
        @Tag(name = "JWT Test Kit", description = "APIs for JWT token generation and key management")
    },
    servers = {
        @Server(url = "http://localhost:8080", description = "Local Development Server")
    }
)
@Log4j2
public class Application {
    public static void main(String[] args) {
        Properties properties = new Properties();
        try (InputStream input = Application.class.getClassLoader()
                .getResourceAsStream("micronaut-version.properties")) {
            if (input != null) {
                properties.load(input);
                log.info("Micronaut version: {}", properties.getProperty("micronaut.version", "Unknown"));
            }
        } catch (IOException e) {
            log.warn("Unable to find Micronaut version in properties file: {}", e.getMessage());
        }

        Micronaut
            .build(args)
            .mainClass(Application.class)
            .banner(false)
            .start();


    }
}
