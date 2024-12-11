package org.gebit.services.searching.mt.isolatate;

public enum CrossTenantAccess {

	TENANT("Tenant"),
	USER_TENANT_MAPPINGS_ADMIN("admin"),
	USER_TENANT_MAPPINGS_USER("user"),
	USER_TENANT_MAPPINGS_BOTH("both"),
	NONE("None");
	
	private final String type;
	
	private CrossTenantAccess(String type) {
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}
	
	
	
	
}
