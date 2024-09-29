package org.gebit.registration.handler;

import java.util.UUID;

import org.gebit.gen.db.Tenant;
import org.gebit.gen.db.User;
import org.gebit.gen.srv.registration.RegisterContext;
import org.gebit.gen.srv.registration.Registration_;
import org.gebit.gen.srv.registration.RegistredUser;
import org.gebit.i18n.MessageKeys;
import org.gebit.registration.repository.RegistrationUserRepository;
import org.gebit.registration.repository.TenantRepository;
import org.springframework.stereotype.Component;

import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

@Component
@ServiceName(Registration_.CDS_NAME)
public class RegisterNewUser implements EventHandler {
	private RegistrationUserRepository userRepository;
	private TenantRepository tenantRepository;
	
	

	public RegisterNewUser(RegistrationUserRepository userRepository, TenantRepository tenantRepository) {
		super();
		this.userRepository = userRepository;
		this.tenantRepository = tenantRepository;
	}


	

	@On(event=RegisterContext.CDS_NAME)
	public void onRegister(RegisterContext context) {
		if(userRepository.isExists(context.getEmail())) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST,MessageKeys.USER_ALREADY_EXISTS, context.getEmail());
		}
		
		String currentTenant = UUID.randomUUID().toString();
		
		User user = User.create();
		user.setName(context.getName());
		user.setEmail(context.getEmail());
		user.setSurname(context.getSurname());
		user.setCurrentTenant(currentTenant);
		user.setPassword(context.getPassword());
		
		user = this.userRepository.registerUser(user);
		Tenant tenant = this.tenantRepository.onboardTenantForUser(user, currentTenant);
	
		RegistredUser response = RegistredUser.create();
		response.setEmail(user.getEmail());
		response.setId(user.getId());
		response.setName(user.getName());
		response.setSurname(user.getSurname());
		response.setTenant(tenant.getId());
		
		context.setResult(response);
		context.setCompleted();
		
		
		
	}
}
