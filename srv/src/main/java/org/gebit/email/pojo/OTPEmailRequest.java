package org.gebit.email.pojo;

public record OTPEmailRequest(String to, Integer passcode, String language) {

}
