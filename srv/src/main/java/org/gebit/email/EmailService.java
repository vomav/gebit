package org.gebit.email;

import org.gebit.email.brevo.EmailClient;
import org.gebit.email.pojo.OTPEmailRequest;
import org.gebit.email.pojo.TemporaryPasswordRequest;
import org.springframework.stereotype.Component;

@Component
public class EmailService {
	
	private static final String OTP_HTML_BODY_TEMPLATE = "<html><body><h1>Your OTP is %d</h1></body></html>";
	
	private static final String OTP_SUBJECT = "You One Time Password";

	private static final String TEMP_PASSWORD_HTML_BODY_TEMPLATE = "<html><body><h1>Your Temporary Password is %s</h1></body></html>";
	
	private static final String TEMP_PASSWORD_SUBJECT = "You One Temporary Password";
	
	
	private EmailClient emailClient;

	public EmailService(EmailClient emailClient) {
		this.emailClient = emailClient;
	}

	public void sendOneTimePassword(OTPEmailRequest otp) {
		String body = String.format(OTP_HTML_BODY_TEMPLATE, otp.passcode());
		emailClient.sendEmail(otp.to(), OTP_SUBJECT, body);
	}
	
	public void sendTemparyPassword(TemporaryPasswordRequest request) {
		String body = String.format(TEMP_PASSWORD_HTML_BODY_TEMPLATE, request.password());
		emailClient.sendEmail(request.to(), TEMP_PASSWORD_SUBJECT, body);
	}
}
