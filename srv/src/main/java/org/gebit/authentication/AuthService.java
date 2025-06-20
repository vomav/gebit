package org.gebit.authentication;

import java.util.List;

import org.gebit.authentication.dto.JwtRequest;
import org.gebit.authentication.dto.JwtResponse;
import org.gebit.authentication.exceptions.UserNotVerifiedException;
import org.gebit.authentication.exceptions.WrongPasswordException;
import org.gebit.common.user.repository.UserRepository;
import org.gebit.gen.db.UserTenantMappings;
import org.gebit.gen.db.Users;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sap.cds.services.ServiceException;

import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;


/**
 * Service class for handling authentication-related operations.
 * <p>
 * This service provides methods for user login, access token generation, refresh token generation,
 * and obtaining authentication information from the security context.
 * </p>
 *
 * @Service               - Indicates that an annotated class is a service component.
 * @RequiredArgsConstructor - Lombok annotation to generate a constructor for all final fields,
 *                           with parameter order same as field order.
 *
 * @author A-R
 * @version 1.0
 * @since 1.0
 */
@Service
public class AuthService {


    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository userRepository, JwtProvider jwtProvider, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.encoder = encoder;
    }

    public JwtResponse login(JwtRequest authRequest) throws AuthException {
        final Users user = userRepository.findUserByEmail(authRequest.getLogin())
                .orElseThrow(() -> new AuthException("user.isnot.found"));
        
        if(user.getIsActivated() == Boolean.FALSE) {
        	throw new UserNotVerifiedException(user.getCurrentTenantId(), user.getId());
        }
        
        if (encoder.matches(authRequest.getPassword(), user.getPassword())) {
        	List<UserTenantMappings> permissions = userRepository.getUserPermission(user);
            final String accessToken = jwtProvider.generateAccessToken(user,permissions);
            final String refreshToken = jwtProvider.generateRefreshToken(user,permissions);
            user.setRefreshToken(refreshToken);
            userRepository.updateUser(user);
            return new JwtResponse(accessToken, refreshToken);
        } else {
            throw new WrongPasswordException(authRequest.getLogin());
        }
    }

    public JwtResponse getAccessToken(String refreshToken) throws AuthException {
        // Validate the provided refresh token
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            // Extract claims from the refresh token
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            // Get the user login from the token claims
            final String login = claims.getSubject();
            // Fetch the user data
            final Users user = userRepository.findUserByEmail(login)
                    .orElseThrow(() -> new AuthException("User is not found"));
            List<UserTenantMappings> permissions = userRepository.getUserPermission(user);
            // Retrieve the stored refresh token for the user
            final String savedRefreshToken = user.getRefreshToken();
            // Compare the stored refresh token with the provided token
            if (savedRefreshToken != null && savedRefreshToken.equals(refreshToken)) {
                // Generate a new access token
                final String accessToken = jwtProvider.generateAccessToken(user, permissions);
                // Return a JwtResponse with the new access token
                return new JwtResponse(accessToken, null);
            }
        }
        throw  new AuthException("Validation failed");
    }

    public JwtResponse refresh(String refreshToken) throws AuthException {
        // Validate the provided refresh token
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            // Extract claims from the refresh token
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            // Get the user login from the token claims
            final String login = claims.getSubject();
            // Fetch the user data
            final Users user = userRepository.findUserByEmail(login)
                    .orElseThrow(() -> new AuthException("User is not found"));
            List<UserTenantMappings> permissions = userRepository.getUserPermission(user);
            // Retrieve the stored refresh token for the user
            final String savedRefreshToken = user.getRefreshToken();
            // Compare the stored refresh token with the provided token
            if (savedRefreshToken != null && savedRefreshToken.equals(refreshToken)) {
                // Generate new access and refresh tokens
                final String accessToken = jwtProvider.generateAccessToken(user, permissions);
                final String newRefreshToken = jwtProvider.generateRefreshToken(user, permissions);
                // Update the stored refresh token for the user
                user.setRefreshToken(newRefreshToken);
                userRepository.updateUser(user);
                // Return a JwtResponse with the new access and refresh tokens
                return new JwtResponse(accessToken, newRefreshToken);
            }
        }
        // Throw an AuthException if validation fails
        throw new AuthException("Invalid JWT token");
    }


    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

}
