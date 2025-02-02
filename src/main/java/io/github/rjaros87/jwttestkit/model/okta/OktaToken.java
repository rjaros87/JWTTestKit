package io.github.rjaros87.jwttestkit.model.okta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.rjaros87.jwttestkit.utils.AbstractTokenHelper;
import io.github.rjaros87.jwttestkit.model.Claims;
import io.github.rjaros87.jwttestkit.utils.JWTUtils;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Introspected
@Serdeable
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OktaToken extends AbstractTokenHelper implements Claims {

    @JsonProperty("sub")
    private String sub = UUID.randomUUID().toString();

    @JsonProperty("name")
    private String name = FAKER.name().fullName();

    @JsonProperty("preferred_username")
    private String preferredUsername = FAKER.internet().username();

    @JsonProperty("email")
    private String email = FAKER.internet().emailAddress();

    @JsonProperty("groups")
    private List<String> groups = List.of(FAKER.lorem().word());

    @JsonProperty("exp")
    private Long exp = JWTUtils.getDefaultExpTime();

    @JsonProperty("iat")
    private Long iat = JWTUtils.getDefaultIatTime();

    @JsonProperty("iss")
    private String iss = FAKER.internet().url();

    @JsonProperty("aud")
    private String aud = FAKER.internet().url();

    @JsonCreator
    public OktaToken(@JsonProperty("sub") String sub,
                     @JsonProperty("name") String name,
                     @JsonProperty("preferred_username") String preferredUsername,
                     @JsonProperty("email") String email,
                     @JsonProperty("groups") List<String> groups,
                     @JsonProperty("exp") Long exp,
                     @JsonProperty("iat") Long iat,
                     @JsonProperty("iss") String iss,
                     @JsonProperty("aud") String aud) {
        this.sub = Optional.ofNullable(sub).orElse(this.sub);
        this.name = Optional.ofNullable(name).orElse(this.name);
        this.preferredUsername = Optional.ofNullable(preferredUsername).orElse(this.preferredUsername);
        this.email = Optional.ofNullable(email).orElse(this.email);
        this.groups = Optional.ofNullable(groups).orElse(this.groups);
        this.exp = Optional.ofNullable(exp).orElse(this.exp);
        this.iat = Optional.ofNullable(iat).orElse(this.iat);
        this.iss = Optional.ofNullable(iss).orElse(this.iss);
        this.aud = Optional.ofNullable(aud).orElse(this.aud);
    }
}
