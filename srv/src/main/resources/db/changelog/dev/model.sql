
DROP VIEW IF EXISTS srv_searching_Territories;
DROP VIEW IF EXISTS srv_publicSearching_Image;
DROP VIEW IF EXISTS srv_publicSearching_Parts;
DROP VIEW IF EXISTS srv_publicSearching_InWorkBy;
DROP VIEW IF EXISTS srv_publicSearching_PartAssignments;
DROP VIEW IF EXISTS srv_publicSearching_PublicTerritoryAssignments;
DROP VIEW IF EXISTS srv_admin_UserTenantMappings;
DROP VIEW IF EXISTS srv_admin_LoggedInUser;
DROP VIEW IF EXISTS srv_admin_Tenants;
DROP VIEW IF EXISTS srv_searching_Images;
DROP VIEW IF EXISTS srv_searching_InWorkBy;
DROP VIEW IF EXISTS srv_searching_Tenants;
DROP VIEW IF EXISTS srv_searching_TenantMappings;
DROP VIEW IF EXISTS srv_searching_PartAssignments;
DROP VIEW IF EXISTS srv_searching_PublicTerritoryAssignments;
DROP VIEW IF EXISTS srv_searching_UnregisteredUserTerritoryAssugnment;
DROP VIEW IF EXISTS srv_searching_TerritoryAssignments;
DROP VIEW IF EXISTS srv_searching_Parts;
DROP TABLE IF EXISTS db_Tenants;
DROP TABLE IF EXISTS db_UserTenantMappings;
DROP TABLE IF EXISTS db_UserAccountActivations;
DROP TABLE IF EXISTS db_Users;
DROP TABLE IF EXISTS db_InWorkBy;
DROP TABLE IF EXISTS db_Image;
DROP TABLE IF EXISTS db_PartAssignments;
DROP TABLE IF EXISTS db_UnregisteredUserTerritoryAssignment;
DROP TABLE IF EXISTS db_TerritoryAssignments;
DROP TABLE IF EXISTS db_Parts;
DROP TABLE IF EXISTS db_Territories;

CREATE TABLE db_Territories (
  ID VARCHAR(36) NOT NULL,
  tenantDiscriminator VARCHAR(36),
  name VARCHAR(64),
  link VARCHAR(2048),
  isReady BOOLEAN DEFAULT FALSE,
  embedUrl VARCHAR(2048),
  lastTimeWorked DATE,
  createdAt TIMESTAMP,
  updatedAt TIMESTAMP,
  PRIMARY KEY(ID)
); 

CREATE TABLE db_Parts (
  ID VARCHAR(36) NOT NULL,
  tenantDiscriminator VARCHAR(36),
  name VARCHAR(64),
  isBoundaries BOOLEAN DEFAULT FALSE,
  coordinates VARCHAR(1024),
  toParent_ID VARCHAR(36),
  PRIMARY KEY(ID)
); 

CREATE TABLE db_TerritoryAssignments (
  ID VARCHAR(36) NOT NULL,
  tenantDiscriminator VARCHAR(36),
  toTerritory_ID VARCHAR(36),
  isDone BOOLEAN,
  startedDate TIMESTAMP,
  finishedDate TIMESTAMP,
  type VARCHAR(32),
  assignedTo_ID VARCHAR(36),
  assignedToUnregisteredUser VARCHAR(128),
  PRIMARY KEY(ID)
); 

CREATE TABLE db_UnregisteredUserTerritoryAssignment (
  ID VARCHAR(36) NOT NULL,
  unregisteredUser VARCHAR(128),
  unregisteredUserEmail VARCHAR(128),
  toTerritoryAssignment_ID VARCHAR(36),
  PRIMARY KEY(ID)
); 

CREATE TABLE db_PartAssignments (
  ID VARCHAR(36) NOT NULL,
  tenantDiscriminator VARCHAR(36),
  part_ID VARCHAR(36),
  isDone BOOLEAN,
  imageUrl VARCHAR(512),
  toParent_ID VARCHAR(36),
  PRIMARY KEY(ID)
); 

