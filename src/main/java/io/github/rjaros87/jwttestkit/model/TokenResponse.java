package io.github.rjaros87.jwttestkit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * Represents a response containing a JWT token.
 */
@Log4j2
@Introspected
@Serdeable
@Getter
public class TokenResponse {

    @JsonProperty("token_type")
    private final String tokenType = "Bearer";

    @JsonProperty("access_token")
    private final String accessToken;

    @JsonProperty("expires_in")
    private final long expiresIn;

    /**
     * Constructs a new TokenResponse with the specified access token and expiration time.
     *
     * @param accessToken the access token
     * @param exp the expiration time in seconds
     */
    public TokenResponse(String accessToken, Long exp) {
        this.expiresIn = exp;
        this.accessToken = accessToken;
    }
}
