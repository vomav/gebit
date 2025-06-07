package org.gebit.services.registration.handler.utils;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class OTPGenerator {

	public Integer generateOTP() {
        Random random = new Random();
        int number = 0;

        for (int i = 0; i < 6; i++) {
            int digit = random.nextInt(10); // 0 to 9
            number = number * 10 + digit;
        }

        return number;
	}
}
