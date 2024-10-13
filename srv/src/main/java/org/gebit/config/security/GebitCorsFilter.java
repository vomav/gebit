package org.gebit.config.security;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class GebitCorsFilter extends CorsFilter {

	public GebitCorsFilter(CorsConfigurationSource source) {
		super((CorsConfigurationSource) source);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		response.addHeader("Access-Control-Allow-Headers",
				"Access-Control-Allow-Origin, Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, OData-Version, x-csrf-token, odata-maxversion");
//		
//		

//		if(response.getHeader("OData-Version") == null) {
//			response.setHeader("OData-Version", "4.0");
//		}
		
//		if(response.getHeader("OData-maxversion") == null) {
//			response.setHeader("OData-Version", "4.0");
//		}
		if (response.getHeader("Access-Control-Allow-Origin") == null)
			response.addHeader("Access-Control-Allow-Origin", "*");
		
		
		filterChain.doFilter(request, response);
	}

}
