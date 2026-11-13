package com.classmonster.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * application.yml 의 jwt.* 설정.
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret, long accessTokenValiditySeconds) {
}
