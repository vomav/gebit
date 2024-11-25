package org.gebit.services.admin.repository;

import org.gebit.gen.db.UserTenantMappings;
import org.gebit.gen.db.UserTenantMappings_;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Upsert;
import com.sap.cds.services.persistence.PersistenceService;

@Component
public class UserMappingsRepository {

	private PersistenceService ps;

	public UserMappingsRepository(PersistenceService ps) {
		this.ps = ps;
	}

	public void upsert(UserTenantMappings mapping) {

		ps.run(Upsert.into(UserTenantMappings_.class).entry(mapping));

	}
}
