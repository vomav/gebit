namespace srv;

using { db.Territory as Territory, db.Part as Part, db.PreachingInProggress as PreachingInProggress, db.PartInProgress as PartInProgress } from '../db/database';

service preaching {

    entity Territories as projection on Territory;
    entity Parts as projection on Part;
    entity PreachingsInProggress as projection on PreachingInProggress;
    entity PartsInProgress as projection on PartInProgress;

}