CREATE TABLE db_Image (
  ID VARCHAR(36) NOT NULL,
  tenantDiscriminator VARCHAR(36),
  imageUrl VARCHAR(512),
  spacePath VARCHAR(512),
  mediaType VARCHAR(64),
  toParent_ID VARCHAR(36),
  PRIMARY KEY(ID)
); 

CREATE TABLE db_InWorkBy (
  ID VARCHAR(36) NOT NULL,
  user_ID VARCHAR(36),
  freestyleName VARCHAR(128),
  toParent_ID VARCHAR(36),
  PRIMARY KEY(ID)
); 

CREATE TABLE db_Users (
  ID VARCHAR(36) NOT NULL,
  email VARCHAR(64),
  name VARCHAR(128),
  surname VARCHAR(128),
  currentTenant_ID VARCHAR(36),
  password VARCHAR(128),
  previousPassword VARCHAR(128),
  refreshToken VARCHAR(2048),
  isActivated BOOLEAN,
  PRIMARY KEY(ID)
); 

CREATE TABLE db_UserAccountActivations (
  ID VARCHAR(36) NOT NULL,
  toUser_ID VARCHAR(36),
  activationCode INTEGER,
  PRIMARY KEY(ID)
); 

CREATE TABLE db_UserTenantMappings (
  user_ID VARCHAR(36) NOT NULL,
  tenant_ID VARCHAR(36) NOT NULL,
  mappingType VARCHAR(32),
  PRIMARY KEY(user_ID, tenant_ID)
); 

CREATE TABLE db_Tenants (
  ID VARCHAR(36) NOT NULL,
  createdAt TIMESTAMP,
  createdBy_ID VARCHAR(36),
  name VARCHAR(128),
  description VARCHAR(1024),
  defaultUserTenant BOOLEAN DEFAULT TRUE,
  PRIMARY KEY(ID)
); 

CREATE VIEW srv_searching_Parts AS SELECT
  dbPart_0.ID,
  dbPart_0.tenantDiscriminator,
  dbPart_0.name,
  dbPart_0.isBoundaries,
  dbPart_0.coordinates,
  dbPart_0.toParent_ID
FROM db_Parts AS dbPart_0; 

CREATE VIEW srv_searching_TerritoryAssignments AS SELECT
  dbTerritoryAssignment_0.ID,
  dbTerritoryAssignment_0.tenantDiscriminator,
  dbTerritoryAssignment_0.toTerritory_ID,
  dbTerritoryAssignment_0.isDone,
  dbTerritoryAssignment_0.startedDate,
  dbTerritoryAssignment_0.finishedDate,
  dbTerritoryAssignment_0.type,
  dbTerritoryAssignment_0.assignedTo_ID,
  dbTerritoryAssignment_0.assignedToUnregisteredUser,
  toTerritory_1.name AS name,
  toTerritory_1.link AS link,
  toUnregisetredUserTerritoryAssignments_2.unregisteredUser AS unregisteredUser,
  toUnregisetredUserTerritoryAssignments_2.unregisteredUserEmail AS unregisteredUserEmail,
  toUnregisetredUserTerritoryAssignments_2.ID AS unregisteredUserAssignmentId
FROM ((db_TerritoryAssignments AS dbTerritoryAssignment_0 LEFT JOIN db_Territories AS toTerritory_1 ON dbTerritoryAssignment_0.toTerritory_ID = toTerritory_1.ID) LEFT JOIN db_UnregisteredUserTerritoryAssignment AS toUnregisetredUserTerritoryAssignments_2 ON (toUnregisetredUserTerritoryAssignments_2.toTerritoryAssignment_ID = dbTerritoryAssignment_0.ID)); 

