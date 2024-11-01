package org.gebit.searching.repository;

import java.util.Optional;

import org.gebit.gen.db.Territories;
import org.gebit.gen.db.Territories_;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Expand;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnExpand;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnSelectListItem;
import com.sap.cds.ql.cqn.Modifier;
import com.sap.cds.services.persistence.PersistenceService;

@Component
public class TerritoryRepository {

	private PersistenceService ps;

	public TerritoryRepository(PersistenceService ps) {
		this.ps = ps;
	}
	
	public Optional<Territories> byId(String id) {
		Expand<?> toParts = CQL.to("toParts").expand();
		Select<?> userById = Select.from(Territories_.class).columns(CQL.star(), toParts).byId(id);
		return ps.run(userById).first(Territories.class);
	}
	
	public Territories runCqn(CqnSelect cqnSelect) {
		Expand<?> toParts = CQL.to("toParts").expand();
		
		Modifier m = new Modifier() {
				@Override
				public CqnSelectListItem expand(CqnExpand expand) {
					// TODO Auto-generated method stub
					return expand(toParts);
				}
			
		};
		
		Territories territory = ps.run(CQL.copy(cqnSelect, m)).single(Territories.class);
		return this.byId(territory.getId()).get();
	}

	
}
