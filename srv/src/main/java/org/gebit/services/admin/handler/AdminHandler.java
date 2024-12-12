package org.gebit.services.admin.handler;

import static org.gebit.authentication.CustomUserInfoProvider.USER_ID;

import java.time.Instant;
import java.util.UUID;

import org.gebit.common.user.repository.UserRepository;
import org.gebit.gen.db.Tenants;
import org.gebit.gen.db.UserTenantMappings;
import org.gebit.gen.db.UserTenantMappings_;
import org.gebit.gen.db.Users;
import org.gebit.gen.srv.admin.Admin_;
import org.gebit.gen.srv.admin.CreateSiteContext;
import org.gebit.gen.srv.admin.LoggedInUser_;
import org.gebit.gen.srv.admin.TenantsAddUserByEmailContext;
import org.gebit.gen.srv.admin.Tenants_;
import org.gebit.services.admin.repository.TenantsRepository;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Predicate;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnPredicate;
import com.sap.cds.ql.cqn.Modifier;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.request.UserInfo;

@Component
@ServiceName(Admin_.CDS_NAME)
public class AdminHandler implements EventHandler {
	
	private UserInfo userInfo;
	private UserRepository userRepository;
	private TenantsRepository tenantRepository;
	
	
	public AdminHandler(UserInfo userInfo, TenantsRepository tenantRepository, UserRepository userRepository) {
		this.userInfo = userInfo;
		this.tenantRepository = tenantRepository;
		this.userRepository = userRepository;
	}
	
	@Before(entity = Tenants_.CDS_NAME, event = CqnService.EVENT_READ)
	public void onBeforeTenantsRead(CdsReadEventContext c) {
		String userId = userInfo.getAdditionalAttribute(USER_ID).toString();
		Modifier m = new WhereModifier(userId);
		c.setCqn(CQL.copy(c.getCqn(), m));
	}
	
	@Before(entity = LoggedInUser_.CDS_NAME, event = CqnService.EVENT_READ)
	public void onBeforeLoggedInUserRead(CdsReadEventContext c) {
		String userId = userInfo.getAdditionalAttribute(USER_ID).toString();
		Modifier m = new SingletonUserByUserId(userId);
		c.setCqn(CQL.copy(c.getCqn(), m));
	}
	
	@On(event=TenantsAddUserByEmailContext.CDS_NAME)
	public void addUserToTenant(TenantsAddUserByEmailContext c) {
		org.gebit.gen.srv.admin.Tenants tenant = this.tenantRepository.selectSingleTenantByCqnSelect(c.getCqn());
		Users user = this.userRepository.findUserByEmail(c.getEmail()).orElseThrow(() -> new ServiceException("email.is.not.registered"));
		
		UserTenantMappings mapping = UserTenantMappings.create();
		mapping.setTenantId(tenant.getId());
		mapping.setUserId(user.getId());
		mapping.setMappingType(c.getMappingType());
		
		this.userRepository.upsertUserTenantMapping(mapping);
		
		c.setCompleted();
		c.setResult(true);
	}
	
	@On(event=CreateSiteContext.CDS_NAME)
	public void onCreateTenant(CreateSiteContext c) {
		Users user = this.userRepository.byId(this.userInfo.getAdditionalAttribute(USER_ID).toString());
		String tenantId = UUID.randomUUID().toString();
		
		Tenants newTenant = Tenants.create();
		newTenant.setId(tenantId);
		newTenant.setName(c.getName());
		newTenant.setDescription(c.getDescription());
		newTenant.setCreatedAt(Instant.now());
		newTenant.setCreatedBy(user);
		this.tenantRepository.upsert(newTenant);
		
		UserTenantMappings mapping = UserTenantMappings.create();
		mapping.setTenantId(UUID.randomUUID().toString());
		mapping.setMappingType("admin");
		mapping.setTenantId(tenantId);
		mapping.setUserId(user.getId());
		this.userRepository.upsertUserTenantMapping(mapping);
		
		c.setResult(true);
		c.setCompleted();
	}
	
	
	
}

class WhereModifier implements Modifier {
	
	private String userId;

	public WhereModifier(String userId) {
		this.userId = userId;
	}

	@Override
	public CqnPredicate where(Predicate where) {
		Predicate exists = CQL.exists(Select.from(UserTenantMappings_.class).columns(CQL.star()).where( predicate -> CQL.and( CQL.and( predicate.user_ID().eq(userId), CQL.get("$outer.ID").eq(predicate.tenant_ID())), predicate.mappingType().eq("admin"))  ));
		return where == null ? exists : CQL.and(where, exists);
	}
	
}

class SingletonUserByUserId implements Modifier {
	
	private String userId;

	public SingletonUserByUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public CqnPredicate where(Predicate where) {
		return CQL.get("ID").eq(userId);
	}
}
