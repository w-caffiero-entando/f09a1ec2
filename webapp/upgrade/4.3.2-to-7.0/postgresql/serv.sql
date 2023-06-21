-- from <entando.version>4.3.2</entando.version>

-- ************* To 5.0.0 *************
-- Nothing to do

-- ************* To 5.3.2 *************
-- Nothing to do

-- ************* To 6.2.0 *************
ALTER TABLE authusers ALTER COLUMN username TYPE character varying(80);
ALTER TABLE authusergrouprole ALTER COLUMN username TYPE character varying(80);
ALTER TABLE authuserprofileattrroles ALTER COLUMN username TYPE character varying(80);
ALTER TABLE authusershortcuts ALTER COLUMN username TYPE character varying(80);

ALTER TABLE actionlogrecords ALTER COLUMN actionname TYPE character varying(250);

ALTER TABLE api_oauth_tokens ALTER COLUMN localuser TYPE character varying(255);

ALTER TABLE actionloglikerecords ALTER COLUMN id SET DEFAULT nextval('actionloglikerecords_id_seq'::regclass);
ALTER TABLE actionlogrelations ALTER COLUMN id SET DEFAULT nextval('actionlogrelations_id_seq'::regclass);

ALTER TABLE authrolepermissions ALTER COLUMN id SET DEFAULT nextval('authrolepermissions_id_seq'::regclass);
ALTER TABLE authusergrouprole ALTER COLUMN id SET DEFAULT nextval('authusergrouprole_id_seq'::regclass);
ALTER TABLE authuserprofileattrroles ALTER COLUMN id SET DEFAULT nextval('authuserprofileattrroles_id_seq'::regclass);
ALTER TABLE authuserprofilesearch ALTER COLUMN id SET DEFAULT nextval('authuserprofilesearch_id_seq'::regclass);

-- ************* To 6.3.2 *************
-- Nothing to do

-- ************* To 7.0.0 *************

DROP TABLE dataobjectsearch;
DROP TABLE dataobjectattributeroles;
DROP TABLE dataobjectmodels;
DROP TABLE dataobjectrelations;
DROP TABLE dataobjects;

DROP SEQUENCE IF EXISTS dataobjectattributeroles_id_seq;
DROP SEQUENCE IF EXISTS dataobjectrelations_id_seq;
DROP SEQUENCE IF EXISTS dataobjectsearch_id_seq;

ALTER TABLE authuserprofileattrroles ALTER COLUMN attrname DROP NOT NULL;
ALTER TABLE authuserprofileattrroles ALTER COLUMN rolename DROP NOT NULL;
ALTER TABLE authuserprofilesearch ALTER COLUMN attrname DROP NOT NULL;

ALTER TABLE actionlogrecords ALTER COLUMN actionname TYPE varchar(40);
ALTER TABLE api_oauth_tokens ALTER COLUMN localuser TYPE varchar(255);

-- pay attention - sequence must have same owner as table it is linked to
CREATE SEQUENCE actionlogcommentrecords_id_seq OWNED BY actionlogcommentrecords.id;
ALTER TABLE actionlogcommentrecords ALTER COLUMN id SET DEFAULT nextval('actionlogcommentrecords_id_seq');

CREATE TABLE databasechangelog (
  id varchar(255) NOT NULL,
  author varchar(255) NOT NULL,
  filename varchar(255) NOT NULL,
  dateexecuted timestamp NOT NULL,
  orderexecuted int4 NOT NULL,
  exectype varchar(10) NOT NULL,
  md5sum varchar(35) NULL,
  description varchar(255) NULL,
  comments varchar(255) NULL,
  tag varchar(255) NULL,
  liquibase varchar(20) NULL,
  contexts varchar(255) NULL,
  labels varchar(255) NULL,
  deployment_id varchar(10) NULL
);
CREATE TABLE databasechangeloglock (
  id int4 NOT NULL,
  locked bool NOT NULL,
  lockgranted timestamp NULL,
  lockedby varchar(255) NULL,
  CONSTRAINT databasechangeloglock_pkey PRIMARY KEY (id)
);

INSERT INTO databasechangelog (id, author, filename, dateexecuted, orderexecuted, exectype, md5sum, description, comments, tag, liquibase, contexts, labels, deployment_id) VALUES
  ('00000000000001_schemaServ','entando','liquibase/serv/00000000000001_schemaServ.xml','2022-03-03 12:12:33.41696',1,'EXECUTED','8:34d064f27c2a4d67d3797a8c2fb5ef58','createTable tableName=authgroups; createTable tableName=authpermissions; createTable tableName=authroles; createTable tableName=authrolepermissions; addForeignKeyConstraint baseTableName=authrolepermissions, constraintName=authrolepermissions_role...','',NULL,'4.4.3',NULL,NULL,'6305953101'),
  ('00000000000001_schemaServ_consumers','entando','liquibase/serv/00000000000001_schemaServ.xml','2022-03-03 12:12:33.445449',2,'EXECUTED','8:9de859f33cc0ee804dcc703b09b03681','createTable tableName=api_oauth_consumers','',NULL,'4.4.3',NULL,NULL,'6305953101'),
  ('00000000000001_dataServ_production','entando','liquibase/serv/00000000000001_dataServ_production.xml','2022-03-03 12:12:33.492386',3,'EXECUTED','8:e9f08ba4d2b4976f2a016fab54deb591','insert tableName=authgroups; insert tableName=authgroups; insert tableName=authpermissions; insert tableName=authpermissions; insert tableName=authpermissions; insert tableName=authpermissions; insert tableName=authpermissions; insert tableName=au...','',NULL,'4.4.3','production',NULL,'6305953101');

INSERT INTO databasechangelog (id, author, filename, dateexecuted, orderexecuted, exectype, md5sum, description, comments, tag, liquibase, contexts, labels, deployment_id) VALUES
  ('00000000000001_adminconsole_schemaServ','entando','liquibase/admin-console/serv/00000000000001_schemaServ.xml','2022-03-03 12:12:34.978668',4,'EXECUTED','8:950e18117492e0486cba83d27a5856f8','createTable tableName=authusershortcuts','',NULL,'4.4.3',NULL,NULL,'6305954961'),
  ('00000000000001_adminconsole_dataServ_production','entando','liquibase/admin-console/serv/00000000000001_dataServ_production.xml','2022-03-03 12:12:34.990755',5,'EXECUTED','8:2b5e09374fa37f80c4d2c8de7e58a2f6','insert tableName=authusershortcuts','',NULL,'4.4.3','production',NULL,'6305954961');

-- ATTENTION reset local admin password
UPDATE public.authusers SET passwd = '{bcrypt}$2a$10$TMRaAmZE4w5LEeELdmpJguuSuJc2D9hUelMGmsJyK35K3PBiePqXu' WHERE username = 'admin';