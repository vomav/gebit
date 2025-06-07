package org.gebit.authentication.exceptions;

public class UserNotVerifiedException extends RuntimeException{

	private static final long serialVersionUID = 7399744635666567318L;
	private String tenantId;
	private String userId;
	
	public UserNotVerifiedException(String tenantId, String userId) {
		this.tenantId = tenantId;
		this.userId = userId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	
	
}

