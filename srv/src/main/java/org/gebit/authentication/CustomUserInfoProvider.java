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
		try {
			jwtInfoToken  = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
			if(jwtInfoToken == null) {
				return defaultProvider.get();
			}
		}catch (Exception e) {
//			throw new ServiceException(ErrorStatuses.UNAUTHORIZED, "" );
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
		jwtInfoToken.getAuthorities().forEach(authority -> {
			CrossTenantPermissions perm = CrossTenantPermissions.create(authority.toString());
			userInfo.setAdditionalAttribute(perm.getTenantId(), perm);
			perms.add(perm);
		});
		
		
		userInfo.setAdditionalAttribute(PERM, perms);
		
		
		
		return userInfo;
	}
	
    @Override
    public void setPrevious(UserInfoProvider prev) {
        this.defaultProvider = prev;
    }

}
