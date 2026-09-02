package com.practice.student_management.config;

import java.security.Key;
import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import io.jsonwebtoken.Claims;
import java.util.function.Function;

@Service
public class JwtService {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(String username, String role) {

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(
                    new Date(System.currentTimeMillis() + 3600000)
                )
                .signWith(key)
                .compact();
    }
    
    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }
    
    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        Claims claims = Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claimsResolver.apply(claims);
    }
    
    public boolean isTokenValid(String token, String username) {

        String extractedUsername = extractUsername(token);

        return extractedUsername.equals(username)
                && !isTokenExpired(token);
    }
    
    private boolean isTokenExpired(String token) {

        Date expiration = extractClaim(
                token,
                Claims::getExpiration
        );

        return expiration.before(new Date());
    }
//    @Bean
//    CommandLineRunner jwtTest(JwtService jwtService) {
//
//        return args -> {
//
//            String token =
//                    jwtService.generateToken("ashutosh");
//
//            System.out.println("JWT = " + token);
//        };
//    }
}