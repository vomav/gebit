package org.gebit.authentication;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.sap.cds.services.request.ModifiableUserInfo;
import com.sap.cds.services.request.UserInfo;
import com.sap.cds.services.runtime.UserInfoProvider;

@Component
public class CustomUserInfoProvider implements UserInfoProvider {

	public static final String USER_ID = "UserID";
	public static final String LOGON_SURNAME = "LogonSurname";
	public static final String LOGON_USERNAME = "LogonUsername";
	public static final String PERM = "Permissions";
	
	private UserInfoProvider defaultProvider;
	
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
		
		
		ModifiableUserInfo userInfo = UserInfo.create();
		userInfo.setTenant(jwtInfoToken.getTenantId());
		userInfo.setName(jwtInfoToken.getPrincipal().toString());
		userInfo.setIsAuthenticated(true);
		userInfo.setAdditionalAttribute(LOGON_USERNAME, jwtInfoToken.getUsername());
		userInfo.setAdditionalAttribute(LOGON_SURNAME, jwtInfoToken.getSurname());
		userInfo.setAdditionalAttribute(USER_ID, jwtInfoToken.getUserId());
		
		
		
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
		
		
		
		return userInfo;
	}
	
    @Override
    public void setPrevious(UserInfoProvider prev) {
        this.defaultProvider = prev;
    }

}
