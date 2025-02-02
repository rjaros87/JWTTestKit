package io.github.rjaros87.jwttestkit.controller;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import io.github.rjaros87.jwttestkit.model.Claims;
import io.github.rjaros87.jwttestkit.model.KeysResponse;
import io.github.rjaros87.jwttestkit.model.TokenResponse;
import io.github.rjaros87.jwttestkit.model.awscognito.AWSCognitoToken;
import io.github.rjaros87.jwttestkit.model.custom.CustomToken;
import io.github.rjaros87.jwttestkit.model.okta.OktaToken;
import io.github.rjaros87.jwttestkit.model.sample.SampleToken;
import io.github.rjaros87.jwttestkit.utils.JWTUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/JWTTestKit")
public class JWTTestKitController {

    private static final int RSA_KEY_SIZE = 2048;

    private final JWK jwk;
    private final JWSSigner signer;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JWTTestKitController() throws JOSEException {
        jwk = new RSAKeyGenerator(RSA_KEY_SIZE)
                .keyID(JWTUtils.generateKeyId())
                .generate();
        privateKey = jwk.toRSAKey().toPrivateKey();
        publicKey = jwk.toRSAKey().toRSAPublicKey();
        signer = new RSASSASigner(privateKey);
    }

    @Get("/keys")
    public KeysResponse keys() {
        return new KeysResponse(JWTUtils.getPrivateKeyPem(privateKey), JWTUtils.getPublicKeyPem(publicKey));
    }

    @Get("/jwks")
    public Map<String, Object> jwks() {
        JWKSet jwkSet = new JWKSet(jwk.toPublicJWK());
        return jwkSet.toJSONObject();
    }

    @Get("/token")
    @Consumes(MediaType.APPLICATION_JSON)
    public HttpResponse<TokenResponse> generateSampleToken() throws JOSEException {
        Claims claims = new SampleToken();
        return HttpResponse.ok(new TokenResponse(claims, jwk, signer));
    }

    @Post("/token/aws-cognito")
    @Consumes(MediaType.APPLICATION_JSON)
    public HttpResponse<TokenResponse> createCognitoToken(@Body AWSCognitoToken body) throws JOSEException {
        return HttpResponse.ok(new TokenResponse(body, jwk, signer));
    }

    @Post("/token/okta")
    @Consumes(MediaType.APPLICATION_JSON)
    public HttpResponse<TokenResponse> createOktaToken(@Body OktaToken body) throws JOSEException {
        return HttpResponse.ok(new TokenResponse(body, jwk, signer));
    }

    @Post("/token/custom")
    @Consumes(MediaType.APPLICATION_JSON)
    public HttpResponse<TokenResponse> createCustomToken(@Body CustomToken body) throws JOSEException {
        return HttpResponse.ok(new TokenResponse(body, jwk, signer));
    }
}