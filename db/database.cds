namespace db;

using { cuid } from '@sap/cds/common';

aspect tenant {
    tenantDiscriminator: UUID;
}

aspect crossTenant {
    accessBy : String(36) default 'UserTenantMappings';
}

entity Territories  : cuid, tenant {
    name: String(64);
    link: String(2048);
    isReady: Boolean default false;
    embedUrl: String(2048);
    toParts : Composition of many Parts on toParts.toParent=$self;
    lastTimeWorked: Date;
    createdAt: Timestamp;
    updatedAt: Timestamp;
}

entity Parts : cuid, tenant {
    name: String(64);
    isBoundaries: Boolean default false;
    coordinates: String(1024);
    toParent: Association to one Territories;
}

// type GeographyPolygon : String(1024) @odata.Type : 'Edm.GeographyPolygon';


entity TerritoryAssignments : cuid, tenant, crossTenant {
    toTerritory: Association to one Territories;
    isDone: Boolean;
    toPartAssignments: Composition of many PartAssignments on toPartAssignments.toParent = $self;
    startedDate:Timestamp;
    finishedDate:Timestamp;
    type:String(32) enum {
        Personal;
        Public;
    };
    assignedTo: Association to one Users;
    
}

entity PartAssignments : cuid, tenant, crossTenant {
    part: Association to one Parts;
    inWorkBy: Association to one Users;
    isDone: Boolean;
    toParent: Association to TerritoryAssignments;
}

entity Users : cuid {
    email: String(64);
    name: String(128);
    surname: String(128);
    currentTenant: Association to one Tenants;
    password: String(128);
    oid:String(64);
    toAllowedTenants: Association to many UserTenantMappings on toAllowedTenants.user=$self;
    toTerritories: Association to many TerritoryAssignments on toTerritories.assignedTo=$self;
    refreshToken: String(2048);
}

entity UserTenantMappings {
   key user: Association to one Users;
   key tenant: Association to one Tenants;
   mappingType: String(32) enum{ 
        admin;
        user
    };
}


entity Tenants : cuid {
    createdAt: Timestamp @cds.on.insert: $now;
    createdBy: Association to one Users;
    name: String(128);
    description: String(1024);
    toUsers: Association to many UserTenantMappings on toUsers.tenant=$self;
    virtual myRole: String(32);
    // toAdministrators: Association to many UserTenantMappings on toAdministrators.tenant = $self and toAdministrators.mappingType = 'admin';
}

