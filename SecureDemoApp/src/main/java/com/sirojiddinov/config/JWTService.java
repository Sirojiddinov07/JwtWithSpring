package com.sirojiddinov.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JWTService {
    private static final String SECRET_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJmaXJzdE5hbWUiOiJzYXZyb24iLCJsYXN0TmFtZSI6IlNhdnJvbiIsInBhc3N3b3JkIjoiMTIzIiwiZW1haWwiOiJzYXZyb25AZ21haWwuY29tIn0.1Uiz8B4mx4Y_MWpBCsdyuiIjA0vqvrHo_z70p9cRW5w\n";

    public String extractUsername(String token) {
        return extractClaims(token, Claims :: getSubject);
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }


            public String generateToken(
            Map<String, Objects> extraClaims,
            UserDetails userDetails
    ){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() * 1000 * 60 * 24))
                .signWith(getSignKey(), SignatureAlgorithm.ES256)
                .compact();
    }


    public boolean isValidToken(String token, UserDetails userDetails){
        final String userName = extractUsername(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return etctExperation(token).before(new Date());
    }

    private Date etctExperation(String token) {
        return extractClaims(token,Claims :: getExpiration);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte [] key = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(key);
    }
}
