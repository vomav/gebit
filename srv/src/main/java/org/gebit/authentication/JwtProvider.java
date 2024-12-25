package org.gebit.authentication;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gebit.gen.db.UserTenantMappings;
import org.gebit.gen.db.Users;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;



@Component
public class JwtProvider {

    private static final Logger log = LogManager.getLogger(JwtProvider.class);
    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;


    public static final String ROLES_CLAIM = "roles";
    public static final String USERNAME_CLAIM = "username";
    public static final String SURNAME_CLAIM = "surename";
    public static final String USER_ID_CLAIM = "userId";
    public static final String TENANT_ID_CLAIM = "tenantId";
    
    public JwtProvider(
            @Value("${jwt.secret.access}") String jwtAccessSecret,
            @Value("${jwt.secret.refresh}") String jwtRefreshSecret
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }


    public String generateAccessToken(Users user, List<UserTenantMappings> permissions) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(accessExpiration)
                .claim(ROLES_CLAIM, permissions.stream().map(this::createRole).toList())
                .claim(USERNAME_CLAIM, user.getName())
                .claim(SURNAME_CLAIM, user.getSurname())
                .claim(TENANT_ID_CLAIM, user.getCurrentTenantId())
                .claim(USER_ID_CLAIM, user.getId())
                .signWith(jwtAccessSecret)
                .compact();
    }

    public String generateRefreshToken(Users user, List<UserTenantMappings> permissions) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(refreshExpiration)
                .claim(ROLES_CLAIM, permissions.stream().map(this::createRole).toList())
                .claim(USERNAME_CLAIM, user.getName())
                .claim(SURNAME_CLAIM, user.getSurname())
                .claim(TENANT_ID_CLAIM, user.getCurrentTenantId())
                .claim(USER_ID_CLAIM, user.getId())
                .signWith(jwtRefreshSecret)
                .compact();
    }

    private String createRole(UserTenantMappings permission) {
    	return permission.getTenant().getName() + ":" + permission.getTenantId() + ":" + permission.getMappingType();
    }
    
    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    @SuppressWarnings("deprecation")
	private boolean validateToken(String token, Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.warn("Token expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.warn("Unsupported jwt", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.warn("Malformed jwt", mjEx);
        } catch (SignatureException sEx) {
            log.warn("Invalid signature", sEx);
        } catch (Exception e) {
            log.warn("invalid token", e);
        }
        return false;
    }


    public Claims getAccessClaims( String token) {
        return getClaims(token, jwtAccessSecret);
    }


    public Claims getRefreshClaims( String token) {
        return getClaims(token, jwtRefreshSecret);
    }


    private Claims getClaims( String token, Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}

