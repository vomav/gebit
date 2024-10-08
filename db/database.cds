namespace db;

using { cuid } from '@sap/cds/common';

aspect tenant {
    tenant: UUID;
}

entity Territory : cuid, tenant {
    name: String(64);
    link: String(2048);
    embedUrl: String(2048);
    toParts : Composition of many Part on toParts.toParent=$self;
    lastTimeWorked: Date;
}

entity Part : cuid, tenant {
    name: String(64);
    coordinates: array of Point;
    toParent: Association to one Territory;
}

type Point : {
    lattitude: Double;
    longitude: Double;
}

entity TerritoryAssignment : cuid, tenant {
    toTerritory: Association to one Territory;
    isDone: Boolean;
    toPartAssignments: Composition of many PartAssignmenst on toPartAssignments.toParent = $self;
    startedDate:Timestamp;
    finishedDate:Timestamp;
    type:String(32) enum {
        Personal = 'Personal';
        Public = 'Public';
    };
    assignedTo: Association to one User;
}

entity PartAssignmenst : cuid, tenant {
    part: Association to one Part;
    inWorkBy: String(128);
    isDone: Boolean;
    toParent: Association to TerritoryAssignment;
}

entity User : cuid {
    email: String(64);
    name: String(128);
    surname: String(128);
    currentTenant: Association to one Tenant;
    password: String(128);
    oid:String(64);
    allowedTenants: Association to many UserTenantMapping on allowedTenants.user=$self;
    myTerritories: Association to many TerritoryAssignment on myTerritories.assignedTo=$self;
    refreshToken: String(2048);
}

entity TenantMappingRequest : cuid {
    tenant: Association to one Tenant;
    requestFrom: Association to one User;
    comment: String(256);
}

entity UserTenantMapping {
   key user: Association to one User;
   key tenant: Association to one Tenant;
   mappingType: String(32) enum{ 
        admin;
        user
    };
}


entity Tenant : cuid {
    createdAt: Timestamp @cds.on.insert: $now;
    createdBy: Association to one User;
    name: String(128);
    description: String(1024);
    allowedUsers: Association to many UserTenantMapping on allowedUsers.tenant=$self;
    administrators: Association to many UserTenantMapping on administrators.tenant = $self and administrators.mappingType = 'admin';
}
