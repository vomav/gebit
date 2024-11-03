package org.gebit.searching.handler;

import static org.gebit.authentication.CustomUserInfoProvider.USER_ID;

import java.time.Instant;
import java.util.ArrayList;

import org.gebit.gen.db.PartAssignments;
import org.gebit.gen.db.Territories;
import org.gebit.gen.db.TerritoryAssignments;
import org.gebit.gen.srv.admin.Users;
import org.gebit.gen.srv.searching.Searching_;
import org.gebit.gen.srv.searching.TerritoriesAssignToUserContext;
import org.gebit.gen.srv.searching.TerritoriesWithdrawFromUserContext;
import org.gebit.searching.repository.TerritoryAssignmentRepository;
import org.gebit.searching.repository.TerritoryRepository;
import org.gebit.searching.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Predicate;
import com.sap.cds.ql.cqn.CqnPredicate;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.Modifier;
import com.sap.cds.services.ServiceException;
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
	private static final String USER_ISNOT_FOUND_MESSAGE = "user.isnot.found";
	
	
	private UserRepository userRepository;
	private TerritoryRepository territoryRepository;
	private TerritoryAssignmentRepository territoryAssignmentRepository;
	private UserInfo userInfo;
	

	
	public SearchingHandler(@Qualifier("searching_user_repository") UserRepository userRepository, TerritoryRepository territoryRepository, TerritoryAssignmentRepository territoryAssignmentRepository, UserInfo userInfo) {
		super();
		this.userRepository = userRepository;
		this.territoryRepository = territoryRepository;
		this.territoryAssignmentRepository=territoryAssignmentRepository;
		this.userInfo = userInfo;
    }


	

	@On(event=TerritoriesAssignToUserContext.CDS_NAME)
	public void onRegister(TerritoriesAssignToUserContext context) {
		String userId = context.getUserInfo().getAdditionalAttribute(org.gebit.authentication.CustomUserInfoProvider.USER_ID).toString();
		this.userRepository.byId(userId).orElseThrow(() -> new ServiceException(USER_ISNOT_FOUND_MESSAGE));
		
		Territories territory = this.territoryRepository.runCqn(context.getCqn());
		
		TerritoryAssignments assignment = TerritoryAssignments.create();
		assignment.setAssignedTo(Users.create(context.getUserId()));
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
	
}