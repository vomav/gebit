package org.gebit.email;

import org.gebit.email.pojo.OTPEmailRequest;
import org.gebit.email.pojo.TemporaryPasswordRequest;

public interface IEmailService {

	void sendOneTimePassword(OTPEmailRequest otp);

	void sendTemparyPassword(TemporaryPasswordRequest request);

}