package org.gebit.email.brevo;

import java.util.List;

import org.gebit.client.rest.RestTemplateFactory;
import org.gebit.email.brevo.pojo.SendEmailRequestPayload;
import org.gebit.email.brevo.pojo.SendEmailRequestPayload.Recipient;
import org.gebit.email.brevo.pojo.SendEmailRequestPayload.Sender;
import org.gebit.email.brevo.pojo.SendEmailResponsePayload;
import org.gebit.email.brevo.rest.intercepter.AddApiKeyFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cds.services.ServiceException;

@Component
public class EmailClient {

	private RestTemplate restTemplate;
	private String url;
	private String senderEmail;
	private String senderName;
	private ObjectMapper objectMapper;
	private Boolean enabled;

	public EmailClient(RestTemplateFactory restTemplateFactory, AddApiKeyFactory brevoIRestTemplateInterceptorFactory,
			@Value("${email.brevo.api.url}") String url, @Value("${email.brevo.sender.name}") String senderName,
			@Value("${email.brevo.sender.email}") String senderEmail, @Value("${email.enabled}") Boolean enabled) {
		this.restTemplate = restTemplateFactory.addInteceptors(brevoIRestTemplateInterceptorFactory.build()).build();
		this.url = url;
		this.senderName = senderName;
		this.senderEmail = senderEmail;
		this.objectMapper = new ObjectMapper();
		this.enabled = enabled;
	}

	public String sendEmail(String to, String subject, String body) {
		
		if(enabled == Boolean.FALSE) {
			return "";
		}
		
		ResponseEntity<SendEmailResponsePayload> responseEntity;
		try {
			responseEntity = restTemplate.postForEntity(url,
					this.objectMapper.writeValueAsString(this.buildPayloadObject(to, subject, body)),
					SendEmailResponsePayload.class);
			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				return responseEntity.getBody().getMessageId();
			}
		} catch (RestClientException | JsonProcessingException e) {
			new ServiceException(e);
			e.printStackTrace();
		}

		return "";
	}

	private SendEmailRequestPayload buildPayloadObject(String to, String subject, String body) {
		SendEmailRequestPayload payload = new SendEmailRequestPayload();
		payload.setSubject(subject);
		payload.setSender(buildSender());
		payload.setTo(buildRecipients(to));
		payload.setHtmlContent(body);
		return payload;
	}

	private List<Recipient> buildRecipients(String to) {
		Recipient r = new Recipient();
		r.setEmail(to);
		return List.of(r);
	}

	private Sender buildSender() {
		Sender sender = new Sender();
		sender.setEmail(senderEmail);
		sender.setName(senderName);
		return sender;
	}
}
