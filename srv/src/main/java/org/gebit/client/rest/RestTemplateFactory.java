package org.gebit.client.rest;

import java.util.List;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateFactory {

	public RestTemplate client;
	
	public RestTemplateFactory() {
		client = new RestTemplate();
	}
	
	
	public RestTemplateFactory addInteceptors(List<ClientHttpRequestInterceptor> interceptors) {
		this.client.setInterceptors(interceptors);
		return this;
	}
	
	public RestTemplate build() {
		return this.client;
	}
}
