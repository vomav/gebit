insert into db_Users(ID,email,name,surname,currentTenant_ID,password,oid)
values
('e3886d83-f2c4-4ed6-8fe8-49c9d70a5c19', 'vova@vova.com', 'Vova', 'Voytovych', '4d13ecb3-d7e7-483b-b982-d9ec6f701ca3','$2a$10$KLEGs3TMa2U38U3l9rLc9O3TbjjECeRThCULoTmzG22XJt488R/K.','gebit.org'),
('25525452-4197-4bdc-b254-847ac2ae1cdf', 'sa', 'sa', 'sa', '4d13ecb3-d7e7-483b-b982-d9ec6f701ca3','$2a$10$KLEGs3TMa2U38U3l9rLc9O3TbjjECeRThCULoTmzG22XJt488R/K.','gebit.org');


insert into db_Tenants(ID,createdBy_ID, name, description)
values
('4d13ecb3-d7e7-483b-b982-d9ec6f701ca3','e3886d83-f2c4-4ed6-8fe8-49c9d70a5c19', 'name 1', 'desc 1');

insert into db_UserTenantMappings (user_ID, tenant_ID, mappingType)
values
('e3886d83-f2c4-4ed6-8fe8-49c9d70a5c19','4d13ecb3-d7e7-483b-b982-d9ec6f701ca3','user'),
('25525452-4197-4bdc-b254-847ac2ae1cdf','4d13ecb3-d7e7-483b-b982-d9ec6f701ca3','admin');