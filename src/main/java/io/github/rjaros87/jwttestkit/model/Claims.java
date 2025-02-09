package io.github.rjaros87.jwttestkit.model;

/**
 * Interface representing the required claims of a JWT token.
 */
public interface Claims {
    /**
     * Gets the expiration time of the token.
     *
     * @return the expiration time in seconds since the epoch
     */
    Long getExp();

    /**
     * Gets the subject of the token.
     *
     * @return the subject of the token
     */
    String getSub();

    /**
     * Gets the object to sign.
     *
     * @return the object to sign
     */
    Object objectToSign();
}
