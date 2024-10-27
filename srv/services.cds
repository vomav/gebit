namespace srv;

using { db.Territories as dbTerritory, db.Parts as dbPart, db.TerritoryAssignments as dbTerritoryAssignment, db.PartAssignmensts as dbPartAssignmenst, db.Users as dbUser, db.Tenants as dbTenant, db.UserTenantMappings as dbUserTenantMapping } from '../db/database';

service searching {

    entity Territories as projection on dbTerritory actions {
       action assignToUser(userId:String) returns Boolean;
    };
    entity Parts as projection on dbPart;
    entity TerritoryAssignments as projection on dbTerritoryAssignment {
      *,
      toTerritory.name as name,
      toTerritory.link as link,
      toTerritory.isReady as isReady
    };
    entity PartsInProgresss as projection on dbPartAssignmenst;
    entity User as projection on dbUser {
        *,
        toTerritories : Association to TerritoryAssignments on toTerritories.assignedTo=$self,
    } excluding {
        password
    };
    entity Tenants as projection on dbTenant;
    entity UserTenantMappings as projection on dbUserTenantMapping;


}

service admin {
    entity Tenants as projection on dbTenant;
        entity Users as projection on dbUser {
        *,
    } excluding {
        password,
        myTerritories
    };
    entity UserTenantMappings as projection on dbUserTenantMapping;

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
    }
}