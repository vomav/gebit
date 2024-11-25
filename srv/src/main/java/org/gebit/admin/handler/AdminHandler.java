package org.gebit.admin.handler;

import org.gebit.admin.repository.TenantsRepository;
import org.gebit.admin.repository.UserMappingsRepository;
import org.gebit.admin.repository.UsersRepository;
import org.gebit.authentication.CustomUserInfoProvider;
import org.gebit.gen.db.UserTenantMappings;
import org.gebit.gen.db.UserTenantMappings_;
import org.gebit.gen.db.Users;
import org.gebit.gen.srv.admin.Admin_;
import org.gebit.gen.srv.admin.Tenants;
import org.gebit.gen.srv.admin.TenantsAddUserByEmailContext;
import org.gebit.gen.srv.admin.Tenants_;
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
	private UsersRepository userRepository;
	private TenantsRepository tenantRepository;
	private UserMappingsRepository userMappingRepository;
	
	
	public AdminHandler(UserInfo userInfo, TenantsRepository tenantRepository, UsersRepository userRepository, UserMappingsRepository userMappingRepository) {
		this.userInfo = userInfo;
		this.tenantRepository = tenantRepository;
		this.userRepository = userRepository;
		this.userMappingRepository = userMappingRepository;
	}
	
	@Before(entity = Tenants_.CDS_NAME, event = CqnService.EVENT_READ)
	public void onBeforeTenantsRead(CdsReadEventContext c) {
		String userId = userInfo.getAdditionalAttribute(CustomUserInfoProvider.USER_ID).toString();
		Modifier m = new WhereModifier(userId);
		c.setCqn(CQL.copy(c.getCqn(), m));
	}
	
	
	@On(event=TenantsAddUserByEmailContext.CDS_NAME)
	public void addUserToTenant(TenantsAddUserByEmailContext c) {
		Tenants tenant = this.tenantRepository.selectSingleTenantByCqnSelect(c.getCqn());
		Users user = this.userRepository.findUserByEmail(c.getEmail()).orElseThrow(() -> new ServiceException("email.is.not.registered"));
		
		UserTenantMappings mapping = UserTenantMappings.create();
		mapping.setTenantId(tenant.getId());
		mapping.setUserId(user.getId());
		mapping.setMappingType(c.getMappingType());
		
		this.userMappingRepository.upsert(mapping);
		
		c.setCompleted();
		c.setResult(true);
	}
	
	
}

class WhereModifier implements Modifier {
	
	private String userId;

	public WhereModifier(String userId) {
		this.userId = userId;
	}

//	@Override
//	public CqnSelectListItem expand(CqnExpand expand) {
//		Expand<?> toUsers = CQL.to(".toUsers").expand();
//		return toUsers;
//	}
	@Override
	public CqnPredicate where(Predicate where) {
		Predicate exists = CQL.exists(Select.from(UserTenantMappings_.class).columns(CQL.star()).where( predicate -> CQL.and( CQL.and( predicate.user_ID().eq(userId), CQL.get("$outer.ID").eq(predicate.tenant_ID())), predicate.mappingType().eq("admin"))  ));
		return where == null ? exists : CQL.and(where, exists);
	}
	
}
