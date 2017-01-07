package com.szymon.service;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class TokenService {

    public void validateToken(String token, String secret) throws MalformedJwtException, SignatureException, ExpiredJwtException {
        getClaimsFromToken(token, secret);
    }

    public Jws<Claims> getClaimsFromToken(String token, String secret) throws SignatureException, MalformedJwtException, ExpiredJwtException {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token);
    }

    public String updateExpiration(String token, String secret, Date date) {
        Jws<Claims> claims = getClaimsFromToken(token, secret);

        return Jwts.builder()
                .setExpiration(date)
                .setIssuedAt(claims.getBody().getIssuedAt())
                .claim("userId", claims.getBody().get("userId"))
                .claim("login", claims.getBody().get("login"))
                .claim("role", claims.getBody().get("role"))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
}
