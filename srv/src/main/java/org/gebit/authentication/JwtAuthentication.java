package org.gebit.authentication;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtAuthentication implements Authentication {

    /**
     * Indicates whether the user is authenticated.
     */
    private boolean authenticated;

    /**
     * The username of the authenticated user.
     */
    private String login;

    /**
     * The first name of the authenticated user.
     */
    private String firstName;

    /**
     * The roles granted to the authenticated user.
     */
    private Set<SimpleGrantedAuthority> roles;

    /**
     * Constructor to initialize a JwtAuthentication instance with a username and a collection of roles.
     *
     * @param login the username of the authenticated user.
     * @param roles    a collection of roles granted to the user.
     */
    public JwtAuthentication(String login, Collection<String> roles) {
        this.login = login;
        this.roles = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return login;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return firstName;
    }
}