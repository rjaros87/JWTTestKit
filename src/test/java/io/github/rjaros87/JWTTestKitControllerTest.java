package io.github.rjaros87;


import com.nimbusds.jose.JOSEException;
import io.github.rjaros87.jwttestkit.model.TokenResponse;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Map;

@MicronautTest
class JWTTestKitControllerTest {

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void testApplicationIsRunning() {
        Assertions.assertTrue(application.isRunning());
    }

    @Test
    void testKeysEndpoint() {
        HttpResponse<Map> response = client.toBlocking().exchange("/JWTTestKit/keys", Map.class);
        Assertions.assertEquals(200, response.code());
        Map<String, Object> keys = response.body();
        Assertions.assertNotNull(keys);
        Assertions.assertNotNull(keys.get("private_key"));
        Assertions.assertNotNull(keys.get("public_key"));
        Assertions.assertTrue(keys.get("private_key").toString().startsWith("-----BEGIN PRIVATE KEY-----"));
        Assertions.assertTrue(keys.get("public_key").toString().startsWith("-----BEGIN PUBLIC KEY-----"));
    }

    @Test
    void testJwksEndpoint() {
        HttpResponse<Map> response = client.toBlocking().exchange("/JWTTestKit/jwks", Map.class);
        Assertions.assertEquals(200, response.code());
        Map<String, Object> jwks = response.body();
        Assertions.assertNotNull(jwks);
        Assertions.assertTrue(jwks.containsKey("keys"));
    }

    @Test
    void testSampleTokenEndpoint() {
        HttpResponse<Map> response = client.toBlocking().exchange("/JWTTestKit/token", Map.class);
        Assertions.assertEquals(200, response.code());
        Map<String, Object> token = response.body();
        Assertions.assertNotNull(token);
        Assertions.assertNotNull(token.get("access_token"));
    }

    @Test
    void testAwsCognitoTokenEndpoint() {
        Map<String, Object> cognitoToken = Map.of("sub", "test-sub", "email", "test@example.com");

        HttpRequest<Map> request = HttpRequest.POST("/JWTTestKit/token/aws-cognito", cognitoToken);
        HttpResponse<Map> response = client.toBlocking().exchange(request, Map.class);
        Assertions.assertEquals(200, response.code());
        Map<String, Object> token = response.body();
        Assertions.assertNotNull(token);
        Assertions.assertNotNull(token.get("access_token"));
    }

    @Test
    void testCreateCognitoTokenFromUrlEncoded_Success() throws JOSEException {
        Map<String, String> formParams = Map.of("scope", "test-scope");
        String authorizationHeader = "Basic " + Base64.getEncoder().encodeToString("clientId:secret".getBytes());

        HttpRequest<Map<String, String>> request = HttpRequest.POST("/JWTTestKit/token/aws-cognito", formParams)
                .header("Authorization", authorizationHeader)
                .contentType("application/x-www-form-urlencoded");

        HttpResponse<Map> response = client.toBlocking().exchange(request, Map.class);

        Assertions.assertEquals(200, response.code());
        Assertions.assertNotNull(response.body());
        Assertions.assertNotNull(response.body().get("access_token"));
    }

    @Test
    void testCreateCognitoTokenFromUrlEncoded_MissingScope() {
        Map<String, String> formParams = Map.of();
        String authorizationHeader = "Basic " + Base64.getEncoder().encodeToString("clientId:secret".getBytes());

        HttpRequest<Map<String, String>> request = HttpRequest.POST("/JWTTestKit/token/aws-cognito", formParams)
                .header("Authorization", authorizationHeader)
                .contentType("application/x-www-form-urlencoded");

        try {
            client.toBlocking().exchange(request, Map.class);
            Assertions.fail("Expected HttpClientResponseException to be thrown");
        } catch (HttpClientResponseException e) {
            Assertions.assertEquals(400, e.getResponse().code(), "Expected 400 Bad Request");
        }
    }

    @Test
    void testCreateCognitoTokenFromUrlEncoded_InvalidAuthorizationHeader() {
        Map<String, String> formParams = Map.of("scope", "test-scope");
        String authorizationHeader = "InvalidHeader";

        HttpRequest<Map<String, String>> request = HttpRequest.POST("/JWTTestKit/token/aws-cognito", formParams)
                .header("Authorization", authorizationHeader)
                .contentType("application/x-www-form-urlencoded");

        try {
            client.toBlocking().exchange(request, TokenResponse.class);
            Assertions.fail("Expected HttpClientResponseException to be thrown");
        } catch (HttpClientResponseException e) {
            Assertions.assertEquals(400, e.getResponse().code(), "Expected 400 Bad Request");
        }
    }

    @Test
    void testOktaTokenEndpoint() {
        Map<String, Object> oktaToken = Map.of("sub", "test-sub", "email", "test@example.com");

        HttpRequest<Map> request = HttpRequest.POST("/JWTTestKit/token/okta", oktaToken);
        HttpResponse<Map> response = client.toBlocking().exchange(request, Map.class);
        Assertions.assertEquals(200, response.code());
        Map<String, Object> token = response.body();
        Assertions.assertNotNull(token);
        Assertions.assertNotNull(token.get("access_token"));
    }

    @Test
    void testCustomTokenEndpoint_withoutSub() {
        Map<String, Object> customToken = Map.of();

        HttpRequest<Map> request = HttpRequest.POST("/JWTTestKit/token/custom", customToken);
        try {
            client.toBlocking().exchange(request, Map.class);
            Assertions.fail("Expected HttpClientResponseException to be thrown");
        } catch (HttpClientResponseException e) {
            Assertions.assertEquals(400, e.getStatus().getCode());
        }
    }

    @Test
    void testCustomTokenEndpoint() {
        Map<String, Object> customToken = Map.of("sub", "test-sub");

        HttpRequest<Map> request = HttpRequest.POST("/JWTTestKit/token/custom", customToken);
        HttpResponse<Map> response = client.toBlocking().exchange(request, Map.class);
        Assertions.assertEquals(200, response.code());

        Map<String, Object> token = response.body();
        Assertions.assertNotNull(token);
        Assertions.assertNotNull(token.get("access_token"));
    }
}
