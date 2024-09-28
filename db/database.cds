namespace db;

using { cuid } from '@sap/cds/common';

aspect tenant {
    tenant: String(32);
}

entity Territory : cuid, tenant {
    name: String(64);
    link: String(2048);
    parts : Composition of many Part on parts.toParent=$self;
    lastTimeWorked: Date;
}

entity Part : cuid, tenant {
    name: String(64);
    coordinates: array of Point;
    toParent: Association to one Territory;
}

type Point : {
    lattitude: Double;
    longitude: Double;
}

entity TerritoryInProggress : cuid, tenant {
    toTerritory: Association to one Territory;
    done: Boolean;
    inProgress: Composition of many PartInProgress on inProgress.toParent = $self;
    started:Date;
    finished:Date;
    type:String(32) enum {
        Personal = 'Personal';
        Public = 'Public';
    }
}

entity PartInProgress : cuid, tenant {
    part: Association to one Part;
    inWorkBy: String(128);
    done: Boolean;
    toParent: Association to TerritoryInProggress;
}

entity User : cuid {
    userId: String(64);
    name: String(128);
    surname: String(128);
    currentTenant: String(32);
    allowedTenants: array of String;
    password: String(128);

}
