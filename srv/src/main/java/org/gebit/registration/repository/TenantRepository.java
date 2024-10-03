package org.gebit.registration.repository;

import org.springframework.stereotype.Component;

import com.sap.cds.ql.Insert;
import com.sap.cds.services.persistence.PersistenceService;

import static org.gebit.gen.db.Db_.TENANT;
import static org.gebit.gen.db.Db_.USER_TENANT_MAPPING;

import org.gebit.gen.db.Tenant;
import org.gebit.gen.db.User;
import org.gebit.gen.db.UserTenantMapping;
@Component
public class TenantRepository {

	private PersistenceService ps;

	public TenantRepository(PersistenceService ps) {
		this.ps = ps;
	}
	public Tenant onboardTenantForUser(User user, String tenantId) {
		Tenant tenant = Tenant.create(tenantId);
		tenant.setCreatedBy(user);
		
		Insert insertIntoTenantTable = Insert.into(TENANT).entry(tenant);
		tenant = ps.run(insertIntoTenantTable).single(Tenant.class);
		
		UserTenantMapping mapping = UserTenantMapping.create();
		mapping.setTenant(tenant);
		mapping.setUser(user);
		mapping.setMappingType("admin");
		
		Insert insertTenantMapping = Insert.into(USER_TENANT_MAPPING).entry(mapping);
		ps.run(insertTenantMapping);
		
		return tenant;
	}
}
