package org.gebit.services.registration.repository;

import org.springframework.stereotype.Component;

import com.sap.cds.ql.Insert;
import com.sap.cds.services.persistence.PersistenceService;

import static org.gebit.gen.db.Db_.TENANTS;
import static org.gebit.gen.db.Db_.USER_TENANT_MAPPINGS;

import java.time.Instant;

import org.gebit.gen.db.Tenants;
import org.gebit.gen.db.Users;
import org.gebit.gen.db.UserTenantMappings;
@Component
public class TenantRepository {

	private PersistenceService ps;

	public TenantRepository(PersistenceService ps) {
		this.ps = ps;
	}
	public Tenants onboardTenantForUser(Users user, String tenantId) {
		Tenants tenant = Tenants.create(tenantId);
		tenant.setCreatedBy(user);
		tenant.setCreatedAt(Instant.now());
		tenant.setCreatedBy(user);
		tenant.setName(user.getName());
		
		Insert insertIntoTenantTable = Insert.into(TENANTS).entry(tenant);
		tenant = ps.run(insertIntoTenantTable).single(Tenants.class);
		
		UserTenantMappings mapping = UserTenantMappings.create();
		mapping.setTenant(tenant);
		mapping.setUser(user);
		mapping.setMappingType("admin");
		
		Insert insertTenantMapping = Insert.into(USER_TENANT_MAPPINGS).entry(mapping);
		ps.run(insertTenantMapping);
		
		return tenant;
	}
}
