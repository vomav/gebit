package org.gebit.services.searching.mt.isolatate;

import static org.gebit.services.searching.mt.isolatate.TenantEnchancerHandler.TENANT_DESCRIMITATOR_COLUMN;

import java.util.Optional;

import javax.sql.rowset.serial.SerialException;



import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Predicate;
import com.sap.cds.ql.cqn.Modifier;
import com.sap.cds.reflect.CdsElement;
import com.sap.cds.reflect.CdsEntity;
import com.sap.cds.services.ServiceException;
public class TenantModifiedWhereType implements Modifier {

	private String tenant;
	private CdsEntity cdsEntity;
	
	public TenantModifiedWhereType(String tenant, CdsEntity cdsEntity) {
		this.tenant = tenant;
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
		
		return where == null ? CQL.get(tenantElement.get().getName()).eq(tenant) : CQL.and(where, CQL.get(tenantElement.get().getName()).eq(tenant));
	}

}
