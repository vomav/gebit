package org.gebit.services.searching.mt.isolatate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.gebit.authentication.CrossTenantPermissions;
import org.gebit.authentication.CustomUserInfoProvider;
import org.gebit.gen.db.Users;
import org.gebit.gen.srv.searching.PublicTerritoryAssignments;
import org.gebit.gen.srv.searching.PublicTerritoryAssignments_;
import org.gebit.gen.srv.searching.Searching;
import org.gebit.services.searching.mt.crossaccess.CrossTenantAccessWhereModifier;
import org.gebit.services.searching.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sap.cds.CdsData;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.Modifier;
import com.sap.cds.reflect.CdsEntity;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.reflect.CdsStructuredType;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsDeleteEventContext;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.cds.CdsUpsertEventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.services.request.UserInfo;

@Component
@ServiceName(value = "*", type = PersistenceService.class)
public class TenantPersistenceServiceEnchancerHandler implements EventHandler {

	private UserInfo userInfo;
	private UserRepository userRepository;

	public static final String TENANT_DESCRIMITATOR_COLUMN = "tenantDiscriminator";

	public TenantPersistenceServiceEnchancerHandler(UserInfo userInfo,
			@Qualifier("searching_user_repository") UserRepository userRepository) {
		this.userInfo = userInfo;
		this.userRepository = userRepository;
		System.err.println("TenantPersistenceServiceEnchancerHandler-constructor");
	}

	@Before(event = { PersistenceService.EVENT_READ }, serviceType = { Searching.class })
	public void onBeforeRead(CdsReadEventContext c) {
		System.err.println("TenantPersistenceServiceEnchancerHandler-onBeforeRead- " + c.getTarget().getQualifiedName());
		
		CrossTenantAccess access = this.getCrossTenantAccessType(c.getModel(), c.getTarget().getQualifiedName());
		
		Optional<Modifier> m = this.resolveModifier(access, c.getTarget());

		if(m.isPresent()) 
			c.setCqn(CQL.copy(c.getCqn(), m.get()));
		
		System.out.println(c.getCqn());
		System.out.println("");

		
	}

	private Modifier createTenantSpecifictWhere(CdsEntity cdsEntity) {
		String tenant = userInfo.getTenant();
		return new TenantModifiedWhereType(tenant, cdsEntity);
	}

	@SuppressWarnings("unchecked")
	private Modifier createCrossTenantSpecifictWhere(CdsEntity cdsEntity) {
		List<CrossTenantPermissions> perms = (List<CrossTenantPermissions>) userInfo
				.getAdditionalAttribute(CustomUserInfoProvider.PERM);
		return new CrossTenantAccessWhereModifier(perms, cdsEntity);
	}

	@Before(event = PersistenceService.EVENT_DELETE, serviceType = { PersistenceService.class })
	public void onBeforeDelete(CdsDeleteEventContext c) {
		System.err.println("TenantPersistenceServiceEnchancerHandler-onBeforeDelete - " + c.getTarget().getQualifiedName());
		CrossTenantAccess access = this.getCrossTenantAccessType(c.getModel(), c.getTarget().getQualifiedName());
		
		Optional<Modifier> m = this.resolveModifier(access, c.getTarget());

		if(m.isPresent()) 
			c.setCqn(CQL.copy(c.getCqn(), m.get()));

		
	}

	@Before(event = PersistenceService.EVENT_UPDATE, serviceType = { PersistenceService.class })
	public void onBeforeUpdate(CdsUpdateEventContext c) {
		System.err.println("TenantPersistenceServiceEnchancerHandler-onBeforeUpdate- " + c.getTarget().getQualifiedName());
		CrossTenantAccess access = this.getCrossTenantAccessType(c.getModel(), c.getTarget().getQualifiedName());
		
		Optional<Modifier> m = this.resolveModifier(access, c.getTarget());

		if(m.isPresent()) 
			c.setCqn(CQL.copy(c.getCqn(), m.get()));

		
	}

	@Before(event = PersistenceService.EVENT_UPSERT, serviceType = { PersistenceService.class })
	public void onBeforeUpsert(CdsUpsertEventContext c) {
		System.err.println("TenantPersistenceServiceEnchancerHandler-onBeforeUpsert - " + c.getTarget().getQualifiedName());
		
		CrossTenantAccess access = this.getCrossTenantAccessType(c.getModel(), c.getTarget().getQualifiedName());
		
		Optional<Modifier> m = this.resolveModifier(access, c.getTarget());

		if(m.isPresent()) 
			c.setCqn(CQL.copy(c.getCqn(), m.get()));

		
		
	}
	
	private Optional<Modifier> resolveModifier(CrossTenantAccess access, CdsEntity entity) {
		if(access == CrossTenantAccess.TENANT) {
		    return Optional.of(createTenantSpecifictWhere(entity));
		} else if(access == CrossTenantAccess.USER_TENANT_MAPPINGS) {
			return Optional.of(createCrossTenantSpecifictWhere(entity));			
		}
		return Optional.empty();
		
	}

	@Before(event = PersistenceService.EVENT_CREATE, serviceType = { PersistenceService.class })
	public void onBeforeInsert(CdsCreateEventContext c, CdsData data) {
		System.err.println("TenantPersistenceServiceEnchancerHandler-onBeforeInsert - " + c.getTarget().getQualifiedName());
		
		CrossTenantAccess access = this.getCrossTenantAccessType(c.getModel(), c.getTarget().getQualifiedName());
		
		if(access == CrossTenantAccess.TENANT || access == CrossTenantAccess.USER_TENANT_MAPPINGS) {
			data.put(TENANT_DESCRIMITATOR_COLUMN, this.userInfo.getTenant());
		} 
		

	}
	
	private CrossTenantAccess getCrossTenantAccessType(CdsModel model, String name) {
		CdsStructuredType type = model.getStructuredType(name);
		Optional<?> tenantDiscriminatorElemOpt = type.findElement(TENANT_DESCRIMITATOR_COLUMN);
		Optional<?> tenantCrossTenantAccessOpt = type.findElement("accessBy");
		
		if(tenantDiscriminatorElemOpt.isEmpty() && tenantCrossTenantAccessOpt.isEmpty()) {
			return CrossTenantAccess.NONE;
		}
		
		return tenantDiscriminatorElemOpt.isPresent() && tenantCrossTenantAccessOpt.isPresent() ? CrossTenantAccess.USER_TENANT_MAPPINGS : CrossTenantAccess.TENANT;
	}

	@After(event = PersistenceService.EVENT_READ, serviceType = { PersistenceService.class }, entity = {
			PublicTerritoryAssignments_.CDS_NAME })
	public void onAfterReadPublicTerritoryAssignments(CdsReadEventContext c) {
		System.err.println("TenantPersistenceServiceEnchancerHandler-onAfterReadPublicTerritoryAssignments - " + c.getTarget().getQualifiedName());
		List<CrossTenantPermissions> perms = (List<CrossTenantPermissions>) userInfo
				.getAdditionalAttribute(CustomUserInfoProvider.PERM);
		c.getResult().streamOf(PublicTerritoryAssignments.class).forEach(assignment -> {
			String tenantDiscrimantor = assignment.getTenantDiscriminator();
			Optional<CrossTenantPermissions> tenantPermission = perms.stream()
					.filter(perm -> perm.getTenantId().equals(tenantDiscrimantor)).findAny();
			if (tenantPermission.isPresent())
				assignment.setSite(tenantPermission.get().getTenantName());
		});
	}

}
