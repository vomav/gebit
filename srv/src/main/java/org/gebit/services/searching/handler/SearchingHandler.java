package org.gebit.services.searching.handler;

import static org.gebit.authentication.CustomUserInfoProvider.USER_ID;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import org.gebit.authentication.CustomUserInfoProvider;
import org.gebit.common.user.repository.UserRepository;
import org.gebit.gen.db.PartAssignments;
import org.gebit.gen.db.Tenant;
import org.gebit.gen.db.Tenants;
import org.gebit.gen.db.Territories;
import org.gebit.gen.db.TerritoryAssignments;
import org.gebit.gen.db.UserTenantMappings;
import org.gebit.gen.db.Users;
import org.gebit.gen.srv.searching.PartAssignmentsAssignPartToMeContext;
import org.gebit.gen.srv.searching.PartAssignmentsAssignPartToUserContext;
import org.gebit.gen.srv.searching.PartAssignmentsCancelPartAssignmentContext;
import org.gebit.gen.srv.searching.Searching_;
import org.gebit.gen.srv.searching.TerritoriesAssignToUserContext;
import org.gebit.gen.srv.searching.TerritoriesWithdrawFromUserContext;
import org.gebit.services.searching.repository.PartAssignmentsRepository;
import org.gebit.services.searching.repository.TenantSearchingRepository;
import org.gebit.services.searching.repository.TerritoryAssignmentRepository;
import org.gebit.services.searching.repository.TerritoryRepository;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Predicate;
import com.sap.cds.ql.cqn.CqnPredicate;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.Modifier;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.request.UserInfo;
@Component
@ServiceName(Searching_.CDS_NAME)
public class SearchingHandler implements EventHandler {

	private TerritoryRepository territoryRepository;
	private TerritoryAssignmentRepository territoryAssignmentRepository;
	private UserInfo userInfo;
	private PartAssignmentsRepository partsAssignmentsRepository;
	
	public SearchingHandler(TerritoryRepository territoryRepository, TerritoryAssignmentRepository territoryAssignmentRepository, UserInfo userInfo, PartAssignmentsRepository partsAssignmentsRepository) {
		super();
		this.territoryRepository = territoryRepository;
		this.territoryAssignmentRepository=territoryAssignmentRepository;
		this.userInfo = userInfo;
		this.partsAssignmentsRepository = partsAssignmentsRepository;
    }

	@On(event=TerritoriesAssignToUserContext.CDS_NAME)
	public void onRegister(TerritoriesAssignToUserContext context) {
		Territories territory = this.territoryRepository.runCqn(context.getCqn());
		
		TerritoryAssignments assignment = TerritoryAssignments.create();
		assignment.setAssignedToId(context.getUserId());
		assignment.setToTerritory(territory);
		assignment.setIsDone(false);
		assignment.setStartedDate(Instant.now());
		assignment.setType("Personal");
		assignment.setToPartAssignments(new ArrayList<>());
		
		territory.getToParts().forEach(part -> {
			PartAssignments partAssignment = PartAssignments.create();
			partAssignment.setPartId(part.getId());
			partAssignment.setTenantDiscriminator(userInfo.getTenant());
			assignment.getToPartAssignments().add(partAssignment);
		});
		
		territoryAssignmentRepository.save(assignment);
		context.setResult(true);
		context.setCompleted();
	}
	
	@Before(event = CqnService.EVENT_READ, entity = org.gebit.gen.srv.searching.TerritoryAssignments_.CDS_NAME)
	public void onBeforeReadMyTerritories(CdsReadEventContext c) {
		CqnSelect select = c.getCqn();
		Modifier m = new Modifier() {
			@Override
			public CqnPredicate where(Predicate where) {
				return CQL.and(where, CQL.get("assignedTo_ID").eq(userInfo.getAdditionalAttribute(USER_ID)));
			}
		};
		
		c.setCqn(CQL.copy(select,m));
		
	}
	
	@On(event=TerritoriesWithdrawFromUserContext.CDS_NAME)
	public void onwithdrawFromUser(TerritoriesWithdrawFromUserContext c) {
		Territories assignment = territoryRepository.runCqn(c.getCqn());
		territoryAssignmentRepository.deleteByTerritoryId(assignment.getId());
		c.setResult(true);
		c.setCompleted();
	}
	
	@On(event=PartAssignmentsAssignPartToUserContext.CDS_NAME)
	public void assignPartToUser(PartAssignmentsAssignPartToUserContext c) {
		String userId = c.getUserId();
		CqnSelect select = c.getCqn();
		PartAssignments pa =  this.partsAssignmentsRepository.runCqnSingleSelect(select, c.getModel());
		pa.setInWorkById(userId);
		
		this.partsAssignmentsRepository.save(pa);
		
		c.setResult(true);
		c.setCompleted();
	}
	
	@On(event=PartAssignmentsAssignPartToMeContext.CDS_NAME)
	public void assignPartToMe(PartAssignmentsAssignPartToMeContext c) {
		String userId = c.getUserInfo().getAdditionalAttribute(USER_ID).toString();
		
		CqnSelect select = c.getCqn();
		PartAssignments pa =  this.partsAssignmentsRepository.runCqnSingleSelect(select, c.getModel());
		pa.setInWorkById(userId);
		
		this.partsAssignmentsRepository.save(pa);
		c.setResult(true);
		c.setCompleted();
	}
	
	@On(event=PartAssignmentsCancelPartAssignmentContext.CDS_NAME)
	public void cancelPartAssignment(PartAssignmentsCancelPartAssignmentContext c) {
		
		c.setResult(true);
		c.setCompleted();
	}
	

}
