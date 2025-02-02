package io.github.rjaros87.jwttestkit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.jwk.JWK;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;

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

    public TokenResponse(Claims claims, JWK jwk, JWSSigner signer) throws JOSEException {
        expiresIn = claims.getExp();

        // Create signed JWT
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(jwk.getKeyID())
                .type(JOSEObjectType.JWT)
                .build();

        Payload payload = new Payload(claims.toMap());
        JWSObject jwsObject = new JWSObject(header, payload);

        jwsObject.sign(signer);

        accessToken = jwsObject.serialize();
    }
}