CREATE VIEW srv_searching_UnregisteredUserTerritoryAssugnment AS SELECT
  dbUnregisteredUserTerritoryAssignment_0.ID,
  dbUnregisteredUserTerritoryAssignment_0.unregisteredUser,
  dbUnregisteredUserTerritoryAssignment_0.unregisteredUserEmail,
  dbUnregisteredUserTerritoryAssignment_0.toTerritoryAssignment_ID,
  toTerritoryAssignment_1.isDone AS isDone,
  toTerritoryAssignment_1.assignedTo_ID AS assignedTo_ID,
  toTerritoryAssignment_1.type AS type,
  toTerritoryAssignment_1.startedDate AS startedDate,
  toTerritoryAssignment_1.finishedDate AS finishedDate,
  toTerritory_2.name AS name,
  toTerritory_2.link AS link
FROM ((db_UnregisteredUserTerritoryAssignment AS dbUnregisteredUserTerritoryAssignment_0 LEFT JOIN db_TerritoryAssignments AS toTerritoryAssignment_1 ON dbUnregisteredUserTerritoryAssignment_0.toTerritoryAssignment_ID = toTerritoryAssignment_1.ID) LEFT JOIN db_Territories AS toTerritory_2 ON toTerritoryAssignment_1.toTerritory_ID = toTerritory_2.ID); 

CREATE VIEW srv_searching_PublicTerritoryAssignments AS SELECT
  dbTerritoryAssignment_0.ID,
  dbTerritoryAssignment_0.tenantDiscriminator,
  dbTerritoryAssignment_0.toTerritory_ID,
  dbTerritoryAssignment_0.isDone,
  dbTerritoryAssignment_0.startedDate,
  dbTerritoryAssignment_0.finishedDate,
  dbTerritoryAssignment_0.type,
  dbTerritoryAssignment_0.assignedTo_ID,
  dbTerritoryAssignment_0.assignedToUnregisteredUser,
  toTerritory_1.name AS name,
  toTerritory_1.link AS link
FROM (db_TerritoryAssignments AS dbTerritoryAssignment_0 LEFT JOIN db_Territories AS toTerritory_1 ON dbTerritoryAssignment_0.toTerritory_ID = toTerritory_1.ID)
WHERE dbTerritoryAssignment_0.type = 'Public' AND toTerritory_1.isReady = TRUE; 

CREATE VIEW srv_searching_PartAssignments AS SELECT
  dbPartAssignmenst_0.ID,
  dbPartAssignmenst_0.tenantDiscriminator,
  dbPartAssignmenst_0.part_ID,
  dbPartAssignmenst_0.isDone,
  dbPartAssignmenst_0.imageUrl,
  dbPartAssignmenst_0.toParent_ID,
  part_1.name AS name,
  part_1.coordinates AS coordinates,
  part_1.isBoundaries AS isBoundaries,
  toWorkedPartImage_2.imageUrl AS workedPartImageUrl,
  toWorkedPartImage_2.mediaType AS workedPartImageMediaType
FROM ((db_PartAssignments AS dbPartAssignmenst_0 LEFT JOIN db_Parts AS part_1 ON dbPartAssignmenst_0.part_ID = part_1.ID) LEFT JOIN db_Image AS toWorkedPartImage_2 ON (toWorkedPartImage_2.toParent_ID = dbPartAssignmenst_0.ID)); 

CREATE VIEW srv_searching_TenantMappings AS SELECT
  dbUserTenantMapping_0.user_ID,
  dbUserTenantMapping_0.tenant_ID,
  dbUserTenantMapping_0.mappingType,
  user_1.name AS name,
  user_1.surname AS surname,
  user_1.email AS email,
  tenant_2.name AS siteName,
  tenant_2.ID AS siteId,
  tenant_2.description AS siteDescription
FROM ((db_UserTenantMappings AS dbUserTenantMapping_0 LEFT JOIN db_Users AS user_1 ON dbUserTenantMapping_0.user_ID = user_1.ID) LEFT JOIN db_Tenants AS tenant_2 ON dbUserTenantMapping_0.tenant_ID = tenant_2.ID); 

