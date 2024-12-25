package org.gebit.services.searching.mt.handler;

import java.util.List;
import java.util.Optional;

import org.gebit.authentication.CrossTenantPermissions;
import org.gebit.authentication.CustomUserInfoProvider;
import org.gebit.gen.srv.searching.PublicTerritoryAssignments;
import org.gebit.gen.srv.searching.PublicTerritoryAssignments_;
import org.gebit.gen.srv.searching.Searching;
import org.gebit.services.searching.mt.isolatate.CrossTenantAccess;
import org.springframework.stereotype.Component;

import com.sap.cds.CdsData;
import com.sap.cds.reflect.CdsElement;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsReadEventContext;
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

	public static final String TENANT_DESCRIMITATOR_COLUMN = "tenantDiscriminator";

	public TenantPersistenceServiceEnchancerHandler(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	@Before(event = PersistenceService.EVENT_CREATE, serviceType = { PersistenceService.class })
	public void onBeforeInsert(CdsCreateEventContext c, CdsData data) {
		CrossTenantAccess access = this.getCrossTenantAccessType(c.getModel(), c.getTarget().getQualifiedName());
		if (access != CrossTenantAccess.NONE && data.get(TENANT_DESCRIMITATOR_COLUMN) == null) {
			data.put(TENANT_DESCRIMITATOR_COLUMN, this.userInfo.getTenant());
		}
	}

	private CrossTenantAccess getCrossTenantAccessType(CdsModel model, String name) {
		Optional<CdsElement> tenantDiscriminatorElemOpt = model.getStructuredType(name).findElement(TENANT_DESCRIMITATOR_COLUMN);
		return tenantDiscriminatorElemOpt.isEmpty() ? CrossTenantAccess.NONE : CrossTenantAccess.TENANT;
	}

	@SuppressWarnings("unchecked")
	@After(event = PersistenceService.EVENT_READ, serviceType = { Searching.class }, entity = {
			PublicTerritoryAssignments_.CDS_NAME })
	public void onAfterReadPublicTerritoryAssignments(CdsReadEventContext c) {
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
