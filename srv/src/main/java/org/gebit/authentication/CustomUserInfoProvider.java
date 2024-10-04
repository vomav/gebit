package org.gebit.authentication;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.sap.cds.services.request.ModifiableUserInfo;
import com.sap.cds.services.request.UserInfo;
import com.sap.cds.services.runtime.UserInfoProvider;

@Component
public class CustomUserInfoProvider implements UserInfoProvider {

	private UserInfoProvider defaultProvider;
	
	@Override
	public UserInfo get() {
		ModifiableUserInfo userInfo = UserInfo.create();
		userInfo.setTenant("4d13ecb3-d7e7-483b-b982-d9ec6f701ca3");
		userInfo.setIsAuthenticated(true);
		userInfo.setRoles(Set.of("admin"));
		return userInfo;
	}
	
    @Override
    public void setPrevious(UserInfoProvider prev) {
        this.defaultProvider = prev;
    }

}
