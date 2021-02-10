package org.gty.demo.config.properties;

import com.google.common.base.MoreObjects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

@ConstructorBinding
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties implements Serializable {

    @Serial
    private static final long serialVersionUID = 2913237737054759921L;

    private final String secret;
    private final String issuer;
    private final String authorizationHeader;
    private final Duration expiration;

    public JwtProperties(
        final String secret,
        final String issuer,
        final String authorizationHeader,
        final Duration expiration
    ) {
        this.secret = secret;
        this.issuer = issuer;
        this.authorizationHeader = authorizationHeader;
        this.expiration = expiration;
    }

    public String getSecret() {
        return secret;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getAuthorizationHeader() {
        return authorizationHeader;
    }

    public Duration getExpiration() {
        return expiration;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof JwtProperties)) return false;
        final var that = (JwtProperties) o;
        return Objects.equals(secret, that.secret) &&
            Objects.equals(issuer, that.issuer) &&
            Objects.equals(authorizationHeader, that.authorizationHeader) &&
            Objects.equals(expiration, that.expiration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(secret, issuer, authorizationHeader, expiration);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("secret", secret)
            .add("issuer", issuer)
            .add("authorizationHeader", authorizationHeader)
            .add("expiration", expiration)
            .toString();
    }
}