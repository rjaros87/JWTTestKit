package io.github.rjaros87.jwttestkit.controller;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;

/**
 * Controller for handling secret-related endpoints.
 */
@Slf4j
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/secret")
@Tag(name = "Secret", description = "Endpoints for testing generated JWT.")
@SecuritySchemes({
    @SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
    )
})
public class SecretController {

    /**
     * Retrieves a secret message for an authenticated user.
     *
     * @param principal the authenticated user's principal
     * @return a secret message
     */
    @Operation(
        summary = "Retrieve Secret Message",
        description = "Retrieves a secret message for an authenticated user. JWT requires at least 'sub' claim.",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved secret message",
                content = @Content(
                    schema = @Schema(implementation = String.class)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - user is not authenticated",
                content = @Content(mediaType = MediaType.APPLICATION_JSON)
            )
        }
    )
    @Get
    public String secret(Principal principal) {
        log.info("Principal: {}", principal.getName());
        return "Secret message";
    }
}