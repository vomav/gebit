package org.gebit.registration.handler;

import static org.gebit.authentication.CustomUserInfoProvider.LOGON_SURNAME;
import static org.gebit.authentication.CustomUserInfoProvider.LOGON_USERNAME;
import static org.gebit.authentication.CustomUserInfoProvider.USER_ID;

import java.util.List;

import org.gebit.authentication.repository.UserRepository;
import org.gebit.gen.db.Users;
import org.gebit.gen.srv.ui_service.LogedInUser;
import org.gebit.gen.srv.ui_service.LogedInUser_;
import org.gebit.gen.srv.ui_service.UiService_;
import org.springframework.stereotype.Component;

import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.request.UserInfo;

@Component
@ServiceName(UiService_.CDS_NAME)
public class UIServiceHandler implements EventHandler {

	private UserInfo userInfo;
    private final UserRepository userRepository;
	
	public UIServiceHandler(UserInfo userInfo, UserRepository userRepository) {
		this.userInfo = userInfo;
		this.userRepository = userRepository;
	}


	@On(event=CqnService.EVENT_READ, entity = LogedInUser_.CDS_NAME)
	public void onRead(CdsReadEventContext context) {
		
		LogedInUser user = LogedInUser.create();
		
		Users savedUser = userRepository.byId(userInfo.getAdditionalAttribute(USER_ID).toString());
		
		user.setId(userInfo.getAdditionalAttribute(USER_ID).toString());
		user.setEmail(userInfo.getName());
		user.setUsername(userInfo.getAdditionalAttribute(LOGON_USERNAME).toString());
		user.setSurname(userInfo.getAdditionalAttribute(LOGON_SURNAME).toString());
		user.setTenant(userInfo.getTenant());
		user.setRole(userInfo.getAdditionalAttribute("ROLE_" + savedUser.getCurrentTenantId()).toString());
		user.setIsAdmin(userInfo.getAdditionalAttribute("ROLE_" + savedUser.getCurrentTenantId()).toString().equals("admin"));
		context.setResult(List.of(user));
		context.setCompleted();
		
		
		
	}
}
