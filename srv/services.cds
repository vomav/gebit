namespace srv;

using { db.Territory as dbTerritory, db.Part as dbPart, db.TerritoryAssignment as dbTerritoryAssignment, db.PartAssignmenst as dbPartAssignmenst, db.User as dbUser, db.Tenant as dbTenant, db.UserTenantMapping as dbUserTenantMapping } from '../db/database';

service searching {

    entity Territory as projection on dbTerritory;
    entity Parts as projection on dbPart;
    entity TerritoryAssignment as projection on dbTerritoryAssignment;
    entity PartsInProgress as projection on dbPartAssignmenst;
    entity User as projection on dbUser {
        *,
        myTerritories : Association to TerritoryAssignment on myTerritories.assignedTo=$self,
    } excluding {
        password
    };
    entity Tenant as projection on dbTenant;
    entity UserTenantMapping as projection on dbUserTenantMapping;


}

service admin {
    entity Tenant as projection on dbTenant;
        entity User as projection on dbUser {
        *,
    } excluding {
        password,
        myTerritories
    };
    entity UserTenantMapping as projection on dbUserTenantMapping;

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