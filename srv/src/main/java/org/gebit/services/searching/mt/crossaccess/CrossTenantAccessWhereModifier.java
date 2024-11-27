package org.gebit.services.searching.mt.crossaccess;

import static org.gebit.services.searching.mt.isolatate.TenantEnchancerHandler.TENANT_DESCRIMITATOR_COLUMN;

import java.util.List;
import java.util.Optional;

import org.gebit.authentication.CrossTenantPermissions;

import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Predicate;
import com.sap.cds.ql.cqn.Modifier;
import com.sap.cds.reflect.CdsElement;
import com.sap.cds.reflect.CdsEntity;

public class CrossTenantAccessWhereModifier implements Modifier {

	private List<CrossTenantPermissions> tenants;
	private CdsEntity cdsEntity;

	public CrossTenantAccessWhereModifier(List<CrossTenantPermissions> tenants, CdsEntity cdsEntity) {
		this.tenants = tenants;
		this.cdsEntity = cdsEntity;
	}
	
	
	@Override
	public Predicate where(Predicate where) {
		Optional<CdsElement> tenantElement = cdsEntity.elements().filter(e -> e.getName().equals(TENANT_DESCRIMITATOR_COLUMN)).findAny();
		if(tenantElement.isEmpty()) {
			tenantElement = cdsEntity.elements().filter(e -> e.getName().equals("tenant_ID")).findAny();
		}
		
		if(tenantElement.isEmpty()) {
			return where;
		}
		
		
		return buildDynamicPredicate(tenantElement.get(), where);
	}
	
	
	private Predicate buildDynamicPredicate(CdsElement tenantElement, Predicate p) {
		Predicate resultPrediate = p;
		for(CrossTenantPermissions perm: this.tenants) {
			String tenant = perm.getTenantId();
			if(resultPrediate == null) {
				resultPrediate = CQL.get(tenantElement.getName()).eq(tenant); 
			} else { 
				resultPrediate = buildDynamicPredicate(tenantElement, resultPrediate, tenant);
			}
		}
		
		return resultPrediate;
		
	}
	
	private Predicate buildDynamicPredicate(CdsElement tenantElement, Predicate p, String tenantId) {
		return CQL.or(p, CQL.get(tenantElement.getName()).eq(tenantId));
	}
}
