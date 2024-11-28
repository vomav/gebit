package org.gebit.authentication;

import com.sap.cds.services.ServiceException;

public class CrossTenantPermissions {

	private String tenantName;
	private String tenantId;
	private String mappingType;

	private CrossTenantPermissions() {
		super();
	}

	private CrossTenantPermissions(String tenantName, String tenantId, String mappingType) {
		super();
		this.tenantName = tenantName;
		this.tenantId = tenantId;
		this.mappingType = mappingType;
	}

	public String getTenantName() {
		return tenantName;
	}

	public String getTenantId() {
		return tenantId;
	}

	public String getMappingType() {
		return mappingType;
	}

	
	public static CrossTenantPermissions create(String role) {
		String [] parts = role.split(":");
		if(parts.length != 3) {
			throw new ServiceException("Not valid ROLE");
		}
		
		String site = parts[0].startsWith("ROLE_") ? parts[0].split("_")[1] : parts[0];
		
		return new CrossTenantPermissions(site, parts[1], parts[2]);
	}

}
