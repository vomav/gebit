package org.gebit.authentication.repository;

import java.util.List;
import java.util.Optional;

import org.gebit.gen.db.UserTenantMappings;
import org.gebit.gen.db.UserTenantMappings_;
import org.gebit.gen.db.Users;
import org.gebit.gen.db.Users_;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.services.persistence.PersistenceService;

@Component("authentication_user_repository")
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
        Select<?> selectUsersTenantMappings = Select.from(UserTenantMappings_.class).columns(CQL.star()).where(predicate-> predicate.user_ID().eq(Users.getId()));
        return persistenceService.run(selectUsersTenantMappings).listOf(UserTenantMappings.class);
    }

   public void updateUser (Users Users){
       Update<?> updatedUsers = Update.entity(Users_.class).data(Users);
       persistenceService.run(updatedUsers);
   }
}
