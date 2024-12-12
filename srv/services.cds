namespace srv;

using { db.Territories as dbTerritory, db.Parts as dbPart, db.TerritoryAssignments as dbTerritoryAssignment, db.PartAssignments as dbPartAssignmenst, db.Users as dbUser, db.Tenants as dbTenant, db.UserTenantMappings as dbUserTenantMapping, db.crossTenant as dbCrossTenant } from '../db/database';

service searching {

    entity Territories as select from dbTerritory mixin{
        toTerritoryAssignment: Association to one TerritoryAssignments on toTerritoryAssignment.toTerritory = $self;
    }
    into {
        *,
        toTerritoryAssignment.assignedTo.name as assignedToName,
        toTerritoryAssignment.assignedTo.surname as assignedToSurname,
        toAllowedUsers: redirected to TenantMappings on toAllowedUsers.tenant.ID = tenantDiscriminator
    } actions {
       action assignToUser(userId:String) returns Boolean;
       action withdrawFromUser() returns Boolean;
       action trasferToAnotherSite(siteId:String) returns Boolean;
    } ;
    entity Parts as projection on dbPart;

    @cds.redirection.target: true
    entity TerritoryAssignments as projection on dbTerritoryAssignment {
      *,
      toTerritory.name as name,
      toTerritory.link as link,
      toPartAssignments: redirected to PartAssignments
    };

    @cds.redirection.target: false
    entity PublicTerritoryAssignments as select from dbTerritoryAssignment {
      *,
      toTerritory.name as name,
      toTerritory.link as link,
      toPartAssignments: redirected to PartAssignments
    } where type = 'Public' and toTerritory.isReady = true;


    extend projection PublicTerritoryAssignments with {
        virtual null as site: String
    };
     
    
    
    entity PartAssignments as projection on dbPartAssignmenst {
        *,
        part.name as name,
        part.coordinates as coordinates,
        part.isBoundaries as isBoundaries,
        inWorkBy.name as userName,
        inWorkBy.surname as surname
    } actions {
        action assignPartToMe() returns Boolean;
        action assignPartToUser(userId: String) returns Boolean;
        action cancelPartAssignment() returns Boolean;
    };

    entity TenantMappings as projection on dbUserTenantMapping {
        *,
        user.name as name,
        user.surname as surname,
        user.email as email,
        tenant.name as siteName,
        tenant.description as siteDescription
    }

    entity Tenants as projection on dbTenant {
       *
    };

}

service admin {

    entity Tenants as projection on dbTenant {
        *,
        createdBy.name as createdByName,
        createdBy.surname as createdBySurname,
        toUsers: redirected to UserTenantMappings on toUsers.tenant = $self
    } actions {
        action addUserByEmail(email:String, mappingType:String) returns Boolean
    };

    @odata.singleton
    entity LoggedInUser as projection on dbUser {
        *,
    } excluding {
        password,
        myTerritories
    };
    
    entity UserTenantMappings as projection on dbUserTenantMapping {
        *,
        user.name as username,
        user.surname as surname,
        user.email as email
    };

    action createSite(name:String, description:String) returns Boolean;
}


service registration {
    type RegistredUser {
        id: UUID;
        email: String(64);
        name: String(128);
        surname: String(128);
        tenant: String(32);
        oid:String(64);
    }
    action register(email:String not null, name:String not null, surname: String not null, password:String not null) returns RegistredUser;

    action resetPassword(email:String) returns Boolean;
}
