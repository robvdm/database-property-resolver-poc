INSERT INTO person(id, name, birth, eyes, nickname) VALUES (nextval('hibernate_sequence'), 'Farid Ulyanov', to_date('1974-08-15', 'YYYY-MM-dd'), 'BLUE', 'dumbo1');
INSERT INTO person(id, name, birth, eyes, nickname) VALUES (nextval('hibernate_sequence'), 'Salvador L. Witcher', to_date('1984-05-24', 'YYYY-MM-dd'), 'BROWN', 'dumbo2');
INSERT INTO person(id, name, birth, eyes, nickname) VALUES (nextval('hibernate_sequence'), 'Huynh Kim Hue', to_date('1999-04-25', 'YYYY-MM-dd'), 'HAZEL', 'dumbo3');

CREATE TABLE IF NOT EXISTS properties2 (
    application varchar(255) NULL,
    "encrypted" bool NULL,
    encryption_key_name varchar(255) NULL,
    "key" varchar(255) NULL,
    nonce varchar(255) NULL,
    value varchar(255) NULL
);
INSERT INTO properties (application,"key",value,encrypted,nonce,encryption_key_name) VALUES
     (N'general',N'mq.password',N'ENC(passw0rd)',true,NULL,NULL),
     (N'general',N'mq.port',N'1414',false,NULL,NULL),
     (N'general',N'mq.queue.manager',N'QM1',false,NULL,NULL),
     (N'general',N'mq.user',N'app',false,NULL,NULL),
     (N'foobar',N'foo',N'bar',false,NULL,NULL),
     (N'foobar.not-enc-foo',N'not-enc-foo',N'bar',false,NULL,NULL),
     (N'rabobank-file-transfer-manager-API',N'rbac.password',N'ENC(secret)',true,NULL,NULL),
     (N'rabobank-file-transfer-manager-API',N'rbac.url',N'http://localhost:8080/rbac-middleware/api/v1/principals/createOrUpdate',false,NULL,NULL);
     (N'rabobank-file-transfer-manager-API',N'greeting',N'Ciao',false,NULL,NULL);
     (N'rabobank-file-transfer-manager-API',N'team',N'Giaguaro',false,NULL,NULL);
     
