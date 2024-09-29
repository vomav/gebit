package org.gebit.registration.repository;

import org.gebit.gen.db.User;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.services.persistence.PersistenceService;

import static org.gebit.gen.db.Db_.USER;

@Component
public class RegistrationUserRepository {
	private PersistenceService ps;

	public RegistrationUserRepository(PersistenceService ps) {
		this.ps = ps;
	}

	public boolean isExists(String email) {
		Select<?> select = Select.from(USER).where(p -> p.email().eq(email));
		return ps.run(select).rowCount() > 0;
	}

	public User registerUser(User user) {
		Insert insertUser = Insert.into(USER).entry(user);
		return ps.run(insertUser).single(User.class);
	}
	
}
