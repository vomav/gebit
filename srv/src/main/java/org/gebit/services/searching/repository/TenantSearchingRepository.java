package org.gebit.services.searching.repository;

import org.gebit.gen.db.Tenants;
import org.gebit.gen.db.Tenants_;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Upsert;
import com.sap.cds.services.persistence.PersistenceService;
@Component
public class TenantSearchingRepository {

	private PersistenceService ps;

	public TenantSearchingRepository(PersistenceService ps) {
		this.ps = ps;
	}
	
	
	public Tenants upsert(Tenants tenant) {
		Upsert upsert = Upsert.into(Tenants_.class).entry(tenant);
		return ps.run(upsert).single(Tenants.class);
	}
}
