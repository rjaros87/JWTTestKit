package io.github.rjaros87.jwttestkit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Introspected
@Serdeable
@AllArgsConstructor
@Getter
public class KeysResponse {

    @JsonProperty("private_key")
    private String privateKey;

    @JsonProperty("public_key")
    private String publicKey;
}
