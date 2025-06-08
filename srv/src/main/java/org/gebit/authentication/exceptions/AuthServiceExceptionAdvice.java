package org.gebit.authentication.exceptions;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthServiceExceptionAdvice {

	@ExceptionHandler(UserNotVerifiedException.class)
	@ResponseStatus(value = HttpStatus.FAILED_DEPENDENCY)
	public Map<String,String> handleUserNotVerifiedExeption(UserNotVerifiedException ex) {
		return Map.of("code","USER_NOT_VERIFIED","message", "User is not verified","tenant", ex.getTenantId(), "userId", ex.getUserId());
		
	}
	
	@ExceptionHandler(WrongPasswordException.class)
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	public Map<String,String> handleWrongPasswordExeption(WrongPasswordException ex) {
		return Map.of("code","WRONG_PASSWORD","message", "User is not verified", "login", ex.getEmail());
		
	}
	
}
	