package com.mailcursor.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${mailcursor.jwt.secret:MailCursorSecretKey2026}")
    private String secret;

    @Value("${mailcursor.jwt.expire-hours:24}")
    private long expireHours;

    public String createToken(Long userId, String username, boolean superAdmin) {
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + expireHours * 3600L * 1000L);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .claim("superAdmin", superAdmin)
                .setIssuedAt(now)
                .setExpiration(expireAt)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
