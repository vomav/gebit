
DROP VIEW srv_searching_Territories;
DROP VIEW srv_publicSearching_Image;
DROP VIEW srv_publicSearching_Parts;
DROP VIEW srv_publicSearching_PartAssignments;
DROP VIEW srv_admin_LoggedInUser;
DROP VIEW srv_searching_Images;
DROP VIEW srv_searching_PartAssignments;
DROP VIEW srv_searching_PublicTerritoryAssignments;
DROP VIEW srv_searching_TerritoryAssignments;
DROP VIEW srv_searching_Parts;

CREATE VIEW srv_searching_Parts AS SELECT
  dbPart_0.ID,
  dbPart_0.tenantDiscriminator,
  dbPart_0.name,
  dbPart_0.isBoundaries,
  dbPart_0.coordinates,
  dbPart_0.count,
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
  dbTerritoryAssignment_0.totalCount,
  dbTerritoryAssignment_0.assignedTo_ID,
  dbTerritoryAssignment_0.assignedToUnregisteredUser,
  toTerritory_1.name AS name,
  toTerritory_1.link AS link,
  toUnregisetredUserTerritoryAssignments_2.unregisteredUser AS unregisteredUser,
  toUnregisetredUserTerritoryAssignments_2.unregisteredUserEmail AS unregisteredUserEmail,
  toUnregisetredUserTerritoryAssignments_2.ID AS unregisteredUserAssignmentId,
  toTerritory_1.totalCount AS prevTotalCount
FROM ((db_TerritoryAssignments AS dbTerritoryAssignment_0 LEFT JOIN db_Territories AS toTerritory_1 ON dbTerritoryAssignment_0.toTerritory_ID = toTerritory_1.ID) LEFT JOIN db_UnregisteredUserTerritoryAssignment AS toUnregisetredUserTerritoryAssignments_2 ON (toUnregisetredUserTerritoryAssignments_2.toTerritoryAssignment_ID = dbTerritoryAssignment_0.ID));


CREATE VIEW srv_searching_PublicTerritoryAssignments AS SELECT
  dbTerritoryAssignment_0.ID,
  dbTerritoryAssignment_0.tenantDiscriminator,
  dbTerritoryAssignment_0.toTerritory_ID,
  dbTerritoryAssignment_0.isDone,
  dbTerritoryAssignment_0.startedDate,
  dbTerritoryAssignment_0.finishedDate,
  dbTerritoryAssignment_0.type,
  dbTerritoryAssignment_0.totalCount,
  dbTerritoryAssignment_0.assignedTo_ID,
  dbTerritoryAssignment_0.assignedToUnregisteredUser,
  toTerritory_1.name AS name,
  toTerritory_1.link AS link,
  toTerritory_1.totalCount AS prevTotalCount
FROM (db_TerritoryAssignments AS dbTerritoryAssignment_0 LEFT JOIN db_Territories AS toTerritory_1 ON dbTerritoryAssignment_0.toTerritory_ID = toTerritory_1.ID)
WHERE dbTerritoryAssignment_0.type = 'Public' AND toTerritory_1.isReady = TRUE;


CREATE VIEW srv_searching_PartAssignments AS SELECT
  dbPartAssignmenst_0.ID,
  dbPartAssignmenst_0.tenantDiscriminator,
  dbPartAssignmenst_0.part_ID,
  dbPartAssignmenst_0.isDone,
  dbPartAssignmenst_0.imageUrl,
  dbPartAssignmenst_0.count,
  dbPartAssignmenst_0.toParent_ID,
  part_1.name AS name,
  part_1.coordinates AS coordinates,
  part_1.isBoundaries AS isBoundaries,
  toWorkedPartImage_2.imageUrl AS workedPartImageUrl,
  toWorkedPartImage_2.mediaType AS workedPartImageMediaType,
  part_1.count AS prevCount
FROM ((db_PartAssignments AS dbPartAssignmenst_0 LEFT JOIN db_Parts AS part_1 ON dbPartAssignmenst_0.part_ID = part_1.ID) LEFT JOIN db_Image AS toWorkedPartImage_2 ON (toWorkedPartImage_2.toParent_ID = dbPartAssignmenst_0.ID));


CREATE VIEW srv_searching_Images AS SELECT
  Image_0.ID,
  Image_0.tenantDiscriminator,
  Image_0.imageUrl,
  Image_0.spacePath,
  Image_0.mediaType,
  Image_0.toParent_ID
FROM db_Image AS Image_0;


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


CREATE VIEW srv_publicSearching_PartAssignments AS SELECT
  dbPartAssignmenst_0.ID,
  dbPartAssignmenst_0.tenantDiscriminator,
  dbPartAssignmenst_0.part_ID,
  dbPartAssignmenst_0.isDone,
  dbPartAssignmenst_0.imageUrl,
  dbPartAssignmenst_0.count,
  dbPartAssignmenst_0.toParent_ID,
  part_1.name AS name,
  part_1.coordinates AS coordinates,
  part_1.isBoundaries AS isBoundaries,
  toWorkedPartImage_2.imageUrl AS workedPartImageUrl,
  toWorkedPartImage_2.mediaType AS workedPartImageMediaType
FROM ((db_PartAssignments AS dbPartAssignmenst_0 LEFT JOIN db_Parts AS part_1 ON dbPartAssignmenst_0.part_ID = part_1.ID) LEFT JOIN db_Image AS toWorkedPartImage_2 ON (toWorkedPartImage_2.toParent_ID = dbPartAssignmenst_0.ID));


CREATE VIEW srv_globalAdmin_Users AS SELECT
  dbUser_0.ID,
  dbUser_0.isActivated,
  dbUser_0.name,
  dbUser_0.surname,
  dbUser_0.email
FROM db_Users AS dbUser_0;


CREATE VIEW srv_globalAdmin_UserAccountActivations AS SELECT
  dbUserAccountActivation_0.ID,
  dbUserAccountActivation_0.activationCode,
  toUser_1.email AS email,
  toUser_1.ID AS userId,
  dbUserAccountActivation_0.toUser_ID AS toUser_ID
FROM (db_UserAccountActivations AS dbUserAccountActivation_0 LEFT JOIN db_Users AS toUser_1 ON dbUserAccountActivation_0.toUser_ID = toUser_1.ID);


CREATE VIEW srv_globalAdmin_UserTenantMappings AS SELECT
  user_1.ID,
  user_1.name AS username,
  user_1.surname AS surname,
  user_1.email AS email,
  tenant_2.name AS siteName,
  tenant_2.ID AS siteId,
  tenant_2.description AS siteDescription,
  dbUserTenantMapping_0.user_ID AS toUser_ID,
  dbUserTenantMapping_0.tenant_ID AS toTenant_ID,
  dbUserTenantMapping_0.mappingType
FROM ((db_UserTenantMappings AS dbUserTenantMapping_0 LEFT JOIN db_Users AS user_1 ON dbUserTenantMapping_0.user_ID = user_1.ID) LEFT JOIN db_Tenants AS tenant_2 ON dbUserTenantMapping_0.tenant_ID = tenant_2.ID);


CREATE VIEW srv_publicSearching_Parts AS SELECT
  Parts_0.ID,
  Parts_0.tenantDiscriminator,
  Parts_0.name,
  Parts_0.isBoundaries,
  Parts_0.coordinates,
  Parts_0.count,
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
  dbTerritory_0.totalCount,
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

