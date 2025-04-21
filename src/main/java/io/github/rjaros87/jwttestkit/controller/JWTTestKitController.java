package io.github.rjaros87.jwttestkit.controller;

import com.nimbusds.jose.JOSEException;
import io.github.rjaros87.jwttestkit.model.Decoder;
import io.github.rjaros87.jwttestkit.model.KeysResponse;
import io.github.rjaros87.jwttestkit.model.TokenResponse;
import io.github.rjaros87.jwttestkit.model.awscognito.AWSCognitoToken;
import io.github.rjaros87.jwttestkit.model.custom.CustomToken;
import io.github.rjaros87.jwttestkit.model.okta.OktaToken;
import io.github.rjaros87.jwttestkit.model.sample.SampleToken;
import io.github.rjaros87.jwttestkit.utils.TokenSigner;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;

import java.text.ParseException;
import java.util.Base64;
import java.util.Map;

/**
 * Controller for JWT token generation and key management.
 * This controller provides endpoints for generating various types of JWT tokens
 * and managing RSA keys used for token signing.
 */
@Secured(SecurityRule.IS_ANONYMOUS)
@Log4j2
@Controller("/JWTTestKit")
@Tag(name = "JWT Test Kit", description = "APIs for JWT token generation and key management")
public class JWTTestKitController {

    @Inject
    private TokenSigner tokenSigner;

    @Inject
    private Decoder decoder;

