package org.gebit.services.searching.handler;

import static org.gebit.authentication.CustomUserInfoProvider.USER_ID;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sap.cds.services.handler.annotations.After;
import jakarta.servlet.http.Part;
import org.gebit.common.user.repository.UserRepository;
import org.gebit.gen.db.InWorkBy;
import org.gebit.gen.db.PartAssignments;
import org.gebit.gen.db.Territories;
import org.gebit.gen.db.TerritoryAssignments;
import org.gebit.gen.db.Users;
import org.gebit.gen.srv.admin.TenantsRemoveSiteContext;
import org.gebit.gen.srv.searching.PartAssignmentsAssignPartToMeContext;
import org.gebit.gen.srv.searching.PartAssignmentsAssignPartToUserContext;
import org.gebit.gen.srv.searching.PartAssignmentsCancelPartAssignmentContext;
import org.gebit.gen.srv.searching.Searching_;
import org.gebit.gen.srv.searching.TenantMappings_;
import org.gebit.gen.srv.searching.TerritoriesAssignToUserContext;
import org.gebit.gen.srv.searching.TerritoriesTrasferToAnotherSiteContext;
import org.gebit.gen.srv.searching.TerritoriesWithdrawFromUserContext;
import org.gebit.gen.srv.searching.TerritoryAssignments_;
import org.gebit.services.admin.repository.TenantsRepository;
import org.gebit.services.searching.repository.PartAssignmentsRepository;
import org.gebit.services.searching.repository.TerritoryAssignmentRepository;
import org.gebit.services.searching.repository.TerritoryRepository;

import org.springframework.stereotype.Component;

import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Predicate;
import com.sap.cds.ql.Select;
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
	private UserRepository userRepository;
	private TenantsRepository tenantRepository;

	public SearchingHandler(TerritoryRepository territoryRepository, TerritoryAssignmentRepository territoryAssignmentRepository, UserInfo userInfo, PartAssignmentsRepository partsAssignmentsRepository, UserRepository userRepository, TenantsRepository tenantRepository) {
		super();
		this.territoryRepository = territoryRepository;
		this.territoryAssignmentRepository=territoryAssignmentRepository;
		this.userInfo = userInfo;
		this.partsAssignmentsRepository = partsAssignmentsRepository;
		this.userRepository=userRepository;
		this.tenantRepository=tenantRepository;
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
		assignment.setTenantDiscriminator(territory.getTenantDiscriminator());
		
		territory.getToParts().forEach(part -> {
			PartAssignments partAssignment = PartAssignments.create();
			partAssignment.setPartId(part.getId());
			partAssignment.setTenantDiscriminator(assignment.getTenantDiscriminator());

			assignment.getToPartAssignments().add(partAssignment);
		});
		
		territoryAssignmentRepository.save(assignment);
		context.setResult(true);
		context.setCompleted();
	}
	
	@Before(event = CqnService.EVENT_READ, entity = TerritoryAssignments_.CDS_NAME)
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
	
//	@Before(event = CqnService.EVENT_READ, entity = TenantMappings_.CDS_NAME)
//	public void onBeforeTenantMappingsRead(CdsReadEventContext c) {
//		if(c.getTarget().getQualifiedName().equals(TenantMappings_.CDS_NAME)) {
//			
//		
//		String userId = userInfo.getAdditionalAttribute(USER_ID).toString();
//		
//		Modifier m = new Modifier() {
//			@Override
//			public CqnPredicate where(Predicate where) {
//				Select<?> s = Select.from(TenantMappings_.class).columns(CQL.star()).where(p -> p.user_ID().eq(userId));
//				return where == null ? CQL.exists(s): CQL.and(where,CQL.exists(s));
//			}
//		};
//		
//		
//		c.setCqn(CQL.copy(c.getCqn(), m));
//		
//		}
//	}
	
	
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
		// pa.setInWorkById(userId);
		
		this.partsAssignmentsRepository.save(pa);
		
		c.setResult(true);
		c.setCompleted();
	}
	
	@On(event=PartAssignmentsAssignPartToMeContext.CDS_NAME)
	public void assignPartToMe(PartAssignmentsAssignPartToMeContext c) {
		String userId = c.getUserInfo().getAdditionalAttribute(USER_ID).toString();
		
		CqnSelect select = c.getCqn();
		PartAssignments pa =  this.partsAssignmentsRepository.runCqnSingleSelect(select, c.getModel());
		// pa.setInWorkById(userId);
		
		InWorkBy inWorkBy = InWorkBy.create();
		
		inWorkBy.setId(UUID.randomUUID().toString());
		inWorkBy.setToParentId(pa.getId());
		
		inWorkBy.setUserId(userId);
		if(pa.getInWorkBy() == null) {
			pa.setInWorkBy(List.of(inWorkBy));
		} else {
			pa.getInWorkBy().add(inWorkBy);
		}
		
		
		this.partsAssignmentsRepository.save(pa);
		c.setResult(true);
		c.setCompleted();
	}
	
	@On(event=PartAssignmentsCancelPartAssignmentContext.CDS_NAME)
	public void cancelPartAssignment(PartAssignmentsCancelPartAssignmentContext c) {
		PartAssignments assignment = partsAssignmentsRepository.runCqnSingleSelect(c.getCqn(), c.getModel());
		assignment.setInWorkBy(List.of());
		this.partsAssignmentsRepository.save(assignment);
		c.setResult(true);
		c.setCompleted();
	}
	
	@On(event = TerritoriesTrasferToAnotherSiteContext.CDS_NAME)
	public void transferTerritoryToAnotherSite(TerritoriesTrasferToAnotherSiteContext context) {
		Territories territory = this.territoryRepository.runCqn(context.getCqn());
		context.getSiteId();
		
		territory.setTenantDiscriminator(context.getSiteId());
		
		territory.getToParts().forEach(p -> p.setTenantDiscriminator(context.getSiteId()));
		
		this.territoryRepository.save(territory);
		
		context.setResult(true);
		context.setCompleted();
	}




}
