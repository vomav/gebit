package org.gebit.common.user.repository;

import static org.gebit.gen.db.Db_.USER_TENANT_MAPPINGS;

import java.util.List;
import java.util.Optional;

import org.gebit.gen.db.Tenant;
import org.gebit.gen.db.UserTenantMappings;
import org.gebit.gen.db.UserTenantMappings_;
import org.gebit.gen.db.Users;
import org.gebit.gen.db.Users_;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.Upsert;
import com.sap.cds.services.persistence.PersistenceService;

@Component
public class UserRepository {

    private PersistenceService persistenceService;

    public UserRepository(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public Users byId(String id) {
    	Select<?> selectByUsersEmail = Select.from(Users_.class).columns(CQL.star(), CQL.to("currentTenant").expand()).byId(id);
    	return persistenceService.run(selectByUsersEmail).single(Users.class);
    }
    public Optional<Users> findUserByEmail(String email) {
       Select<?> selectByUsersEmail = Select.from(Users_.class).columns(CQL.star(), CQL.to("currentTenant").expand()).where(predicate->predicate.email().eq(email));
       return persistenceService.run(selectByUsersEmail).first(Users.class);
    }

    public List<UserTenantMappings> getUserPermission(Users Users) {
        Select<?> userBasedPermissionsSelect = Select.from(UserTenantMappings_.class).columns(CQL.star(), CQL.to("tenant").expand()).where(predicate-> predicate.user_ID().eq(Users.getId()));
        List<UserTenantMappings> userBasedPermissions = persistenceService.run(userBasedPermissionsSelect).listOf(UserTenantMappings.class);
        
        List<UserTenantMappings> nonUserTenants = userBasedPermissions.stream().filter(utm -> !utm.getTenant().getDefaultUserTenant()).toList();
        
//        Select<?> nonUserBasedPermissionsSelect = Select.from(UserTenantMappings_.class).columns(CQL.star(), CQL.to("tenant").expand()).where(predicate-> predicate.tenant_ID().eq(nonUserTenants.get(0).getTenantId()));
        
//        List<UserTenantMappings> nonUserBasedPermissions = persistenceService.run(nonUserBasedPermissionsSelect).listOf(UserTenantMappings.class);
//        userBasedPermissions.addAll(nonUserBasedPermissions);
        
        return userBasedPermissions;
    }

   public void updateUser(Users Users){
       Update<?> updatedUsers = Update.entity(Users_.class).data(Users);
       persistenceService.run(updatedUsers);
   }

   public void upsertUserTenantMapping(UserTenantMappings userMapping) {
		Upsert insertTenantMapping = Upsert.into(USER_TENANT_MAPPINGS).entry(userMapping);
		persistenceService.run(insertTenantMapping);
   }

	public void deleteUserMappingsByTenantId(String id) {	// TODO Auto-generated method stub
		Delete<?> deleteByTenantId = Delete.from(UserTenantMappings_.class).where(p -> p.tenant_ID().eq(id));
		persistenceService.run(deleteByTenantId);
	}
}
