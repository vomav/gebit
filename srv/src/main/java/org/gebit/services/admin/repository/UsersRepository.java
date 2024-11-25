package org.gebit.services.admin.repository;

import java.util.Optional;

import org.gebit.gen.db.Users;
import org.gebit.gen.db.Users_;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Select;
import com.sap.cds.services.persistence.PersistenceService;

@Component
public class UsersRepository {

	private PersistenceService ps;
	
	public UsersRepository(PersistenceService ps) {
		this.ps = ps;
	}
	
	public Optional<Users> findUserByEmail(String email) {
		Select<?> selectUserByEmail = Select.from(Users_.class).where(c -> c.email().eq(email));
		return ps.run(selectUserByEmail).first(Users.class);
		
	}
}
