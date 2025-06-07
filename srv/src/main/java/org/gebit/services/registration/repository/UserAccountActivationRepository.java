package org.gebit.services.registration.repository;

import java.util.List;
import java.util.Optional;

import org.gebit.gen.db.UserAccountActivations;
import org.gebit.gen.db.UserAccountActivations_;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnPredicate;
import com.sap.cds.services.persistence.PersistenceService;

@Component
public class UserAccountActivationRepository {

	private PersistenceService ps;

	public UserAccountActivationRepository(PersistenceService ps) {
		this.ps = ps;
	}
	
	public UserAccountActivations createActivationFor(UserAccountActivations obj) {
		
		Select<?> select = Select.from(UserAccountActivations_.class).columns(CQL.star()).where(p -> p.toUser_ID().eq(obj.getToUserId()));
		
		List<UserAccountActivations> existingActivations = ps.run(select).listOf(UserAccountActivations.class);
		
		if(existingActivations!=null && existingActivations.size() > 0) {
			List<? extends CqnPredicate> ids = existingActivations.stream().map(a -> CQL.get(UserAccountActivations.ID).eq(a.getId())).toList();
			ps.run(Delete.from(UserAccountActivations_.class).where(CQL.or((Iterable<? extends CqnPredicate>) ids)));
		}
		
		ps.run(Insert.into(UserAccountActivations_.class).entry(obj));
		return obj;
	}
	
	public Optional<UserAccountActivations> get(String userId) {
		Select<?> select = Select.from(UserAccountActivations_.class).columns(CQL.star()).where(p -> p.toUser_ID().eq(userId));
		return ps.run(select).first(UserAccountActivations.class);
	}
	
	public void delete(String id) {
		ps.run(Delete.from(UserAccountActivations_.class).byId(id));
	}
}
