
DROP VIEW srv_searching_Territories;
DROP VIEW srv_admin_LoggedInUser;
DROP VIEW srv_admin_Tenants;
DROP VIEW srv_searching_Images;
DROP VIEW srv_searching_InWorkBy;
DROP VIEW srv_searching_Tenants;
DROP VIEW srv_searching_PartAssignments;
DROP VIEW srv_searching_PublicTerritoryAssignments;
DROP VIEW srv_searching_TerritoryAssignments;
DROP VIEW srv_searching_Parts;

COMMENT ON TABLE db_Territories IS NULL;


COMMENT ON TABLE db_Parts IS NULL;


COMMENT ON TABLE db_TerritoryAssignments IS NULL;


COMMENT ON TABLE db_PartAssignments IS NULL;


COMMENT ON TABLE db_Image IS NULL;


ALTER TABLE db_InWorkBy ADD freestyleName VARCHAR(128);


COMMENT ON TABLE db_InWorkBy IS NULL;


COMMENT ON TABLE db_Users IS NULL;


COMMENT ON TABLE db_UserAccountActivations IS NULL;


COMMENT ON TABLE db_Tenants IS NULL;


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
  toTerritory_1.name AS name,
  toTerritory_1.link AS link
FROM (db_TerritoryAssignments AS dbTerritoryAssignment_0 LEFT JOIN db_Territories AS toTerritory_1 ON dbTerritoryAssignment_0.toTerritory_ID = toTerritory_1.ID);


CREATE VIEW srv_searching_PublicTerritoryAssignments AS SELECT
  dbTerritoryAssignment_0.ID,
  dbTerritoryAssignment_0.tenantDiscriminator,
  dbTerritoryAssignment_0.toTerritory_ID,
  dbTerritoryAssignment_0.isDone,
  dbTerritoryAssignment_0.startedDate,
  dbTerritoryAssignment_0.finishedDate,
  dbTerritoryAssignment_0.type,
  dbTerritoryAssignment_0.assignedTo_ID,
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
  toTenant_1.ID AS siteId
FROM (((db_Territories AS dbTerritory_0 LEFT JOIN db_Tenants AS toTenant_1 ON toTenant_1.ID = dbTerritory_0.tenantDiscriminator) LEFT JOIN srv_searching_TerritoryAssignments AS toTerritoryAssignment_2 ON (toTerritoryAssignment_2.toTerritory_ID = dbTerritory_0.ID)) LEFT JOIN db_Users AS assignedTo_3 ON toTerritoryAssignment_2.assignedTo_ID = assignedTo_3.ID);

