package io.github.rjaros87.jwttestkit.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import io.github.rjaros87.jwttestkit.model.Claims;
import io.github.rjaros87.jwttestkit.model.KeysResponse;
import io.github.rjaros87.jwttestkit.model.TokenResponse;
import io.micronaut.context.annotation.Context;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

/**
 * Utility class for signing JWT tokens.
 */
@Log4j2
@Getter
@Context
@Singleton
public class TokenSigner {
    private static final int RSA_KEY_SIZE = 2048;

    private final ObjectMapper objectMapper;
    private final JWK jwk;
    private final JWSSigner signer;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    /**
     * Constructs a new TokenSigner with the specified ObjectMapper.
     *
     * @param objectMapper the ObjectMapper to use for converting objects to JSON
     * @throws JOSEException if an error occurs while generating the RSA keys
     */
    public TokenSigner(ObjectMapper objectMapper) throws JOSEException {
        this.objectMapper = objectMapper;
        jwk = new RSAKeyGenerator(RSA_KEY_SIZE)
                .keyID(JWTUtils.generateKeyId())
                .generate();
        privateKey = jwk.toRSAKey().toPrivateKey();
        publicKey = jwk.toRSAKey().toRSAPublicKey();
        signer = new RSASSASigner(privateKey);
    }

    /**
     * Signs the specified token and returns a TokenResponse containing the signed JWT.
     *
     * @param token the token to sign
     * @return a TokenResponse containing the signed JWT
     * @throws JOSEException if an error occurs while signing the token
     * @throws IllegalArgumentException if the token's sub claim is null
     */
    public TokenResponse sign(@NotNull Claims token) throws JOSEException, IllegalArgumentException {
        if (token.getSub() == null) {
            throw new IllegalArgumentException("Token sub cannot be null");
        }

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
            .keyID(jwk.getKeyID())
            .type(JOSEObjectType.JWT)
            .build();

        Map<String, Object> claims = toMap(token);

        log.debug("Claims to sign: {}", claims);

        Payload payload = new Payload(claims);
        JWSObject jwsObject = new JWSObject(header, payload);

        jwsObject.sign(signer);

        String accessToken = jwsObject.serialize();

        log.debug("Generated accessToken: {}", accessToken);

        return new TokenResponse(accessToken, token.getExp());
    }

    /**
     * Retrieves the JSON Web Key Set (JWKS) containing the public key.
     *
     * @return a Map containing the JWKS in JSON format
     */
    public Map<String, Object> getJwks() {
        JWKSet jwkSet = new JWKSet(jwk.toPublicJWK());
        return jwkSet.toJSONObject();
    }

    /**
     * Retrieves the RSA key pair in PEM format.
     *
     * @return a KeysResponse containing both private and public keys in PEM format
     */
    public KeysResponse getKeys() {
        return new KeysResponse(JWTUtils.getPrivateKeyPem(privateKey),
            JWTUtils.getPublicKeyPem(publicKey));
    }

    /**
     * Converts the specified token to a map of claims.
     *
     * @param token the token to convert
     * @return a map of claims
     */
    private Map<String, Object> toMap(Claims token) {
        return objectMapper.convertValue(token.objectToSign(), new TypeReference<Map<String, Object>>() {});
    }
}
