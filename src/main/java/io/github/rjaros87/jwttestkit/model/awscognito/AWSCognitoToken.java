package io.github.rjaros87.jwttestkit.model.awscognito;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.rjaros87.jwttestkit.model.Claims;
import io.github.rjaros87.jwttestkit.utils.Faker;
import io.github.rjaros87.jwttestkit.utils.JWTUtils;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.naming.SnakeCaseStrategy;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents an AWS Cognito token with various claims.
 */
@Introspected
@Serdeable(naming = SnakeCaseStrategy.class)
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AWSCognitoToken implements Claims {

    private static final Logger log = LoggerFactory.getLogger(AWSCognitoToken.class);

    @JsonProperty("exp")
    private Long exp = JWTUtils.getDefaultExpTime();

    @JsonProperty("sub")
    private String sub = UUID.randomUUID().toString();

    @JsonProperty("device_key")
    private String deviceKey = UUID.randomUUID().toString();

    @JsonProperty("cognito:groups")
    private List<String> cognitoGroups = List.of(Faker.randomWord());

    @JsonProperty("iss")
    private String iss = Faker.randomUrl();

    @JsonProperty("version")
    private Integer version = 123;

    @JsonProperty("client_id")
    private String clientId = Faker.randomText(10);

    @JsonProperty("origin_jti")
    private String originJti = UUID.randomUUID().toString();

    @JsonProperty("event_id")
    private String eventId = UUID.randomUUID().toString();

    @JsonProperty("token_use")
    private String tokenUse = "access";

    @JsonProperty("scope")
    private String scope = Faker.randomWord();

    @JsonProperty("auth_time")
    private Long authTime = JWTUtils.getDefaultIatTime();

    @JsonProperty("iat")
    private Long iat = JWTUtils.getDefaultIatTime();

    @JsonProperty("jti")
    private String jti = UUID.randomUUID().toString();

    @JsonProperty("username")
    private String username = Faker.randomWord();

    @JsonCreator
    public AWSCognitoToken(String sub, String deviceKey, List<String> cognitoGroups, String iss, Integer version,
                           String clientId, String originJti, String eventId, String tokenUse, String scope,
                           Long authTime, Long exp, Long iat, String jti, String username) {
        this.exp = Optional.ofNullable(exp).orElse(this.exp);
        this.sub = Optional.ofNullable(sub).orElse(this.sub);
        this.deviceKey = Optional.ofNullable(deviceKey).orElse(this.deviceKey);
        this.cognitoGroups = Optional.ofNullable(cognitoGroups).orElse(this.cognitoGroups);
        this.iss = Optional.ofNullable(iss).orElse(this.iss);
        this.version = Optional.ofNullable(version).orElse(this.version);
        this.clientId = Optional.ofNullable(clientId).orElse(this.clientId);
        this.originJti = Optional.ofNullable(originJti).orElse(this.originJti);
        this.eventId = Optional.ofNullable(eventId).orElse(this.eventId);
        this.tokenUse = Optional.ofNullable(tokenUse).orElse(this.tokenUse);
        this.scope = Optional.ofNullable(scope).orElse(this.scope);
        this.authTime = Optional.ofNullable(authTime).orElse(this.authTime);
        this.iat = Optional.ofNullable(iat).orElse(this.iat);
        this.jti = Optional.ofNullable(jti).orElse(this.jti);
        this.username = Optional.ofNullable(username).orElse(this.username);
    }

    @Override
    public Object objectToSign() {
        return this;
    }
}
