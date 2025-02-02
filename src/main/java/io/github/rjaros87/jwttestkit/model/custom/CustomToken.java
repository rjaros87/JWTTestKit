package io.github.rjaros87.jwttestkit.model.custom;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import io.github.rjaros87.jwttestkit.utils.AbstractTokenHelper;
import io.github.rjaros87.jwttestkit.model.Claims;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Introspected
@Serdeable
@Getter
public class CustomToken extends AbstractTokenHelper implements Claims {

    private final Map<String, Object> customClaims = new HashMap<>();;

    @JsonAnySetter
    public void addCustomClaim(String key, Object value) {
        customClaims.put(key, value);
    }

    @Override
    public Map<String, Object> toMap() {
        return objectMapper.convertValue(customClaims, Map.class);
    }

    @Override
    public Long getExp() {
        return customClaims.containsKey("exp") ? Long.parseLong(customClaims.get("exp").toString()) : -1;
    }
}
