package org.gebit.admin.repository;

import java.util.Optional;

import org.gebit.gen.srv.admin.Admin;
import org.gebit.gen.srv.searching.Searching;
import org.gebit.searching.mt.TenantModifiedWhereType;
import org.springframework.stereotype.Component;

import com.sap.cds.CdsData;
import com.sap.cds.ql.CQL;
import com.sap.cds.reflect.CdsEntity;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.reflect.CdsStructuredType;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsDeleteEventContext;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.cds.CdsUpsertEventContext;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.services.request.UserInfo;

@Component
@ServiceName(value = PersistenceService.DEFAULT_NAME, type = {PersistenceService.class, Admin.class})
public class TenantIsolation {

	private UserInfo userInfo;

	public TenantIsolation(UserInfo userInfo) {
		super();
		this.userInfo = userInfo;
	}
	
	
	@Before(event = PersistenceService.EVENT_READ, serviceType = {Admin.class, PersistenceService.class})
	public void onBeforeRead(CdsReadEventContext c) {
		System.out.println();
	}

	
	@Before(event = PersistenceService.EVENT_DELETE, serviceType = {Admin.class, PersistenceService.class})
	public void onBeforeDelete(CdsDeleteEventContext c) {
		System.out.println();
	}
	
	@Before(event = PersistenceService.EVENT_UPDATE, serviceType = {Admin.class, PersistenceService.class})
	public void onBeforeUpdate(CdsUpdateEventContext c) {
		System.out.println();
	}
	
	@Before(event = PersistenceService.EVENT_UPSERT,serviceType = {Admin.class, PersistenceService.class})
	public void onBeforeUpsert(CdsUpsertEventContext c) {
		System.out.println();
	}
	
	@Before(event = PersistenceService.EVENT_CREATE, serviceType = {Admin.class, PersistenceService.class})
	public void onBeforeInsert(CdsCreateEventContext c, CdsData data) {
		System.out.println();
	}
	
	
	private TenantModifiedWhereType createTenantSpecifictWhere(CdsEntity cdsEntity) {
		return new TenantModifiedWhereType(userInfo.getTenant(), cdsEntity);
	}
	
	
}
