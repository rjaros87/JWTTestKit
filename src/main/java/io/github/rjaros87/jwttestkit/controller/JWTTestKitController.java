package io.github.rjaros87.jwttestkit.controller;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import io.github.rjaros87.jwttestkit.model.Claims;
import io.github.rjaros87.jwttestkit.model.KeysResponse;
import io.github.rjaros87.jwttestkit.model.TokenResponse;
import io.github.rjaros87.jwttestkit.model.awscognito.AWSCognitoToken;
import io.github.rjaros87.jwttestkit.model.custom.CustomToken;
import io.github.rjaros87.jwttestkit.model.okta.OktaToken;
import io.github.rjaros87.jwttestkit.model.sample.SampleToken;
import io.github.rjaros87.jwttestkit.utils.JWTUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

/**
 * Controller for JWT token generation and key management.
 * This controller provides endpoints for generating various types of JWT tokens
 * and managing RSA keys used for token signing.
 */
@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/JWTTestKit")
@Tag(name = "JWT Test Kit", description = "APIs for JWT token generation and key management")
public class JWTTestKitController {

    private static final int RSA_KEY_SIZE = 2048;

    private final JWK jwk;
    private final JWSSigner signer;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    /**
     * Initializes the controller by generating RSA key pair and setting up the JWT signer.
     *
     * @throws JOSEException if there's an error during key generation or signer initialization
     */
    public JWTTestKitController() throws JOSEException {
        jwk = new RSAKeyGenerator(RSA_KEY_SIZE)
                .keyID(JWTUtils.generateKeyId())
                .generate();
        privateKey = jwk.toRSAKey().toPrivateKey();
        publicKey = jwk.toRSAKey().toRSAPublicKey();
        signer = new RSASSASigner(privateKey);
    }

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
        return new KeysResponse(JWTUtils.getPrivateKeyPem(privateKey), JWTUtils.getPublicKeyPem(publicKey));
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
        JWKSet jwkSet = new JWKSet(jwk.toPublicJWK());
        return jwkSet.toJSONObject();
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
        Claims claims = new SampleToken();
        return HttpResponse.ok(new TokenResponse(claims, jwk, signer));
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
        return HttpResponse.ok(new TokenResponse(body, jwk, signer));
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
        return HttpResponse.ok(new TokenResponse(body, jwk, signer));
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
        return HttpResponse.ok(new TokenResponse(body, jwk, signer));
    }
}