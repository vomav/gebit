
DROP VIEW srv_searching_Territories;
DROP VIEW srv_searching_Images;
DROP VIEW srv_searching_PartAssignments;
DROP VIEW srv_searching_PublicTerritoryAssignments;
DROP VIEW srv_searching_TerritoryAssignments;
DROP VIEW srv_searching_Parts;

ALTER TABLE db_TerritoryAssignments ADD assignedToUnregisteredUser VARCHAR(128);


CREATE TABLE db_UnregisteredUserTerritoryAssignment (
  ID VARCHAR(36) NOT NULL,
  unregisteredUser VARCHAR(128),
  unregisteredUserEmail VARCHAR(128),
  toTerritoryAssignment_ID VARCHAR(36),
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


CREATE VIEW srv_searching_Images AS SELECT
  Image_0.ID,
  Image_0.tenantDiscriminator,
  Image_0.imageUrl,
  Image_0.spacePath,
  Image_0.mediaType,
  Image_0.toParent_ID
FROM db_Image AS Image_0;


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
  toTerritoryAssignment_2.unregisteredUserAssignmentId AS assignedUnregisteredUserAssignmentId
FROM (((db_Territories AS dbTerritory_0 LEFT JOIN db_Tenants AS toTenant_1 ON toTenant_1.ID = dbTerritory_0.tenantDiscriminator) LEFT JOIN srv_searching_TerritoryAssignments AS toTerritoryAssignment_2 ON (toTerritoryAssignment_2.toTerritory_ID = dbTerritory_0.ID)) LEFT JOIN db_Users AS assignedTo_3 ON toTerritoryAssignment_2.assignedTo_ID = assignedTo_3.ID);

