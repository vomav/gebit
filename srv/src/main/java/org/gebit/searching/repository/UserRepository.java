package org.gebit.searching.repository;

import java.util.Optional;

import org.gebit.gen.db.Users;
import org.gebit.gen.db.Users_;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Select;
import com.sap.cds.services.persistence.PersistenceService;

@Component("searching_user_repository")
public class UserRepository {

	private PersistenceService ps;

	public UserRepository(PersistenceService ps) {
		this.ps = ps;
	}
	
	public Optional<Users> byId(String id) {
		Select<?> userById = Select.from(Users_.class).columns(CQL.star()).byId(id);
		return ps.run(userById).first(Users.class);
	}
	
}
