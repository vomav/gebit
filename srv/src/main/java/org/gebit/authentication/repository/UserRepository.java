package org.gebit.authentication.repository;

import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.services.persistence.PersistenceService;
import org.gebit.gen.db.User;
import org.gebit.gen.db.User_;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepository {

    private PersistenceService persistenceService;

    public UserRepository(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public Optional<User> findUserByEmail(String email) {
       Select<?> selectByUserEmail = Select.from(User_.class).columns(CQL.star()).where(predicate->predicate.email().eq(email));
       return persistenceService.run(selectByUserEmail).first(User.class);
    }

   public void updateUser (User user){
       Update<?> updatedUser = Update.entity(User_.class).data(user);
       persistenceService.run(updatedUser);
   }
}
