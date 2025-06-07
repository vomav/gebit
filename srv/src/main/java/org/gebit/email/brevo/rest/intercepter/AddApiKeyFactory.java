package org.gebit.email.brevo.rest.intercepter;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class AddApiKeyFactory {

	
	private String apiKey;
	public AddApiKeyFactory(@Value("${email.brevo.api.key}") String brevoApiKey) {
		this.apiKey = brevoApiKey;
	}
	
	public List<ClientHttpRequestInterceptor> build() {
		return List.of(new ClientHttpRequestInterceptor() {

			@Override
			public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
					throws IOException {
				request.getHeaders().add("api-key", apiKey);
				request.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
				return execution.execute(request, body);
			}
			
		});
	}
	
}
