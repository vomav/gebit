package org.gebit.authentication;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;

@Component
public class JwtUtils {


        public static JwtAuthentication generate(Claims claims) {
            // Extract the username from the claims
            String email = claims.getSubject();
            // Extract the roles list from the claims
            List<?> rolesObjectList = claims.get("roles", List.class);
            // Convert the roles list to a list of strings
            List<String> roles = rolesObjectList.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
            String surname = claims.get(JwtProvider.SURNAME_CLAIM, String.class);
            String username = claims.get(JwtProvider.USERNAME_CLAIM, String.class);
            String tenantId = claims.get(JwtProvider.TENANT_ID_CLAIM, String.class);
            String userId = claims.get(JwtProvider.USER_ID_CLAIM, String.class);
            // Create and return a JwtAuthentication object with the extracted information
            JwtAuthentication authentication = new JwtAuthentication(email, roles);
            authentication.setSurname(surname);
            authentication.setUsername(username);
            authentication.setTenantId(tenantId);
            authentication.setUserId(userId);
            return authentication;
        }
    }

