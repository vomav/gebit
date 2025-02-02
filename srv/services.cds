namespace srv;

using { db.Territories as dbTerritory, db.Parts as dbPart, db.TerritoryAssignments as dbTerritoryAssignment, db.PartAssignments as dbPartAssignmenst, db.Users as dbUser, db.Tenants as dbTenant, db.UserTenantMappings as dbUserTenantMapping } from '../db/database';

service searching {

    entity Territories     
    @(restrict: [
     { grant: '*',
       where: '$user.adminIn = tenantDiscriminator' } ])  
    as select from dbTerritory mixin{
        toTerritoryAssignment: Association to one TerritoryAssignments on toTerritoryAssignment.toTerritory = $self;
        toAssigedUsersToThisAccount: Association to many TenantMappings on toAssigedUsersToThisAccount.siteId = $self.siteId;
    }
    into {
        *,
        toTerritoryAssignment.assignedTo.name as assignedToName,
        toTerritoryAssignment.assignedTo.surname as assignedToSurname,
        toTenant.name as siteName,
        toTenant.description as siteDescription,
        toTenant.ID as siteId,
        toAssigedUsersToThisAccount
    } actions {
       action assignToUser(userId:String) returns Boolean;
       action withdrawFromUser() returns Boolean;
       action trasferToAnotherSite(siteId:String) returns Boolean;
    } ;
    entity Parts @(restrict: [
     { grant: '*',
       where: '$user.adminIn = tenantDiscriminator' } ]) 
     as projection on dbPart;

    @cds.redirection.target: true
    entity TerritoryAssignments
    @(restrict: [
     { grant: '*',
       where: '$user.userIn = tenantDiscriminator' } ]) 
    as projection on dbTerritoryAssignment {
      *,
      toTerritory.name as name,
      toTerritory.link as link,
      toPartAssignments: redirected to PartAssignments
    };

    @cds.redirection.target: false
    entity PublicTerritoryAssignments
    @(restrict: [
     { grant: '*',
       where: '$user.userIn = tenantDiscriminator' } ]) 
    as select from dbTerritoryAssignment {
      *,
      toTerritory.name as name,
      toTerritory.link as link,
      toPartAssignments: redirected to PartAssignments
    } where type = 'Public' and toTerritory.isReady = true ;


    extend projection PublicTerritoryAssignments with {
        virtual null as site: String
    };
     
    
    @(restrict: [
     { grant: '*',
       where: '$user.userIn = tenantDiscriminator' } ]) 
    entity PartAssignments as select from dbPartAssignmenst {
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
    } ;

    entity TenantMappings as projection on dbUserTenantMapping {
        *,
        user.name as name,
        user.surname as surname,
        user.email as email,
        tenant.name as siteName,
        tenant.ID as siteId,
        tenant.description as siteDescription
    }

    entity Tenants
    @(restrict: [
     { grant: '*',
       where: '$user.adminIn = ID' } ]) 
    as projection on dbTenant {
       *
    };

}

service admin {

    entity Tenants 
    @(restrict: [
     { grant: '*',
       where: '$user.adminIn = ID' } ]) 
    as projection on dbTenant {
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
    
    entity UserTenantMappings as projection on dbUserTenantMapping 
    {
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
