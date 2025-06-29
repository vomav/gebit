package org.gebit.services.searching.handler;

import static org.gebit.authentication.CustomUserInfoProvider.USER_ID;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;

import org.gebit.gen.db.Image;
import org.gebit.gen.db.InWorkBy;
import org.gebit.gen.db.PartAssignments;
import org.gebit.gen.db.Territories;
import org.gebit.gen.db.TerritoryAssignments;
import org.gebit.gen.srv.searching.PartAssignmentsAssignPartToMeContext;
import org.gebit.gen.srv.searching.PartAssignmentsAssignPartToUserContext;
import org.gebit.gen.srv.searching.PartAssignmentsAssignToUnregistredUserContext;
import org.gebit.gen.srv.searching.PartAssignmentsCancelPartAssignmentContext;
import org.gebit.gen.srv.searching.PartAssignmentsUploadImageContext;
import org.gebit.gen.srv.searching.Searching_;
import org.gebit.gen.srv.searching.TerritoriesAssignToUserContext;
import org.gebit.gen.srv.searching.TerritoriesTrasferToAnotherSiteContext;
import org.gebit.gen.srv.searching.TerritoriesWithdrawFromUserContext;
import org.gebit.gen.srv.searching.TerritoryAssignments_;
import org.gebit.services.searching.handler.utils.XBaseUtils;
import org.gebit.services.searching.repository.ImageRepository;
import org.gebit.services.searching.repository.PartAssignmentsRepository;
import org.gebit.services.searching.repository.TerritoryAssignmentRepository;
import org.gebit.services.searching.repository.TerritoryRepository;
import org.gebit.spaces.ObjectStorage;
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

	private TerritoryRepository territoryRepository;
	private TerritoryAssignmentRepository territoryAssignmentRepository;
	private UserInfo userInfo;
	private PartAssignmentsRepository partsAssignmentsRepository;
	private ObjectStorage storageService;
	private XBaseUtils xbaseUtils;
	private ImageRepository imageRepository;


	public SearchingHandler(TerritoryRepository territoryRepository, 
			TerritoryAssignmentRepository territoryAssignmentRepository, 
			UserInfo userInfo, 
			PartAssignmentsRepository partsAssignmentsRepository, 
			ObjectStorage storageService, 
			XBaseUtils xbaseUtils, 
			ImageRepository imageRepository) {
		super();
		this.territoryRepository = territoryRepository;
		this.territoryAssignmentRepository=territoryAssignmentRepository;
		this.userInfo = userInfo;
		this.partsAssignmentsRepository = partsAssignmentsRepository;
		this.storageService = storageService;
		this.xbaseUtils = xbaseUtils;
		this.imageRepository = imageRepository;
		
		
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
	
	@On(event=TerritoriesWithdrawFromUserContext.CDS_NAME)
	public void onWithdrawFromUser(TerritoriesWithdrawFromUserContext c) {
		Territories assignment = territoryRepository.runCqn(c.getCqn());
		TerritoryAssignments territoryAssignments = territoryAssignmentRepository.byTerritoryId(assignment.getId()).orElseThrow(()-> new ServiceException("Assignment is not found"));
		territoryAssignmentRepository.deleteByTerritoryId(assignment.getId());
		
		assignment.setLastTimeWorked(LocalDate.now());
		territoryRepository.save(assignment);
		
		this.storageService.deleteDirectory(territoryAssignments.getTenantDiscriminator() +  "/" + territoryAssignments.getId());
		
		c.setResult(true);
		c.setCompleted();
	}
	
	@On(event=PartAssignmentsAssignPartToUserContext.CDS_NAME)
	public void assignPartToUser(PartAssignmentsAssignPartToUserContext c) {
		CqnSelect select = c.getCqn();
		PartAssignments pa =  this.partsAssignmentsRepository.runCqnSingleSelect(select, c.getModel());
		
		InWorkBy inWorkBy = InWorkBy.create();
		inWorkBy.setId(UUID.randomUUID().toString());
		inWorkBy.setToParentId(pa.getId());
		
		inWorkBy.setUserId(c.getUserId());
		if(pa.getInWorkBy() == null) {
			pa.setInWorkBy(List.of(inWorkBy));
		} else {
			pa.getInWorkBy().add(inWorkBy);
		}
		
		
		this.partsAssignmentsRepository.save(pa);
		
		c.setResult(true);
		c.setCompleted();
	}
	
	@On(event=PartAssignmentsAssignToUnregistredUserContext.CDS_NAME)
	public void assignPartToUser(PartAssignmentsAssignToUnregistredUserContext c) {
		CqnSelect select = c.getCqn();
		PartAssignments pa =  this.partsAssignmentsRepository.runCqnSingleSelect(select, c.getModel());
		
		InWorkBy inWorkBy = InWorkBy.create();
		inWorkBy.setId(UUID.randomUUID().toString());
		inWorkBy.setToParentId(pa.getId());
		
		inWorkBy.setFreestyleName(c.getName());
		if(pa.getInWorkBy() == null) {
			pa.setInWorkBy(List.of(inWorkBy));
		} else {
			pa.getInWorkBy().add(inWorkBy);
		}
		
		
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

	@On(event = PartAssignmentsUploadImageContext.CDS_NAME)
	public void uploadImage(PartAssignmentsUploadImageContext c) {
		PartAssignments assignment = partsAssignmentsRepository.runCqnSingleSelect(c.getCqn(), c.getModel());

		Optional<Image> maybeImage = this.imageRepository.byAssignmentId(assignment.getId());
		
		maybeImage.ifPresent(p -> this.imageRepository.deleteById(p.getId()));
		maybeImage.ifPresent(p -> this.storageService.deleteFile(p.getSpacePath()));
		
		String internalSpacePath = this.resolveInternalSpacePath(assignment.getTenantDiscriminator(), assignment.getToParentId(), assignment.getId(), c.getFile());
		
		String externalPathToImage = this.saveImageToSpace(c.getFile(), internalSpacePath, c.getMediaType());
		
		this.imageRepository.create(externalPathToImage, internalSpacePath, c.getMediaType(), assignment.getId());
	
		c.setResult("");
		c.setCompleted();
	}
	

	private String saveImageToSpace(String xBaseStr, String internalSpacePath, String mimeType) {
		
		byte[] image = Base64.decodeBase64(xbaseUtils.resolveXbasePayload(xBaseStr));
		try {
			return storageService.save(image, internalSpacePath, mimeType);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException("File is not uploaded");
		}

	}
	
	private String resolveInternalSpacePath(String tenantId, String assignmentId, String assignmentPartId,String xbaseStr) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(tenantId).append("/").append(assignmentId).append("/").append(assignmentPartId).append(".").append(xbaseUtils.resolveExtensionByPropic(xbaseStr));
		return buffer.toString();
	}

}
