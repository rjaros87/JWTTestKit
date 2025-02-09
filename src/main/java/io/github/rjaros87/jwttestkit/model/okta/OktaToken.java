package io.github.rjaros87.jwttestkit.model.okta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.rjaros87.jwttestkit.model.Claims;
import io.github.rjaros87.jwttestkit.utils.Faker;
import io.github.rjaros87.jwttestkit.utils.JWTUtils;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents an Okta token with various claims.
 */
@Introspected
@Serdeable
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OktaToken implements Claims {

    @JsonProperty("sub")
    private String sub = UUID.randomUUID().toString();

    @JsonProperty("name")
    private String name = Faker.randomWord();

    @JsonProperty("preferred_username")
    private String preferredUsername = Faker.randomText(5);

    @JsonProperty("email")
    private String email = Faker.randomEmailAddress();

    @JsonProperty("groups")
    private List<String> groups = List.of(Faker.randomText(5));

    @JsonProperty("exp")
    private Long exp = JWTUtils.getDefaultExpTime();

    @JsonProperty("iat")
    private Long iat = JWTUtils.getDefaultIatTime();

    @JsonProperty("iss")
    private String iss = Faker.randomUrl();

    @JsonProperty("aud")
    private String aud = Faker.randomUrl();

    @JsonCreator
    public OktaToken(String sub, String name, String preferredUsername, String email, List<String> groups, Long exp,
                    Long iat, String iss, String aud) {
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

    @Override
    public Object objectToSign() {
        return this;
    }
}
