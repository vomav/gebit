namespace srv;

using { db.Territory, db.Part, db.TerritoryInProggress, db.PartInProgress, db.User } from '../db/database';

service searching {

    entity Territories as projection on Territory;
    entity Parts as projection on Part;
    entity TerritoriesInProggress as projection on TerritoryInProggress;
    entity PartsInProgress as projection on PartInProgress;
    entity Users as projection on User {
        *
    } excluding {
        password
    };

    action register()

}