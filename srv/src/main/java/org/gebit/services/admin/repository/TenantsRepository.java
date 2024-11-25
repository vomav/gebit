package org.gebit.services.admin.repository;

import org.gebit.gen.srv.admin.Tenants;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.persistence.PersistenceService;

@Component
public class TenantsRepository {

	private PersistenceService ps;
	
	public TenantsRepository(PersistenceService ps) {
		this.ps = ps;
	}
	
	
	public Tenants selectSingleTenantByCqnSelect(CqnSelect select) {
		return ps.run(select).single(Tenants.class);
	}
}
