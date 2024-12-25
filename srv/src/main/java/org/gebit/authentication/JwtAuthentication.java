package org.gebit.authentication;


import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class JwtAuthentication implements Authentication {

    private static final long serialVersionUID = 1L;

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
    private String username;
    private String tenantId;
    private String surname;
    private String userId;
    
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
        return username;
    }

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
    
    
    
}