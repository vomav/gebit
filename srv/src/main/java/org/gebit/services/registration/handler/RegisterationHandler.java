package org.gebit.services.registration.handler;

import java.util.Optional;
import java.util.UUID;

import org.gebit.email.IEmailService;
import org.gebit.email.pojo.OTPEmailRequest;
import org.gebit.email.pojo.TemporaryPasswordRequest;
import org.gebit.gen.db.Tenants;
import org.gebit.gen.db.UserAccountActivations;
import org.gebit.gen.db.Users;
import org.gebit.gen.srv.registration.ActivateAccountContext;
import org.gebit.gen.srv.registration.RegisterContext;
import org.gebit.gen.srv.registration.Registration_;
import org.gebit.gen.srv.registration.RegistredUser;
import org.gebit.gen.srv.registration.ResetPasswordContext;
import org.gebit.i18n.MessageKeys;
import org.gebit.services.registration.handler.utils.OTPGenerator;
import org.gebit.services.registration.handler.utils.PasswordGenerator;
import org.gebit.services.registration.repository.RegistrationUserRepository;
import org.gebit.services.registration.repository.TenantRepository;
import org.gebit.services.registration.repository.UserAccountActivationRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

@Component
@ServiceName(Registration_.CDS_NAME)
public class RegisterationHandler implements EventHandler {
	private RegistrationUserRepository userRepository;
	private TenantRepository tenantRepository;
	private final PasswordEncoder encoder;
	private IEmailService emailServce;
	private OTPGenerator otpGenetator;
	private UserAccountActivationRepository userAccountActivationRepository;
	private PasswordGenerator passwordGenerator;

	public RegisterationHandler(RegistrationUserRepository userRepository, TenantRepository tenantRepository,
			PasswordEncoder encoder, IEmailService emailService, OTPGenerator otpGenetator,
			UserAccountActivationRepository userAccountActivationRepository,
			PasswordGenerator passwordGenerator) {
		super();
		this.userRepository = userRepository;
		this.tenantRepository = tenantRepository;
		this.encoder = encoder;
		this.emailServce = emailService;
		this.otpGenetator = otpGenetator;
		this.userAccountActivationRepository = userAccountActivationRepository;
		this.passwordGenerator = passwordGenerator;
	}

	@On(event = RegisterContext.CDS_NAME)
	public void onRegister(RegisterContext context) {
		if (userRepository.isExists(context.getEmail())) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, MessageKeys.USER_ALREADY_EXISTS, context.getEmail());
		}

		String currentTenant = UUID.randomUUID().toString();

		Users user = Users.create();
		user.setName(context.getName());
		user.setEmail(context.getEmail());
		user.setSurname(context.getSurname());
		user.setCurrentTenantId(currentTenant);
		user.setPassword(encoder.encode(context.getPassword()));
		user.setIsActivated(false);

		user = this.userRepository.registerUser(user);
		Tenants tenant = this.tenantRepository.onboardTenantForUser(user, currentTenant);

		RegistredUser response = RegistredUser.create();
		response.setEmail(user.getEmail());
		response.setId(user.getId());
		response.setName(user.getName());
		response.setSurname(user.getSurname());
		response.setTenant(tenant.getId());

		Integer otp = otpGenetator.generateOTP();
		UserAccountActivations activation = UserAccountActivations.create();
		activation.setToUserId(user.getId());
		activation.setActivationCode(otp);
		userAccountActivationRepository.createActivationFor(activation);

		emailServce.sendOneTimePassword(new OTPEmailRequest(context.getEmail(), otp, "en"));

		context.setResult(response);
		context.setCompleted();
	}

	@On(event = ActivateAccountContext.CDS_NAME)
	public void onActivate(ActivateAccountContext context) {
		UserAccountActivations activation = userAccountActivationRepository.get(context.getUserId())
				.orElseThrow(() -> new ServiceException("email.is.not.registered"));
		if (activation.getActivationCode().equals(Integer.parseInt(context.getActivationCode()))) {
			userAccountActivationRepository.delete(activation.getId());
			this.userRepository.activateUserAccount(activation.getToUserId());
			context.setResult(true);
		} else {
			context.setResult(false);
		}
		context.setCompleted();
	}
	
	@On(event=ResetPasswordContext.CDS_NAME)
	public void onResetPassword(ResetPasswordContext context) {
		Optional<Users> maybeUser = this.userRepository.findUserByEmail(context.getEmail());
		Users user = maybeUser.orElseThrow(() -> new ServiceException("email.is.not.registered"));
		user.setPreviousPassword(user.getPassword());
		String password = this.passwordGenerator.generateSecurePassword(8);
		this.emailServce.sendTemparyPassword(new TemporaryPasswordRequest(user.getEmail(), password));
		user.setPassword(encoder.encode(password));
		this.userRepository.save(user);
		
		context.setResult(true);
		context.setCompleted();
	}
	
}
