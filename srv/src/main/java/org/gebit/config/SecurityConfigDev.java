package org.gebit.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

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
@Profile("dev")
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfigDev {
    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    private final JwtFilter jwtFilter;

    public SecurityConfigDev(JwtFilter jwtFilter) {
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
                                        "/odata/v4/srv.registration/**",
                                        "/odata/v4/srv.ui_service/$metadata**",
                                        "/odata/v4/srv.searching/$metadata**",
                                        "/odata/v4/srv.admin/$metadata**",
                                        "/*",
                                        "/favicon.ico",
                                        "/model/uiModel.json",
                                        "/actuator/**",
                                        "/odata/v4/srv.publicSearching/**",
                                        "/odata/v4/srv.globalAdmin/$metadata"
                                ).permitAll()
                                .requestMatchers(CorsUtils:: isPreFlightRequest).permitAll()
                                .anyRequest().authenticated()	
                ).addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                
                .build();
    }
    
    @Bean
    CorsFilter corsFilter() {

        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");  // Allow all origins
        config.addAllowedMethod("*");  // Allow all HTTP methods
        config.addAllowedHeader("*");  // Allow all headers
        config.setAllowCredentials(false);  // Disable credentials for universal access

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

   
}
