package com.example.springsecurity.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private static final String SECRET_KEY = "556B58703273357638792F423F4528482B4D6251655468576D5A713374367739";
    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    public <T> T extractClaim(String jwt, Function<Claims, T> claimsResolve)
    {
       final Claims claims = extractAllClaims(jwt);
       return claimsResolve.apply(claims);
    }


    public String generateToken(UserDetails user)
    {
        Map<String, Object> claim = new HashMap<>();
        claim.put("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));
        return generateToken(claim, user);
    }

    public String refreshToken(UserDetails user)
    {
        return refreshToken(new HashMap<>(), user);
    }
    private String generateToken(Map<String, Object> extractClaim, UserDetails user)
    {
        return Jwts
                .builder()
                .setClaims(extractClaim)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String refreshToken(Map<String, Object> extractClaim, UserDetails user)
    {
        return Jwts
                .builder()
                .setClaims(extractClaim)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //Check token is valid
    public boolean isTokenValid(String jwt, UserDetails user)
    {
        final String username = extractUsername(jwt);
        return (username.equals(user.getUsername())) && !isTokenExpired(jwt);
    }
    private boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }
    private Date extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration);
    }


    private Claims extractAllClaims(String jwt)
    {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyByte = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyByte);
    }


}
