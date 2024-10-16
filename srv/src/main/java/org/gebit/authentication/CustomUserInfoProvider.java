package org.gebit.authentication;

import java.util.Set;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.sap.cds.services.ErrorStatus;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.request.ModifiableUserInfo;
import com.sap.cds.services.request.UserInfo;
import com.sap.cds.services.runtime.UserInfoProvider;

@Component
public class CustomUserInfoProvider implements UserInfoProvider {

	public static final String USER_ID = "UserID";
	public static final String LOGON_SURNAME = "LogonSurname";
	public static final String LOGON_USERNAME = "LogonUsername";
	
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
		jwtInfoToken.getAuthorities().forEach(authority -> {
			String[] keyVal = authority.toString().split(":"); 
			userInfo.setAdditionalAttribute(keyVal[0], keyVal[1]);
			if(jwtInfoToken.getTenantId().equals(keyVal[0])) {
				userInfo.setRoles(Set.of(keyVal[1]));
			}
		});
		
		return userInfo;
	}
	
    @Override
    public void setPrevious(UserInfoProvider prev) {
        this.defaultProvider = prev;
    }

}
