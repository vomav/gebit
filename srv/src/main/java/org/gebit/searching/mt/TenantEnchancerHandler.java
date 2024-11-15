package org.gebit.searching.mt;



import java.util.Optional;

import org.gebit.gen.srv.searching.Searching;
import org.gebit.gen.srv.searching.Searching_;
import org.springframework.stereotype.Component;

import com.sap.cds.CdsData;
import com.sap.cds.ql.CQL;
import com.sap.cds.reflect.CdsElement;
import com.sap.cds.reflect.CdsEntity;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.reflect.CdsStringType;
import com.sap.cds.reflect.CdsStructuredType;
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
@ServiceName(value = PersistenceService.DEFAULT_NAME, type = {PersistenceService.class, Searching.class})
//@ServiceName(value = "*", type = Searching.class)
public class TenantEnchancerHandler implements EventHandler {
	
	private UserInfo userInfo;
	
	public static final String TENANT_DESCRIMITATOR_COLUMN = "tenantDiscriminator";
	
	public TenantEnchancerHandler(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	@Before(event = PersistenceService.EVENT_READ, serviceType = {Searching.class, PersistenceService.class})
	public void onBeforeRead(CdsReadEventContext c) {
		if(isApplicable(c.getModel(), c.getTarget().getQualifiedName())) { 
			TenantModifiedWhereType m = createTenantSpecifictWhere(c.getTarget());
			c.setCqn(CQL.copy(c.getCqn(), m));
		}
	}

	private TenantModifiedWhereType createTenantSpecifictWhere(CdsEntity cdsEntity) {
		return new TenantModifiedWhereType(userInfo.getTenant(), cdsEntity);
	}
	
	
	@Before(event = PersistenceService.EVENT_DELETE, serviceType = {Searching.class, PersistenceService.class})
	public void onBeforeDelete(CdsDeleteEventContext c) {
		if(isApplicable(c.getModel(), c.getTarget().getQualifiedName())) { 
			TenantModifiedWhereType m = createTenantSpecifictWhere(c.getTarget());
			 c.setCqn(CQL.copy(c.getCqn(), m));
		}
	}
	
	@Before(event = PersistenceService.EVENT_UPDATE, serviceType = {Searching.class, PersistenceService.class})
	public void onBeforeUpdate(CdsUpdateEventContext c) {
		if(isApplicable(c.getModel(), c.getTarget().getQualifiedName())) { 
			TenantModifiedWhereType m = createTenantSpecifictWhere(c.getTarget());
			c.setCqn(CQL.copy(c.getCqn(), m));
		}
	}
	
	@Before(event = PersistenceService.EVENT_UPSERT,serviceType = {Searching.class, PersistenceService.class})
	public void onBeforeUpsert(CdsUpsertEventContext c) {
		if(isApplicable(c.getModel(), c.getTarget().getQualifiedName())) { 
			TenantModifiedWhereType m = createTenantSpecifictWhere(c.getTarget());
			 c.setCqn( CQL.copy(c.getCqn(), m));
		}
	}
	
	@Before(event = PersistenceService.EVENT_CREATE, serviceType = {Searching.class, PersistenceService.class})
	public void onBeforeInsert(CdsCreateEventContext c, CdsData data) {
		if(isApplicable(c.getModel(), c.getTarget().getQualifiedName())) {
			data.put(TENANT_DESCRIMITATOR_COLUMN, this.userInfo.getTenant());
		}
		
	}
	
	private boolean isApplicable(CdsModel model, String name) {
		CdsStructuredType type = model.getStructuredType(name);
		Optional<?> tenantDiscriminatorElemOpt = type.findElement(TENANT_DESCRIMITATOR_COLUMN);
		return tenantDiscriminatorElemOpt.isPresent();
	}
}
