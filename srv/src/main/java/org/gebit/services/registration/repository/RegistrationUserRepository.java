package org.gebit.services.registration.repository;

import static org.gebit.gen.db.Db_.USERS;

import org.gebit.gen.db.Users;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.services.persistence.PersistenceService;

@Component
public class RegistrationUserRepository {
	private PersistenceService ps;

	public RegistrationUserRepository(PersistenceService ps) {
		this.ps = ps;
	}

	public boolean isExists(String email) {
		Select<?> select = Select.from(USERS).where(p -> p.email().eq(email));
		return ps.run(select).rowCount() > 0;
	}

	public Users registerUser(Users user) {
		Insert insertUser = Insert.into(USERS).entry(user);
		return ps.run(insertUser).single(Users.class);
	}
	
}
