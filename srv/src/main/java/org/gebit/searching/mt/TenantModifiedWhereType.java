package org.gebit.searching.mt;

import java.util.Optional;

import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Predicate;
import com.sap.cds.ql.cqn.Modifier;
import com.sap.cds.reflect.CdsElement;
import com.sap.cds.reflect.CdsEntity;
import com.sap.cds.reflect.CdsModel;

public class TenantModifiedWhereType implements Modifier {

	private String tenant;
	private CdsEntity cdsEntity;
	
	public TenantModifiedWhereType(String tenant, CdsEntity cdsEntity) {
		this.tenant = tenant;
		this.cdsEntity = cdsEntity;
	}
	
	
	@Override
	public Predicate where(Predicate where) {
		Optional<CdsElement> tenantElement = cdsEntity.elements().filter(e -> e.getName().contains("tenant") && cdsEntity.findAssociation(e.getName()).isEmpty()).findAny();
		return CQL.and(where, CQL.get(tenantElement.get().getName()).eq(tenant));
	}

}
