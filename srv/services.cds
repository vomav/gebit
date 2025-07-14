namespace srv;

using { db.Territories as dbTerritory, db.Parts as dbPart, db.TerritoryAssignments as dbTerritoryAssignment, db.PartAssignments as dbPartAssignmenst, db.Users as dbUser, db.Tenants as dbTenant, db.UserTenantMappings as dbUserTenantMapping, db.InWorkBy as dbInworkBy, db.Image as Image, db.UnregisteredUserTerritoryAssignment as dbUnregisteredUserTerritoryAssignment } from '../db/database';

service searching {

    entity Territories     
    @(restrict: [
     { grant: '*',
       where: '$user.adminIn = tenantDiscriminator' } ])  
    as select from dbTerritory mixin {
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
        toAssigedUsersToThisAccount,
        toTerritoryAssignment.unregisteredUser as assignedUnregisteredUser,
        toTerritoryAssignment.unregisteredUserEmail as assignedUnregisteredUserEmail,
        toTerritoryAssignment.unregisteredUserAssignmentId as assignedUnregisteredUserAssignmentId,
        toTerritoryAssignment.ID as toTerritoryAssignmentId
    } actions {
       action assignToUser(userId:String) returns Boolean;
       action withdrawFromUser() returns Boolean;
       action trasferToAnotherSite(siteId:String) returns Boolean;
       action assignToUnregisteredUser(username:String @mandatory, email:String) returns Boolean;
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
      toPartAssignments: redirected to PartAssignments,
      toUnregisetredUserTerritoryAssignments: redirected to UnregisteredUserTerritoryAssugnment on toUnregisetredUserTerritoryAssignments.toTerritoryAssignment = $self,
      toUnregisetredUserTerritoryAssignments.unregisteredUser as unregisteredUser,
      toUnregisetredUserTerritoryAssignments.unregisteredUserEmail as unregisteredUserEmail,
      toUnregisetredUserTerritoryAssignments.ID as unregisteredUserAssignmentId
    };

    entity UnregisteredUserTerritoryAssugnment as projection on dbUnregisteredUserTerritoryAssignment {
        *,
        toTerritoryAssignment.isDone as isDone,
        toTerritoryAssignment.assignedTo as assignedTo,
        toTerritoryAssignment.type as type,
        toTerritoryAssignment.startedDate as startedDate,
        toTerritoryAssignment.finishedDate as finishedDate,
        toTerritoryAssignment.toTerritory.name as name,
        toTerritoryAssignment.toTerritory.link as link,
        toTerritoryAssignment.toPartAssignments: redirected to PartAssignments
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
        inWorkBy: redirected to InWorkBy on inWorkBy.toParent = $self,
        toWorkedPartImage.imageUrl as workedPartImageUrl,
        toWorkedPartImage.mediaType as workedPartImageMediaType
    } actions {
        action assignPartToMe() returns Boolean;
        action assignPartToUser(userId: String) returns Boolean;
        action cancelPartAssignment() returns Boolean;
        action uploadImage(file: String, mediaType: String) returns String;
        action assignToUnregistredUser(name: String) returns Boolean;
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

    entity InWorkBy as projection on dbInworkBy {
        user.name as username,
        user.surname as surname,
        user.ID as userId,
        ID as id,
        toParent as toParent,
        freestyleName as freestyleName
    };

    entity Images as projection on Image;
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
        action addUserByEmail(email:String, mappingType:String) returns Boolean;
        action removeSite() returns Boolean;
    };

    @odata.singleton
    entity LoggedInUser as projection on dbUser {
        *,
    } excluding {
        password,
        myTerritories
    } actions {
        action changePassword(oldPassword:String, newPassword:String) returns Boolean;    };
    
    entity UserTenantMappings as projection on dbUserTenantMapping 
    {
        *,
        user.name as username,
        user.surname as surname,
        user.email as email,
    };

    action createSite(name:String @mandatory, description:String @mandatory) returns Boolean;
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
    action activateAccount(userId:UUID, activationCode:String) returns Boolean;
}

service publicSearching {
    entity PublicTerritoryAssignments as select from dbUnregisteredUserTerritoryAssignment {
        *,
        toTerritoryAssignment.isDone as isDone,
        toTerritoryAssignment.assignedTo as assignedTo,
        toTerritoryAssignment.type as type,
        toTerritoryAssignment.startedDate as startedDate,
        toTerritoryAssignment.finishedDate as finishedDate,
        toTerritoryAssignment.toTerritory.name as name,
        toTerritoryAssignment.toTerritory.link as link,
        toTerritoryAssignment.toPartAssignments: redirected to PartAssignments on toPartAssignments.toParent = toTerritoryAssignment,
    }
    
    entity PartAssignments as select from dbPartAssignmenst {
        *,
        part.name as name,
        part.coordinates as coordinates,
        part.isBoundaries as isBoundaries,
        inWorkBy: redirected to InWorkBy on inWorkBy.toParent = $self,
        toWorkedPartImage.imageUrl as workedPartImageUrl,
        toWorkedPartImage.mediaType as workedPartImageMediaType
    } actions {
        action assignPartToMe() returns Boolean;
        action cancelMyAssignment() returns Boolean;
    }

    entity InWorkBy as projection on dbInworkBy {
        user.name as username,
        user.surname as surname,
        user.ID as userId,
        ID as id,
        toParent as toParent,
        freestyleName as freestyleName
    }
}