CREATE VIEW srv_searching_Tenants AS SELECT
  dbTenant_0.ID,
  dbTenant_0.createdAt,
  dbTenant_0.createdBy_ID,
  dbTenant_0.name,
  dbTenant_0.description,
  dbTenant_0.defaultUserTenant
FROM db_Tenants AS dbTenant_0; 

CREATE VIEW srv_searching_InWorkBy AS SELECT
  user_1.name AS username,
  user_1.surname AS surname,
  user_1.ID AS userId,
  dbInworkBy_0.ID AS id,
  dbInworkBy_0.toParent_ID AS toParent_ID,
  dbInworkBy_0.freestyleName AS freestyleName
FROM (db_InWorkBy AS dbInworkBy_0 LEFT JOIN db_Users AS user_1 ON dbInworkBy_0.user_ID = user_1.ID); 

CREATE VIEW srv_searching_Images AS SELECT
  Image_0.ID,
  Image_0.tenantDiscriminator,
  Image_0.imageUrl,
  Image_0.spacePath,
  Image_0.mediaType,
  Image_0.toParent_ID
FROM db_Image AS Image_0; 

CREATE VIEW srv_admin_Tenants AS SELECT
  dbTenant_0.ID,
  dbTenant_0.createdAt,
  dbTenant_0.createdBy_ID,
  dbTenant_0.name,
  dbTenant_0.description,
  dbTenant_0.defaultUserTenant,
  createdBy_1.name AS createdByName,
  createdBy_1.surname AS createdBySurname
FROM (db_Tenants AS dbTenant_0 LEFT JOIN db_Users AS createdBy_1 ON dbTenant_0.createdBy_ID = createdBy_1.ID); 

CREATE VIEW srv_admin_LoggedInUser AS SELECT
  dbUser_0.ID,
  dbUser_0.email,
  dbUser_0.name,
  dbUser_0.surname,
  dbUser_0.currentTenant_ID,
  dbUser_0.previousPassword,
  dbUser_0.refreshToken,
  dbUser_0.isActivated
FROM db_Users AS dbUser_0; 

CREATE VIEW srv_admin_UserTenantMappings AS SELECT
  dbUserTenantMapping_0.user_ID,
  dbUserTenantMapping_0.tenant_ID,
  dbUserTenantMapping_0.mappingType,
  user_1.name AS username,
  user_1.surname AS surname,
  user_1.email AS email
FROM (db_UserTenantMappings AS dbUserTenantMapping_0 LEFT JOIN db_Users AS user_1 ON dbUserTenantMapping_0.user_ID = user_1.ID); 

CREATE VIEW srv_publicSearching_PublicTerritoryAssignments AS SELECT
  dbUnregisteredUserTerritoryAssignment_0.ID,
  dbUnregisteredUserTerritoryAssignment_0.unregisteredUser,
  dbUnregisteredUserTerritoryAssignment_0.unregisteredUserEmail,
  dbUnregisteredUserTerritoryAssignment_0.toTerritoryAssignment_ID,
  toTerritoryAssignment_1.isDone AS isDone,
  toTerritoryAssignment_1.assignedTo_ID AS assignedTo_ID,
  toTerritoryAssignment_1.type AS type,
  toTerritoryAssignment_1.startedDate AS startedDate,
  toTerritoryAssignment_1.finishedDate AS finishedDate,
  toTerritory_2.name AS name,
  toTerritory_2.link AS link
FROM ((db_UnregisteredUserTerritoryAssignment AS dbUnregisteredUserTerritoryAssignment_0 LEFT JOIN db_TerritoryAssignments AS toTerritoryAssignment_1 ON dbUnregisteredUserTerritoryAssignment_0.toTerritoryAssignment_ID = toTerritoryAssignment_1.ID) LEFT JOIN db_Territories AS toTerritory_2 ON toTerritoryAssignment_1.toTerritory_ID = toTerritory_2.ID); 

