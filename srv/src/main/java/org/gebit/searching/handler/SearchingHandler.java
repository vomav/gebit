package org.gebit.searching.handler;

import java.util.Optional;

import org.gebit.gen.db.Territories;
import org.gebit.gen.srv.searching.Searching_;
import org.gebit.gen.srv.searching.TerritoriesAssignToUserContext;
import org.gebit.searching.repository.TerritoryRepository;
import org.gebit.searching.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sap.cds.services.ServiceException;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

@Component
@ServiceName(Searching_.CDS_NAME)
public class SearchingHandler implements EventHandler {
	private static final String USER_ISNOT_FOUND_MESSAGE = "user.isnot.found";
	
	
	private UserRepository userRepository;
	private TerritoryRepository territoryRepository;
	

	
	public SearchingHandler(@Qualifier("searching_user_repository") UserRepository userRepository, TerritoryRepository territoryRepository) {
		super();
		this.userRepository = userRepository;
		this.territoryRepository = territoryRepository;
    }


	

	@On(event=TerritoriesAssignToUserContext.CDS_NAME)
	public void onRegister(TerritoriesAssignToUserContext context) {
		String userId = context.getUserInfo().getAdditionalAttribute(org.gebit.authentication.CustomUserInfoProvider.USER_ID).toString();
		
		this.userRepository.byId(userId).orElseThrow(() -> new ServiceException(USER_ISNOT_FOUND_MESSAGE));
		
		String territoryId = context.keySet().iterator().next();
		Optional<Territories> territory = this.territoryRepository.byId(territoryId);
		
		
		context.setResult(true);
		context.setCompleted();
		
		
		
	}
}
