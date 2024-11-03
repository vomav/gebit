package org.gebit.searching.repository;

import java.util.Optional;

import org.gebit.gen.db.TerritoryAssignments;
import org.gebit.gen.db.TerritoryAssignments_;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Expand;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.persistence.PersistenceService;

@Component
public class TerritoryAssignmentRepository {

	private PersistenceService ps;

	public TerritoryAssignmentRepository(PersistenceService ps) {
		this.ps = ps;
	}
	
	public Optional<TerritoryAssignments> byId(String id) {
		Expand<?> toParts = CQL.to("toPartAssignments").expand();
		Select<?> userById = Select.from(TerritoryAssignments_.class).columns(CQL.star(),toParts).byId(id);
		return ps.run(userById).first(TerritoryAssignments.class);
	}
	
	public void save(TerritoryAssignments assignment) {
		Insert insert = Insert.into(TerritoryAssignments_.class).entry(assignment);
		ps.run(insert);
	}
	
	public void deleteByTerritoryId(String id) {
		Delete<?> delete = Delete.from(TerritoryAssignments_.class).where(p -> p.toTerritory_ID().eq(id));
		ps.run(delete);
	}
	
	public TerritoryAssignments runCqnSingleSelect(CqnSelect select) {
		return ps.run(select).single(TerritoryAssignments.class);
	}
}