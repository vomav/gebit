package org.gebit.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import java.util.Arrays;

import org.gebit.authentication.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
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
                                        "/odata/v4/srv.registration/**",
                                        "/odata/v4/srv.ui_service/$metadata**",
                                        "/odata/v4/srv.searching/$metadata**",
                                        "/odata/v4/srv.admin/$metadata**",
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
    
//    @Bean
//    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//    	
//        return http.csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
//                .authorizeHttpRequests(
//                        authz -> authz
//		                        .requestMatchers(request -> !request.getRequestURI().contains("$metadata*")).permitAll()
//		                        .requestMatchers(
//		                        		"/*",
//		                        		"/api/auth/login",
//		                                "/api/auth/token").permitAll()
//		                        .requestMatchers(CorsUtils:: isPreFlightRequest).permitAll()
//                                .requestMatchers(
//                                    	"/odata/v4/**"
//                                    ).authenticated()
//		                        .anyRequest().permitAll()
//
//
//                                
//                                
//                                	
//                ).addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
//                
//                .build();
//    }
    
    
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