CREATE VIEW srv_publicSearching_PartAssignments AS SELECT
  dbPartAssignmenst_0.ID,
  dbPartAssignmenst_0.tenantDiscriminator,
  dbPartAssignmenst_0.part_ID,
  dbPartAssignmenst_0.isDone,
  dbPartAssignmenst_0.imageUrl,
  dbPartAssignmenst_0.toParent_ID,
  part_1.name AS name,
  part_1.coordinates AS coordinates,
  part_1.isBoundaries AS isBoundaries,
  toWorkedPartImage_2.imageUrl AS workedPartImageUrl,
  toWorkedPartImage_2.mediaType AS workedPartImageMediaType
FROM ((db_PartAssignments AS dbPartAssignmenst_0 LEFT JOIN db_Parts AS part_1 ON dbPartAssignmenst_0.part_ID = part_1.ID) LEFT JOIN db_Image AS toWorkedPartImage_2 ON (toWorkedPartImage_2.toParent_ID = dbPartAssignmenst_0.ID)); 

CREATE VIEW srv_publicSearching_InWorkBy AS SELECT
  user_1.name AS username,
  user_1.surname AS surname,
  user_1.ID AS userId,
  dbInworkBy_0.ID AS id,
  dbInworkBy_0.toParent_ID AS toParent_ID,
  dbInworkBy_0.freestyleName AS freestyleName
FROM (db_InWorkBy AS dbInworkBy_0 LEFT JOIN db_Users AS user_1 ON dbInworkBy_0.user_ID = user_1.ID); 

CREATE VIEW srv_publicSearching_Parts AS SELECT
  Parts_0.ID,
  Parts_0.tenantDiscriminator,
  Parts_0.name,
  Parts_0.isBoundaries,
  Parts_0.coordinates,
  Parts_0.toParent_ID
FROM db_Parts AS Parts_0; 

CREATE VIEW srv_publicSearching_Image AS SELECT
  Image_0.ID,
  Image_0.tenantDiscriminator,
  Image_0.imageUrl,
  Image_0.spacePath,
  Image_0.mediaType,
  Image_0.toParent_ID
FROM db_Image AS Image_0; 

CREATE VIEW srv_searching_Territories AS SELECT
  dbTerritory_0.ID,
  dbTerritory_0.tenantDiscriminator,
  dbTerritory_0.name,
  dbTerritory_0.link,
  dbTerritory_0.isReady,
  dbTerritory_0.embedUrl,
  dbTerritory_0.lastTimeWorked,
  dbTerritory_0.createdAt,
  dbTerritory_0.updatedAt,
  assignedTo_3.name AS assignedToName,
  assignedTo_3.surname AS assignedToSurname,
  toTenant_1.name AS siteName,
  toTenant_1.description AS siteDescription,
  toTenant_1.ID AS siteId,
  toTerritoryAssignment_2.unregisteredUser AS assignedUnregisteredUser,
  toTerritoryAssignment_2.unregisteredUserEmail AS assignedUnregisteredUserEmail,
  toTerritoryAssignment_2.unregisteredUserAssignmentId AS assignedUnregisteredUserAssignmentId,
  toTerritoryAssignment_2.ID AS toTerritoryAssignmentId
FROM (((db_Territories AS dbTerritory_0 LEFT JOIN db_Tenants AS toTenant_1 ON toTenant_1.ID = dbTerritory_0.tenantDiscriminator) LEFT JOIN srv_searching_TerritoryAssignments AS toTerritoryAssignment_2 ON (toTerritoryAssignment_2.toTerritory_ID = dbTerritory_0.ID)) LEFT JOIN db_Users AS assignedTo_3 ON toTerritoryAssignment_2.assignedTo_ID = assignedTo_3.ID); 

