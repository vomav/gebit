insert into db_User(ID,email,name,surname,currentTenant_ID,password,oid)
values
('e3886d83-f2c4-4ed6-8fe8-49c9d70a5c19', 'vova@vova.com', 'Vova', 'Voytovych', '4d13ecb3-d7e7-483b-b982-d9ec6f701ca3','password','gebit.org');


insert into db_Tenant(ID,createdBy_ID)
values
('4d13ecb3-d7e7-483b-b982-d9ec6f701ca3','e3886d83-f2c4-4ed6-8fe8-49c9d70a5c19');

insert into db_UserTenantMapping (user_ID, tenant_ID, isOwner)
values
('e3886d83-f2c4-4ed6-8fe8-49c9d70a5c19','4d13ecb3-d7e7-483b-b982-d9ec6f701ca3',true);