    /**
     * Retrieves the RSA key pair in PEM format.
     *
     * @return KeysResponse containing both private and public keys in PEM format
     */
    @Operation(
        summary = "Get RSA Keys",
        description = "Retrieves the RSA key pair in PEM format for token signing and verification",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved RSA keys",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = KeysResponse.class)
                )
            )
        }
    )
    @Get("/keys")
    public KeysResponse keys() {
        return tokenSigner.getKeys();
    }

    /**
     * Retrieves the JSON Web Key Set (JWKS) containing the public key.
     *
     * @return Map containing the JWKS in JSON format
     */
    @Operation(
        summary = "Get JWKS",
        description = "Retrieves the JSON Web Key Set (JWKS) containing the public key for token verification",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved JWKS",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Map.class)
                )
            )
        }
    )
    @Get("/jwks")
    public Map<String, Object> jwks() {
        return tokenSigner.getJwks();
    }

    /**
     * Decodes a JWT token and returns its header and payload.
     *
     * @param rawJwt the JWT token to decode
     * @return Map containing the header and payload of the JWT
     */
    @Operation(
            summary = "Decode JWT",
            description = "Decodes a JWT and returns its header and payload components. Works with both signed and unsigned tokens."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully decoded token",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    examples = {
                            @ExampleObject(
                                    name = "Decoded Token",
                                    value = """
                    {
                        "header": {
                            "alg": "RS256",
                            "typ": "JWT"
                        },
                        "payload": {
                            "sub": "user123",
                            "iss": "https://example.com",
                            "exp": 1700000000,
                            "iat": 1600000000
                        }
                    }
                    """,
                                    description = "Example of decoded JWT structure"
                            )
                    }
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid JWT token format",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Post("/decode")
    @Consumes(MediaType.TEXT_PLAIN)
    public HttpResponse<Map<String, Map<String, Object>>> decodeToken(
            @Parameter(
                    description = "Decode JWT",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN,
                            schema = @Schema(type = "string"),
                            examples = {
                                    @ExampleObject(
                                            name = "JWT Token",
                                            value = "eyJhbGciOiJub25lIn0.eyJzdWIiOiJ1c2VyMTIzIn0.",
                                            description = "Raw JWT token string"
                                    )
                            }
                    )
            )
            @Body String rawJwt) {
        try {
            return HttpResponse.ok(decoder.decode(rawJwt));
        } catch (ParseException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return HttpResponse.badRequest();
        }
    }

    /**
     * Generates a sample JWT token with predefined claims.
     *
     * @return HttpResponse containing the generated token
     * @throws JOSEException if there's an error during token signing
     */
    @Operation(
        summary = "Generate Sample Token",
        description = "Generates a sample JWT token with predefined claims",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully generated token",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = TokenResponse.class)
                )
            )
        }
    )
    @Get("/token")
    @Consumes(MediaType.APPLICATION_JSON)
    public HttpResponse<TokenResponse> generateSampleToken() throws JOSEException {
        return HttpResponse.ok(tokenSigner.sign(new SampleToken()));
    }

    /**
     * Creates a JWT token with AWS Cognito-compatible claims.
     *
     * @param body AWS Cognito token claims
     * @return HttpResponse containing the generated token
     * @throws JOSEException if there's an error during token signing
     */
    @Operation(
        summary = "Create AWS Cognito Token",
        description = "Creates a JWT token with AWS Cognito-compatible claims. Requires at least an empty JSON object {} as request body.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully generated token",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = TokenResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request - missing or malformed request body",
                content = @Content(mediaType = MediaType.APPLICATION_JSON)
            )
        }
    )
    @Post("/token/aws-cognito")
    @Consumes(MediaType.APPLICATION_JSON)
    public HttpResponse<TokenResponse> createCognitoToken(
        @Parameter(
            description = "AWS Cognito token claims (at least empty JSON {})",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = AWSCognitoToken.class),
                examples = {
                    @ExampleObject(
                        name = "Empty request",
                        value = "{}",
                        description = "Minimum required request body"
                    ),
                    @ExampleObject(
                        name = "Full request",
                        value = """
                        {
                            "sub": "user123",
                            "client_id": "6f092efa-d035-403a-97da-7c9123c9e620",
                            "scope": "my-scope",
                            "username": "John_Doe"
                        }
                        """,
                        description = "Request with all available fields - see AWS Cognito documentation for more details"
                    )
                }
            )
        )
        @Body AWSCognitoToken body) throws JOSEException {
        return HttpResponse.ok(tokenSigner.sign(body));
    }

    /**
     * Creates a JWT token with AWS Cognito-compatible claims from URL-encoded form data.
     * <p>
     * This endpoint expects form parameters and a Basic Authorization header.
     * The `scope` parameter is required in the form data, and the `clientId` is extracted
     * from the Basic Authorization header.
     *
     * @param formParams Map containing form parameters, including the required `scope`
     * @param authorizationHeader Basic Authorization header containing the clientId and secret
     * @return HttpResponse containing the generated token or an error response
     * @throws JOSEException if there's an error during token signing
     */
    @Operation(
        summary = "Create AWS Cognito Token (URL-encoded)",
        description = "Creates a JWT token with AWS Cognito-compatible claims from URL-encoded form data. " +
                "Requires a `scope` parameter in the form data and a Basic Authorization header.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully generated token",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = TokenResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request - missing or malformed form data or Authorization header",
                content = @Content(mediaType = MediaType.APPLICATION_JSON)
            )
        }
    )
    @Post("/token/aws-cognito")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<TokenResponse> createCognitoTokenFromUrlEncoded(
        @Parameter(
            description = "Form parameters containing the required `scope`",
            required = true,
            content = @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED),
            schema = @Schema(implementation = Map.class),
            examples = {
                @ExampleObject(
                    name = "Form parameters",
                    value = "scope=my-scope",
                    description = "Form data with required `scope` parameter"
                )
            }
        )
        @Body Map<String, String> formParams,
        @Parameter(
                description = "Basic Authorization header containing the clientId and clientSecret",
                required = true,
                content = @Content(
                    mediaType = MediaType.TEXT_PLAIN,
                    schema = @Schema(type = "string"),
                    examples = {
                        @ExampleObject(
                            name = "Basic Authorization",
                            value = "Basic Y2xpZW50SWQ6c2VjcmV0",
                            description = "Base64-encoded clientId:clientSecret"
                        )
                    }
                )
        )
        @Header("Authorization") String authorizationHeader) throws JOSEException {

        if (formParams.isEmpty() || authorizationHeader == null) {
            return HttpResponse.badRequest();
        }

        String scope = formParams.get("scope");

        // Extract Cognito clientId
        String[] basicAuthorization = decodeBasicAuth(authorizationHeader).split(":");
        if (basicAuthorization.length != 2 || scope == null) {
            return HttpResponse.badRequest();
        }

        String clientId = basicAuthorization[0];

        return HttpResponse.ok(tokenSigner.sign(new AWSCognitoToken(clientId, scope)));
    }

    /**
     * Creates a JWT token with Okta-compatible claims.
     *
     * @param body Okta token claims
     * @return HttpResponse containing the generated token
     * @throws JOSEException if there's an error during token signing
     */
    @Operation(
        summary = "Create Okta Token",
        description = "Creates a JWT token with Okta-compatible claims. Requires at least an empty JSON object {} as request body.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully generated token",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = TokenResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request - missing or malformed request body",
                content = @Content(mediaType = MediaType.APPLICATION_JSON)
            )
        }
    )
    @Post("/token/okta")
    @Consumes(MediaType.APPLICATION_JSON)
    public HttpResponse<TokenResponse> createOktaToken(
        @Parameter(
            description = "Okta token claims (at least empty JSON {})",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = OktaToken.class),
                examples = {
                    @ExampleObject(
                        name = "Empty request",
                        value = "{}",
                        description = "Minimum required request body"
                    ),
                    @ExampleObject(
                        name = "Full request",
                        value = """
                        {
                            "sub": "user123",
                            "email": "user@example.com",
                            "name": "John Doe",
                            "groups": ["Users", "Admins"]
                        }
                        """,
                        description = "Request with all available fields - see Okta documentation for more details"
                    )
                }
            )
        )
        @Body OktaToken body) throws JOSEException {
        return HttpResponse.ok(tokenSigner.sign(body));
    }

    /**
     * Creates a JWT token with custom claims.
     *
     * @param body Custom token claims
     * @return HttpResponse containing the generated token
     * @throws JOSEException if there's an error during token signing
     */
    @Operation(
        summary = "Create Custom Token",
        description = "Creates a JWT token with custom claims. Requires at least an empty JSON object {} as request body.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully generated token",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = TokenResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request - missing or malformed request body",
                content = @Content(mediaType = MediaType.APPLICATION_JSON)
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error generating token",
                content = @Content(mediaType = MediaType.APPLICATION_JSON)
            )
        }
    )
    @Post("/token/custom")
    @Consumes(MediaType.APPLICATION_JSON)
    public HttpResponse<TokenResponse> createCustomToken(
        @Parameter(
            description = "Custom token claims (at least empty JSON {})",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = CustomToken.class),
                examples = {
                    @ExampleObject(
                        name = "Empty request",
                        value = "{}",
                        description = "Minimum required request body"
                    ),
                    @ExampleObject(
                        name = "Full request",
                        value = """
                        {
                            "sub": "user123",
                            "customClaim1": "value1",
                            "customClaim2": "value2",
                            "roles": ["role1", "role2"]
                        }
                        """,
                        description = "Request with example custom claims"
                    )
                }
            )
        )
        @Body CustomToken body) throws JOSEException {
        try {
            return HttpResponse.ok(tokenSigner.sign(body));
        } catch (IllegalArgumentException e) {
            log.error("Error generating custom token due to: {}", e.getMessage());
            return HttpResponse.badRequest();
        }
    }

    private String decodeBasicAuth(String authorizationHeader) {
        String base64Credentials = authorizationHeader.substring("Basic ".length());
        byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
        return new String(decodedBytes);
    }
}