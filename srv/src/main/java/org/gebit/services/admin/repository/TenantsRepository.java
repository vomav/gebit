package org.gebit.services.admin.repository;

import org.gebit.gen.db.Tenants;
import org.gebit.gen.db.Tenants_;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Upsert;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.persistence.PersistenceService;

@Component
public class TenantsRepository {

	private PersistenceService ps;
	
	public TenantsRepository(PersistenceService ps) {
		this.ps = ps;
	}
	
	
	public org.gebit.gen.srv.admin.Tenants selectSingleTenantByCqnSelect(CqnSelect select) {
		return ps.run(select).single(org.gebit.gen.srv.admin.Tenants.class);
	}
	
	public Tenants upsert(Tenants tenant) {
		Upsert upsert = Upsert.into(Tenants_.class).entry(tenant);
		return ps.run(upsert).single(Tenants.class);
	}


	public void delete(String id) {
		Delete<Tenants_> deleteById = Delete.from(Tenants_.class).byId(id);
		ps.run(deleteById);
		
	}
}
