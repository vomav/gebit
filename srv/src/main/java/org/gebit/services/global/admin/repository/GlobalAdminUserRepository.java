package org.gebit.services.global.admin.repository;

import org.gebit.gen.db.InWorkBy_;
import org.gebit.gen.db.Tenants_;
import org.gebit.gen.db.TerritoryAssignments_;
import org.gebit.gen.db.UserTenantMappings_;
import org.gebit.gen.db.Users_;
import org.gebit.gen.srv.globaladmin.Users;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.persistence.PersistenceService;

@Component
public class GlobalAdminUserRepository {

	private PersistenceService ps;
	
	public GlobalAdminUserRepository(PersistenceService ps) {
		this.ps = ps;
	}
	
	public void deleteUserFootprint(String id) {
		// Delete parts that user is in work
		Delete<?> deleteFromInWork = Delete.from(InWorkBy_.class).where(p -> p.user_ID().eq(id));
		ps.run(deleteFromInWork);
		
		Delete<?> deleteFromTerritoryAssignments = Delete.from(TerritoryAssignments_.class).where(p -> p.assignedTo_ID().eq(id));
		ps.run(deleteFromTerritoryAssignments);
		
		Delete<?> deleteFromTenantMappings = Delete.from(UserTenantMappings_.class).where(p -> p.user_ID().eq(id));
		ps.run(deleteFromTenantMappings);
		
		Delete<?> deleteDeafultUserTenant = Delete.from(Tenants_.class).where(p -> CQL.and( p.createdBy_ID().eq(id), p.defaultUserTenant().eq(true)));
		ps.run(deleteDeafultUserTenant);
		
		Delete<?> deleteUser = Delete.from(Users_.class).byId(id);
		ps.run(deleteUser);
	}
	
	public Users runCqn(CqnSelect select) {
		return ps.run(select).single(Users.class);
	}
}
