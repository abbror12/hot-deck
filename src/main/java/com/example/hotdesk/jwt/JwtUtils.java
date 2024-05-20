package com.example.hotdesk.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${hotDesk.security.jwt-secret}")
    private String key;

    public  String issueJwt (String phoneNumber) {
        return issueJwt (phoneNumber, Collections.emptyMap());
    }

    public String issueJwt(String phoneNumber, Map<String,Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(phoneNumber)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(6, ChronoUnit.HOURS)))
                .setIssuer("https://google.com")
                .signWith(signingWith())
                .compact();
    }

    private Key signingWith() {
        return Keys
                .hmacShaKeyFor(Base64.getEncoder().encode(key.getBytes()));
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingWith())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
