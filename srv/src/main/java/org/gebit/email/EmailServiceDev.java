package org.gebit.email;

import org.gebit.email.pojo.OTPEmailRequest;
import org.gebit.email.pojo.TemporaryPasswordRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class EmailServiceDev implements IEmailService{
	

	public void sendOneTimePassword(OTPEmailRequest otp) {
		System.out.println("sendOneTimePassword sent " + otp.passcode());
	}
	
	public void sendTemparyPassword(TemporaryPasswordRequest request) {
		System.out.println("sendTemparyPassword sent");
	}
}
