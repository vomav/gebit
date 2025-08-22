package org.gebit.services.global.admin;

import org.gebit.gen.srv.globaladmin.GlobalAdmin_;
import org.gebit.gen.srv.globaladmin.Users;
import org.gebit.gen.srv.globaladmin.UsersDeleteContext;
import org.gebit.services.global.admin.repository.GlobalAdminUserRepository;
import org.springframework.stereotype.Component;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

@Component
@ServiceName(GlobalAdmin_.CDS_NAME)
public class GlobalAdminHandler implements EventHandler{

	private GlobalAdminUserRepository userRepository;
	
	
	public GlobalAdminHandler(GlobalAdminUserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	
	
	@On(event = UsersDeleteContext.CDS_NAME)
	public void onDeleteUser(UsersDeleteContext context) {
		Users user = this.userRepository.runCqn(context.getCqn());
		this.userRepository.deleteUserFootprint(user.getId());
		context.setResult(true);
		context.setCompleted();
	}
}
