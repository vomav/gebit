namespace srv;

using { db.Territories as dbTerritory, db.Parts as dbPart, db.TerritoryAssignments as dbTerritoryAssignment, db.PartAssignments as dbPartAssignmenst, db.Users as dbUser, db.Tenants as dbTenant, db.UserTenantMappings as dbUserTenantMapping } from '../db/database';

service searching {

    entity Territories as select from dbTerritory mixin{
        toTerritoryAssignment: Association to one TerritoryAssignments on toTerritoryAssignment.toTerritory = $self;
    }
    into {
        *,
        toTerritoryAssignment.assignedTo.name as assignedToName,
        toTerritoryAssignment.assignedTo.surname as assignedToSurname
    } actions {
       action assignToUser(userId:String) returns Boolean;
       action withdrawFromUser() returns Boolean;
    } ;
    entity Parts as projection on dbPart;

    @cds.redirection.target: true
    entity TerritoryAssignments as projection on dbTerritoryAssignment {
      *,
      toTerritory.name as name,
      toTerritory.link as link,
      toPartAssignments: redirected to PartAssignments
    };

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
    entity Users as projection on dbUser {
        *,
        toTerritories : Association to TerritoryAssignments on toTerritories.assignedTo=$self,
    } excluding {
        password
    };
    // entity UserTenantMappings as projection on dbUserTenantMapping;

    entity AvailableUsers as projection on dbUserTenantMapping {
        *,
        tenant.name as tenantName,
        user.email as email,
        user.name as name,
        user.surname as surname,
        mappingType as role
    }


}

service admin {

    entity Tenants as projection on dbTenant {
        *,
        createdBy.name as createdByName,
        createdBy.surname as createdBySurname,
        toUsers: redirected to UserTenantMappings on toUsers.tenant = $self
    } actions {
        action addUserByEmail(email:String, mappingType:String) returns Boolean
    }
    entity Users as projection on dbUser {
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

service ui_service {
    
    @odata.singleton
    entity LoggedInUser {
        key id:UUID;
        email: String(64);
        username: String(128);
        surname: String(128);
        tenant: String(32);
        language: String(10);
        role: String(10);
        isAdmin:Boolean;
        loggedToSite: String(32);
    }
}