package org.gebit.searching.mt;



import org.gebit.gen.srv.searching.Searching;
import org.springframework.stereotype.Component;

import com.sap.cds.CdsData;
import com.sap.cds.ql.CQL;
import com.sap.cds.reflect.CdsEntity;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsDeleteEventContext;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.cds.CdsUpsertEventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.services.request.UserInfo;

@Component
@ServiceName(value = "*", type = Searching.class)
public class TenantEnchancerHandler implements EventHandler {
	
	private UserInfo userInfo;
	
	public TenantEnchancerHandler(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	@Before(event = PersistenceService.EVENT_READ, serviceType = Searching.class )
	public void onBeforeRead(CdsReadEventContext c) {
		TenantModifiedWhereType m = createTenantSpecifictWhere(c.getTarget());
		 c.setCqn( CQL.copy(c.getCqn(), m));
	}

	private TenantModifiedWhereType createTenantSpecifictWhere(CdsEntity cdsEntity) {
		return new TenantModifiedWhereType(userInfo.getTenant(), cdsEntity);
	}
	
	
	@Before(event = PersistenceService.EVENT_DELETE, serviceType = Searching.class)
	public void onBeforeDelete(CdsDeleteEventContext c) {
		TenantModifiedWhereType m = createTenantSpecifictWhere(c.getTarget());
		 c.setCqn( CQL.copy(c.getCqn(), m));
		
	}
	
	@Before(event = PersistenceService.EVENT_UPDATE, serviceType = Searching.class)
	public void onBeforeUpdate(CdsUpdateEventContext c) {
		TenantModifiedWhereType m = createTenantSpecifictWhere(c.getTarget());
		 c.setCqn( CQL.copy(c.getCqn(), m));
		
	}
	
	@Before(event = PersistenceService.EVENT_UPSERT, serviceType = Searching.class)
	public void onBeforeUpsert(CdsUpsertEventContext c) {
		TenantModifiedWhereType m = createTenantSpecifictWhere(c.getTarget());
		 c.setCqn( CQL.copy(c.getCqn(), m));
		
	}
	
	@Before(event = PersistenceService.EVENT_CREATE, serviceType = Searching.class)
	public void onBeforeInsert(CdsCreateEventContext c, CdsData data) {
		data.put("tenant", this.userInfo.getTenant());
		
	}
}
