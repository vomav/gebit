package org.gebit.services.searching.repository;

import java.util.Map;
import java.util.Optional;

import org.apache.catalina.User;
import org.gebit.gen.db.PartAssignments;
import org.gebit.gen.db.PartAssignments_;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Upsert;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.persistence.PersistenceService;

@Component
public class PartAssignmentsRepository {

	private PersistenceService ps;

	public PartAssignmentsRepository(PersistenceService ps) {
		this.ps = ps;
	}
	
	public Optional<PartAssignments> byId(String id) {
		Select<?> userById = Select.from(PartAssignments_.class).columns(CQL.star(), CQL.to(PartAssignments.IN_WORK_BY).expand()).byId(id);
		return ps.run(userById).first(PartAssignments.class);
	}
	
	public void save(PartAssignments assignment) {
		Upsert insert = Upsert.into(PartAssignments_.class).entry(assignment);
		ps.run(insert);
	}

	public PartAssignments runCqnSingleSelect(CqnSelect select, CdsModel model) {
		CqnAnalyzer a = CqnAnalyzer.create(model);
		Map<String, Object> keys = a.analyze(select).targetKeyValues();
		
		return this.byId(keys.get(PartAssignments.ID).toString()).get();
	}

}
