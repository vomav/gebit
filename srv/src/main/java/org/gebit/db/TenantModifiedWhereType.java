package org.gebit.db;

import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Predicate;
import com.sap.cds.ql.cqn.Modifier;

public class TenantModifiedWhereType implements Modifier {

	private String tenant;
	public TenantModifiedWhereType(String tenant) {
		this.tenant = tenant;
	}
	
	
	@Override
	public Predicate where(Predicate where) {
		return CQL.and(where, CQL.get("tenant").eq(tenant));
	}

}
