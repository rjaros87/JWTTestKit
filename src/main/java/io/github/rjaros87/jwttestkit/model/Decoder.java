package io.github.rjaros87.jwttestkit.model;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.Map;

/**
 * Decoder class for decoding JWT tokens.
 * It uses the Nimbus JOSE + JWT library to parse and decode JWT tokens.
 */
@Slf4j
@Singleton
public class Decoder {

    /**
     * Decodes a JWT token and returns its header and payload as a map.
     *
     * @param token the JWT token to decode
     * @return a map containing the header and payload of the JWT
     */
    public Map<String, Map<String, Object>> decode(String token) throws ParseException {
        JWT jwt = JWTParser.parse(token);
        return Map.of(
            "header", jwt.getHeader().toJSONObject(),
            "payload", jwt.getJWTClaimsSet().toJSONObject()
        );
    }
}
