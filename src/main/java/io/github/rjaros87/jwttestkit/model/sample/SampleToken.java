package io.github.rjaros87.jwttestkit.model.sample;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.rjaros87.jwttestkit.utils.AbstractTokenHelper;
import io.github.rjaros87.jwttestkit.model.Claims;
import io.github.rjaros87.jwttestkit.utils.JWTUtils;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;

import java.util.UUID;

@Introspected
@Serdeable
@Getter
public class SampleToken extends AbstractTokenHelper implements Claims {

    @JsonProperty("exp")
    protected Long exp = JWTUtils.getDefaultExpTime();

    @JsonProperty("sub")
    private String sub = UUID.randomUUID().toString();

    @JsonProperty("iat")
    private Long iat = JWTUtils.getDefaultIatTime();

    @JsonProperty("name")
    private String name = FAKER.internet().username();
}
