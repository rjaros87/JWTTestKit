package io.github.rjaros87.jwttestkit.model.sample;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.rjaros87.jwttestkit.model.Claims;
import io.github.rjaros87.jwttestkit.utils.Faker;
import io.github.rjaros87.jwttestkit.utils.JWTUtils;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;

/**
 * Represents a sample token with various claims.
 */
@Log4j2
@Introspected
@Serdeable
@Getter
public class SampleToken implements Claims {

    @JsonProperty("exp")
    protected Long exp = JWTUtils.getDefaultExpTime();

    @JsonProperty("sub")
    private String sub = UUID.randomUUID().toString();

    @JsonProperty("iat")
    private Long iat = JWTUtils.getDefaultIatTime();

    @JsonProperty("name")
    private String name = Faker.randomText(10);

    @Override
    public Object objectToSign() {
        return this;
    }
}
