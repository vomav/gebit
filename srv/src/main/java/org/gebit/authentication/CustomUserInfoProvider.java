package org.gebit.authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.sap.cds.services.request.ModifiableUserInfo;
import com.sap.cds.services.request.UserInfo;
import com.sap.cds.services.runtime.UserInfoProvider;

@Component
public class CustomUserInfoProvider implements UserInfoProvider {

	private static final String GLOBAL_ADMIN = "global_admin";
	public static final String USER_ID = "UserID";
	public static final String LOGON_SURNAME = "LogonSurname";
	public static final String LOGON_USERNAME = "LogonUsername";
	public static final String PERM = "Permissions";
	
	private UserInfoProvider defaultProvider;
	private List<String> userAdmins;
	
	public CustomUserInfoProvider(@Value("${org.gebit.admins}") List<String> userAdmins) {
		this.userAdmins = userAdmins;
		
	}
	@Override
	public UserInfo get() {
		JwtAuthentication jwtInfoToken;
		
		if(SecurityContextHolder.getContext().getAuthentication() instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
			return defaultProvider.get();
		}
		
		try {
			jwtInfoToken  = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
			if(jwtInfoToken == null) {
				return defaultProvider.get();
			}
		}catch (Exception e) {
			System.out.println(e.getMessage());
			return defaultProvider.get();
		}
		
		String tenantId = jwtInfoToken.getTenantId();
		String userId = jwtInfoToken.getUserId();
		
		ModifiableUserInfo userInfo = UserInfo.create();
		userInfo.setTenant(tenantId);
		userInfo.setName(jwtInfoToken.getPrincipal().toString());
		userInfo.setIsAuthenticated(true);
		userInfo.setAdditionalAttribute(LOGON_USERNAME, jwtInfoToken.getUsername());
		userInfo.setAdditionalAttribute(LOGON_SURNAME, jwtInfoToken.getSurname());
		userInfo.setAdditionalAttribute(USER_ID, userId);
		
		
		
		List<CrossTenantPermissions> perms = new ArrayList<>();
		List<String> allowedUsers = new ArrayList<>();
		List<String> allowedAdmins = new ArrayList<>();
		jwtInfoToken.getAuthorities().forEach(authority -> {
			CrossTenantPermissions perm = CrossTenantPermissions.create(authority.toString());
			userInfo.setAdditionalAttribute(perm.getTenantId(), perm);
			perms.add(perm);
			allowedUsers.add(perm.getTenantId());
			
			if(perm.getMappingType().equals("admin")) 
				allowedAdmins.add(perm.getTenantId());
		});
		
		userInfo.setAttributeValues("userIn", allowedUsers);
		userInfo.setAttributeValues("adminIn", allowedAdmins);
		
		userInfo.setAdditionalAttribute(PERM, perms);
		
		Optional<String> maybeAdmin = this.userAdmins.stream().filter(admin -> admin.equals(userId)).findFirst();
		if(maybeAdmin.isPresent())
			userInfo.addRole(GLOBAL_ADMIN);
		
		
		return userInfo;
	}
	
    @Override
    public void setPrevious(UserInfoProvider prev) {
        this.defaultProvider = prev;
    }

}
