package com.example.multitenants.util;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.multitenants.model.UserModel;
import com.example.multitenants.util.Constants.JWT_CLAIM_KEY;

@Component
public class JwtUtil {
    @Value("${application.jwt.secret}")
    private String secret;
    @Value("${application.jwt.expiration}")
    private Long expiration;

    public String generateToken(UserModel user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("MY_APP")
                .withSubject(user.getUsername().toString())
                .withArrayClaim(JWT_CLAIM_KEY.CLAIM_KEY_ROLES.getValue(), convertRolesToArray(user.getAuthorities()))
                .withClaim(JWT_CLAIM_KEY.CLAIM_KEY_TENANT.getValue(), user.getTenant())
                .withAudience("front-end")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(algorithm);
    }

    private String[] convertRolesToArray(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().map(authority -> authority.getAuthority()).toArray(String[]::new);
    }

    public boolean verifyToken(String token) {
        JWTVerifier verifier = jwtVerifier();
        try {
            verifier.verify(token);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public <T> T getClaimValue(String claimKey, DecodedJWT decodedJWT, Class<T> clazz) {
        return decodedJWT.getClaim(claimKey).as(clazz);
    }

    public <T> T[] getArrayClaimValue(String claimKey, DecodedJWT decodedJWT, Class<T> clazz) {
        return decodedJWT.getClaim(claimKey).asArray(clazz);
    }

    public DecodedJWT getDecodedJWT(String authToken) {
        var verifier = jwtVerifier();
        var decoded = verifier.verify(authToken);
        return decoded;
    }

    public String getUserNameFromToken(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }

    private JWTVerifier jwtVerifier() {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .build();
        return verifier;
    }
}
