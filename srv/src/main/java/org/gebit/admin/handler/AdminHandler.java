package org.gebit.admin.handler;

import java.util.List;
import java.util.Optional;

import org.gebit.authentication.CustomUserInfoProvider;
import org.gebit.gen.db.UserTenantMappings;
import org.gebit.gen.db.UserTenantMappings_;
import org.gebit.gen.srv.admin.Admin_;
import org.gebit.gen.srv.admin.Tenants;
import org.gebit.gen.srv.admin.Tenants_;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Expand;
import com.sap.cds.ql.Predicate;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnExpand;
import com.sap.cds.ql.cqn.CqnPredicate;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnSelectListItem;
import com.sap.cds.ql.cqn.Modifier;
import com.sap.cds.reflect.CdsEntity;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.request.UserInfo;

@Component
@ServiceName(Admin_.CDS_NAME)
public class AdminHandler implements EventHandler {
	
	private UserInfo userInfo;
	
	public AdminHandler(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
	@Before(entity = Tenants_.CDS_NAME, event = CqnService.EVENT_READ)
	public void onBeforeTenantsRead(CdsReadEventContext c) {
		String userId = userInfo.getAdditionalAttribute(CustomUserInfoProvider.USER_ID).toString();
		Modifier m = new WhereModifier(userId);
		c.setCqn(CQL.copy(c.getCqn(), m));
	}
	
//	@After(entity = Tenants_.CDS_NAME, event = CqnService.EVENT_READ)
//	public void onAfterTenantsRead(CdsReadEventContext c) {
//		String userId = userInfo.getAdditionalAttribute(CustomUserInfoProvider.USER_ID).toString();
//
//		List<Tenants> tenants = c.getResult().listOf(Tenants.class);
//		tenants.forEach(tenant -> {
//		   Optional<org.gebit.gen.srv.admin.UserTenantMappings> user =	tenant.getToUsers().stream().filter(t -> t.getUserId().equals(userId)).findFirst();
//		   if(user.isPresent()) {
//			   tenant.setMyRole(user.get().getMappingType());
//		   }
//		});
//	}
	
	
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
		Predicate exists = CQL.exists(Select.from(UserTenantMappings_.class).columns(CQL.star()).where( predicate -> CQL.and( predicate.user_ID().eq(userId), CQL.get("$outer.ID").eq(predicate.tenant_ID()))  ));
		return where == null ? exists : CQL.and(where, exists);
	}
	
}
