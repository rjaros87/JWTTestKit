package io.github.rjaros87.jwttestkit.model.custom;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import io.github.rjaros87.jwttestkit.model.Claims;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a custom token with dynamic claims.
 */
@Introspected
@Serdeable
@Getter
public class CustomToken implements Claims {

    private final Map<String, Object> customClaims = new HashMap<>();

    /**
     * Adds a custom claim to the token.
     *
     * @param key the claim key
     * @param value the claim value
     */
    @JsonAnySetter
    public void addCustomClaim(String key, Object value) {
        customClaims.put(key, value);
    }

    @Override
    public Long getExp() {
        return customClaims.containsKey("exp") ? Long.parseLong(customClaims.get("exp").toString()) : -1;
    }

    @Override
    public String getSub() {
        return customClaims.containsKey("sub") ? customClaims.get("sub").toString() : null;
    }

    @Override
    public Object objectToSign() {
        return customClaims;
    }
}
