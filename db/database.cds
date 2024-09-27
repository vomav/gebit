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

entity PreachingInProggress : cuid, tenant {
    toTerritory: Association to one Territory;
    done: Boolean;
    inProgress: Composition of many PartInProgress on inProgress.toParent = $self;
    started:Date;
    finished:Date;
}

entity PartInProgress : cuid, tenant {
    part: Association to one Part;
    inWorkBy: String(128);
    done: Boolean;
    toParent: Association to PreachingInProggress;
}

entity UserTenant {
    userId: String(64);
    currentTenant: String(32);
    alloweTenant: array of String;
}
