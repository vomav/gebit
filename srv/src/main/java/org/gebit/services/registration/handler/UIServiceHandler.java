package org.gebit.services.registration.handler;

import static org.gebit.authentication.CustomUserInfoProvider.LOGON_SURNAME;
import static org.gebit.authentication.CustomUserInfoProvider.LOGON_USERNAME;
import static org.gebit.authentication.CustomUserInfoProvider.USER_ID;

import java.util.List;

import org.gebit.authentication.CrossTenantPermissions;
import org.gebit.authentication.repository.UserRepository;
import org.gebit.gen.db.Users;
import org.gebit.gen.srv.ui_service.LoggedInUser;
import org.gebit.gen.srv.ui_service.LoggedInUser_;
import org.gebit.gen.srv.ui_service.UiService_;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.request.UserInfo;

@Component()
@ServiceName(UiService_.CDS_NAME)
public class UIServiceHandler implements EventHandler {

	private UserInfo userInfo;
    private final UserRepository userRepository;
	
	public UIServiceHandler(UserInfo userInfo, @Qualifier("authentication_user_repository") UserRepository userRepository) {
		this.userInfo = userInfo;
		this.userRepository = userRepository;
	}


	@On(event=CqnService.EVENT_READ, entity = LoggedInUser_.CDS_NAME)
	public void onRead(CdsReadEventContext context) {
		
		LoggedInUser user = LoggedInUser.create();
		
		Users savedUser = userRepository.byId(userInfo.getAdditionalAttribute(USER_ID).toString());
		
		user.setId(userInfo.getAdditionalAttribute(USER_ID).toString());
		user.setEmail(userInfo.getName());
		user.setUsername(userInfo.getAdditionalAttribute(LOGON_USERNAME).toString());
		user.setSurname(userInfo.getAdditionalAttribute(LOGON_SURNAME).toString());
		user.setTenant(userInfo.getTenant());
		user.setLoggedToSite(savedUser.getCurrentTenant().getName());
		CrossTenantPermissions perm = (CrossTenantPermissions) userInfo.getAdditionalAttribute(savedUser.getCurrentTenantId());
		user.setRole(perm.getMappingType());
		user.setIsAdmin(perm.getMappingType().equals("admin"));
		context.setResult(List.of(user));
		context.setCompleted();
		
		
		
	}
}
