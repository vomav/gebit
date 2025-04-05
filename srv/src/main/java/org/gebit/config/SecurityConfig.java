package org.gebit.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import java.util.Arrays;

import org.gebit.authentication.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("cloud")
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Configures the security filter chain.
     * <p>
     * This method sets up the security configurations such as CSRF disabling, session management policy,
     * authorization rules and adds the JWT filter after the UsernamePasswordAuthenticationFilter.
     * </p>
     *
     * @param http the HttpSecurity instance to configure.
     * @return the SecurityFilterChain instance.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	
        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(
                        authz -> authz
                                .requestMatchers(
                                        "/api/auth/login",
                                        "/api/auth/token",
                                        "/webjars/**",
                                        "/*.js",
                                        "/*.html/**",
                                        "/*.css",
                                        "/*.json",
                                        "/services",
                                        "/odata/v4/**",
                                        "/*",
                                        "/favicon.ico",
                                        "/model/uiModel.json",
                                        "/actuator/**"
                                ).permitAll()
                                .requestMatchers(CorsUtils:: isPreFlightRequest).permitAll()
                                .anyRequest().authenticated()	
                ).addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                
                .build();
    }
    
    @Bean
    CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("OData-Version", "OData-maxversion"));
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setExposedHeaders(Arrays.asList("OData-Version", "OData-maxversion"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);

    }

   
}
