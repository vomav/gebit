package org.gebit.authentication;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;


@Component
public class JwtFilter extends GenericFilterBean {
    private static final String AUTHORIZATION = "Authorization";
    private final JwtProvider jwtProvider;

    public JwtFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }


    /**
         * Intercepts a request, extracts and validates the JWT token, and sets the authentication.
         *
         * @param request  the servlet request.
         * @param response the servlet response.
         * @param fc       the filter chain.
         * @throws IOException      if an I/O error occurs during this filter's processing of the request.
         * @throws ServletException if the processing fails for any other reason.
         */
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc)
            throws IOException, ServletException {
            final String token = getTokenFromRequest((HttpServletRequest) request);
            if (token != null && jwtProvider.validateAccessToken(token)) {
                final Claims claims = jwtProvider.getAccessClaims(token);
                final JwtAuthentication jwtInfoToken = JwtUtils.generate(claims);
                jwtInfoToken.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(jwtInfoToken);
            }
            fc.doFilter(request, response);
        }

        /**
         * Extracts the JWT token from the request header.
         *
         * @param request the HTTP servlet request.
         * @return the JWT token if present, null otherwise.
         */
        private String getTokenFromRequest(HttpServletRequest request) {
            final String bearer = request.getHeader(AUTHORIZATION);
            if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
                return bearer.substring(7);
            }
            return null;
        }
}
