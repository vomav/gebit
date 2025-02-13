package org.gebit.services.registration.handler;

import java.util.UUID;

import org.gebit.gen.db.Tenants;
import org.gebit.gen.db.Users;
import org.gebit.gen.srv.registration.RegisterContext;
import org.gebit.gen.srv.registration.Registration_;
import org.gebit.gen.srv.registration.RegistredUser;
import org.gebit.i18n.MessageKeys;
import org.gebit.services.registration.repository.RegistrationUserRepository;
import org.gebit.services.registration.repository.TenantRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
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
	private final PasswordEncoder encoder;
	

	public RegisterNewUser(RegistrationUserRepository userRepository, TenantRepository tenantRepository, PasswordEncoder encoder) {
		super();
		this.userRepository = userRepository;
		this.tenantRepository = tenantRepository;
        this.encoder = encoder;
    }


	

	@On(event=RegisterContext.CDS_NAME)
	public void onRegister(RegisterContext context) {
		if(userRepository.isExists(context.getEmail())) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST,MessageKeys.USER_ALREADY_EXISTS, context.getEmail());
		}
		
		String currentTenant = UUID.randomUUID().toString();
		
		Users user = Users.create();
		user.setName(context.getName());
		user.setEmail(context.getEmail());
		user.setSurname(context.getSurname());
		user.setCurrentTenantId(currentTenant);
		user.setPassword(encoder.encode(context.getPassword()));
		
		user = this.userRepository.registerUser(user);
		Tenants tenant = this.tenantRepository.onboardTenantForUser(user, currentTenant);
		
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
