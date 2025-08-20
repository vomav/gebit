package org.gebit.services.global.admin;

import org.gebit.gen.srv.globaladmin.GlobalAdmin_;
import org.gebit.gen.srv.globaladmin.UserAccountActivationsActivateAccountContext;
import org.gebit.gen.srv.globaladmin.UsersDeleteContext;
import org.springframework.stereotype.Component;

import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

@Component
@ServiceName(GlobalAdmin_.CDS_NAME)
public class GlobalAdminHandler {

	
	@On(event = UsersDeleteContext.CDS_NAME)
	public void deleteAccount(UsersDeleteContext context) {
		
	}
	
	@On(event = UserAccountActivationsActivateAccountContext.CDS_NAME)
	public void activateAccount(UserAccountActivationsActivateAccountContext context) {
		
	}
}
