-- from <entando.version>4.3.2</entando.version>

-- ************* To 5.0.0 *************

ALTER TABLE pages_metadata_draft ADD COLUMN groupcode character varying(30);
ALTER TABLE pages_metadata_online ADD COLUMN groupcode character varying(30);
UPDATE pages_metadata_online SET groupcode = (SELECT pages.groupcode FROM pages WHERE pages_metadata_online.code = pages.code);
UPDATE pages_metadata_draft SET groupcode = (SELECT pages.groupcode FROM pages WHERE pages_metadata_draft.code = pages.code);
ALTER TABLE pages DROP groupcode;

INSERT INTO sysconfig (version, item, descr, config) VALUES ('production', 'jacms_resourceMetadataMapping', 'Mapping between resource Metadata and resource attribute fields', '<mapping>
    <field key="alt"></field>
    <field key="description"></field>
    <field key="legend"></field>
    <field key="title"></field>
</mapping>');

-- ************* To 5.3.2 *************
ALTER TABLE resources ADD COLUMN owner character varying(128);
ALTER TABLE contents ADD COLUMN sync smallint; -- postgres
UPDATE contents SET sync = 1 WHERE workxml = onlinexml;
UPDATE contents SET sync = 0 WHERE workxml <> onlinexml AND onlinexml is not null;
UPDATE contents SET sync = 0 WHERE onlinexml is null;

ALTER TABLE contents ADD COLUMN published character varying(20); -- postgres
UPDATE contents SET published = (xpath('./lastModified/text()', onlinexml::xml))[1] WHERE onlinexml is not null;

CREATE INDEX contents_sync_idx ON contents (sync);

-- ************* To 6.2.0 *************
ALTER TABLE contents ADD COLUMN restriction character varying(40);
ALTER TABLE resources ADD COLUMN folderpath character varying(256);
ALTER TABLE widgetcatalog ADD COLUMN bundleid character varying(150);
ALTER TABLE widgetcatalog ADD COLUMN configui character varying;


CREATE TABLE jpseo_friendlycode (
    friendlycode character varying(256) NOT NULL,
    pagecode character varying(30),
    contentid character varying(16),
    langcode character varying(2),
    CONSTRAINT jpseo_friendlycode_pkey PRIMARY KEY (friendlycode)
);

CREATE TABLE jpversioning_trashedresources (
    resid character varying(16) NOT NULL,
    restype character varying(30) NOT NULL,
    descr character varying(100) NOT NULL,
    maingroup character varying(20) NOT NULL,
    resxml text NOT NULL,
    CONSTRAINT jpversioning_trashedresources_pkey PRIMARY KEY (resid)
);

CREATE TABLE jpversioning_versionedcontents (
    id integer NOT NULL,
    contentid character varying(16) NOT NULL,
    contenttype character varying(30) NOT NULL,
    descr character varying(255) NOT NULL,
    status character varying(12) NOT NULL,
    contentxml text NOT NULL,
    versiondate timestamp without time zone NOT NULL,
    versioncode character varying(7) NOT NULL,
    onlineversion integer NOT NULL,
    approved smallint NOT NULL,
    username character varying(40),
    CONSTRAINT jpversioning_versionedcontents_pkey PRIMARY KEY (id),
    CONSTRAINT jpvers_contentvers_key UNIQUE (contentid, versioncode)
);

INSERT INTO sysconfig (version, item, descr, config) VALUES ('production', 'jpmail_config', 'Configurazione del servizio di invio eMail', '<?xml version="1.0" encoding="UTF-8"?>
<mailConfig>
	<senders>
		<sender code="CODE1">EMAIL1@EMAIL.COM</sender>
		<sender code="CODE2">EMAIL2@EMAIL.COM</sender>
	</senders>
	<smtp debug="false">
		<host>localhost</host>
		<port>25000</port>
		<user></user>
		<password></password>
		<security>std</security>
	</smtp>
</mailConfig>');

INSERT INTO sysconfig (version, item, descr, config) VALUES ('production', 'cthread_config', 'Configurazione thread pubblicazione/sospensione automatica', '<contentThreadconfig sitecode="A">
<!--attiva o disattiva lo scheduler.
I valori da assegnare all attributo active possono essere true o false
 se il valore settato è true , lo scheduler è attivo
 se il valore settato è false , lo scheduler non è attivo
-->
<scheduler active="true"/>
<!--categoria globale a cui verranno associati i contenuti in fase di depubblicazione o sospensione del contenuto.
In corrispondenza della voce="code" va messo il codice della categoria;
è obbligatorio mettere questa categoria in quanto verrà utilizzata per lo spostamento dei contenuti 
nell archivio online in mancanza di categorie specifiche -->    
<globalcat code="archive"/> 
<!--contenuto e modello di contenuto sostitutivo globale, verrà utilizzato nel caso in cui non vi sia specificato 
l idContentReplace e il modelIdContentReplace nel tag contentType.
    nell attributo contentId = deve essere inserito l id del contenuto sostitutivo es. ART1226933
    nell attributo modelId = deve essere inserito l id del modello del contenuto sostitutivo   es. 12
   La definizione di questo contenuto sostitutivo globale è obbligatoria
-->   
<contentReplace contentId="ART1226933" modelId="12"/> 
<contentTypes>
<!--
startAttr: quest attributo deve essere valorizzato con il nome del campo in cui sarà specificata la data di pubblicazione.(obbligatorio)
endAttr: quest attributo deve essere valorizzato con il nome del campo in cui sarà specificata la data di sospensione.(obbligatorio)
idContentReplace: quest attributo deve essere valorizzato con il nome del campo in cui sarà specificato l id del contenuto da sostituire(opzionale)
modelIdContentReplace: quest attributo deve essere valorizzato con il nome del campo in cui sarà specificato il modelid del contenuto da sostituire(opzionale)
Nel caso in cui non vengano valorizzati idContentReplace e modelIdContentReplace, verrà utilizzato il contenuto sostitutivo globale definito precedentemente

Suspend: l attributo suspend è obbligatorio ed è necessario per definire l azione da svolgere(valori possono essere true o false)
- se è settato a true,  predispone la sospensione dei contenuti per la tipologia di contenuti in cui è settato
- se è settato a false, predispone lo  spostamento nell archivio online dei contenuti della tipologia di contenuto in cui è settato 
-->
        <contentType type="NOL" startAttr="Data_inizio" endAttr="Data_fine" idContentReplace="Id_contenuto_sost" modelIdContentReplace="Model_id" suspend="true">
                <!--categorie associate al tipo di contenuto e che verranno associate ai contenuti 
                in fase di depubblicazione o sospensione 
                nell attributo code deve essere specificato il codice della categoria-->
    <!--<category code="cnol_canale7" />
    <category code="cnol_canale3" />
    <category code="cnol_canale4" />
    <category code="cnol_canale5" />
    <category code="cnol_canale2" />
    <category code="cnol_canale1" />
    <category code="cnol_canale6" />
    <category code="cnol_canale8" /> -->
        </contentType>
<contentType type="DRT" startAttr="Data_inizio" endAttr="Data_fine" suspend="true" >
         <!--categorie associate al tipo di contenuto e che verranno associate ai contenuti in fase di depubblicazione o sospensione 
                nell attributo code deve essere specificato il codice della categoria-->
    <!--<category code="pubb_drt" />
    <category code="cnol_canale8" /> -->
   </contentType>
  <contentType type="SNT" startAttr="Data_inizio" endAttr="Data_fine" suspend="true" >
        <!--non essendoci categorie associate, ai contenuti di questo tipo verrà associata la categoria globale -->
   </contentType>
</contentTypes>

<!-- i gruppi attualmente non vengono usati-->
<groups> 
          <group id="gruppoDiProvaThread" contentType="NOL" /> 
</groups>
  
<!-- utenti che riceveranno le comunicazioni sull andamento delle operazioni di pubblicazione, sospensione e spostamento -->
<users>
  <user username="admin" contentType="*" />
</users>


<!-- template della mail di comunicazione-->
<mail alsoHtml="false" senderCode="CODE1" mailAttrName="email" >    
<subject><![CDATA[Report pubblicazione/sospensione automatica]]></subject>     
 <htmlHeader><![CDATA[<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"> <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="it">  <head> </head>  <body><div style="padding:1.5em;font-family:Arial, sans serif; color: #333333;font-size:0.9em">Report  Pubblicazione/Sospensione automatica contenuti<br/><p>Contenuti pubblicati:</p>]]></htmlHeader> 
     <htmlFooter><![CDATA[<br /><br />Cordiali Saluti<br/></div></body></html>]]> </htmlFooter>       
<htmlSeparator> <![CDATA[ <br /> Contenuti sospesi: ]]> </htmlSeparator>     
 <textHeader><![CDATA[Report pubblicazione/sospensione automatica]]> </textHeader>  
<textFooter><![CDATA[   Cordiali Saluti.]]> </textFooter>      
<textSeparator><![CDATA[

Contenuti sospesi:
 ]]></textSeparator>  

<htmlSeparatorMove> <![CDATA[ <br /> Contenuti spostati in archivio online: ]]> </htmlSeparatorMove> 
 <textHeaderMove><![CDATA[Report pubblicazione/sospensione automatica]]> </textHeaderMove>  
<textFooterMove><![CDATA[   Cordiali Saluti.]]> </textFooterMove>  
<textSeparatorMove><![CDATA[

Contenuti spostati in archivio online:
 ]]></textSeparatorMove>   

</mail> </contentThreadconfig>');

-- ************* To 6.3.2 *************

CREATE TABLE userpreferences
(
    username character varying(80) NOT NULL,
    wizard smallint NOT NULL,
    loadonpageselect smallint NOT NULL,
    translationwarning smallint NOT NULL,
    defaultpageownergroup character varying(64),
    defaultpagejoingroups character varying(256),
    defaultcontentownergroup character varying(64),
    defaultcontentjoingroups character varying(256),
    defaultwidgetownergroup character varying(64),
    defaultwidgetjoingroups character varying(256),
    CONSTRAINT userpreferences_pkey PRIMARY KEY (username)
);

ALTER TABLE resources ADD COLUMN correlationcode character varying(256);

ALTER TABLE widgetcatalog ADD COLUMN readonlypagewidgetconfig smallint;
ALTER TABLE widgetcatalog ADD COLUMN widgetcategory character varying(80);
ALTER TABLE widgetcatalog ADD COLUMN icon character varying(80);

-- ************* To 7.0.0 *************
DELETE FROM sysconfig WHERE item = 'dataobjectsubdir';
DELETE FROM sysconfig WHERE item = 'dataTypeDefinitions';
ALTER TABLE guifragment ALTER COLUMN locked DROP NOT NULL;
ALTER TABLE widgetcatalog ALTER COLUMN locked DROP NOT NULL;
ALTER TABLE pages ALTER COLUMN pos DROP NOT NULL;
ALTER TABLE localstrings ALTER COLUMN stringvalue DROP NOT NULL;

ALTER TABLE widgetconfig ALTER COLUMN framepos TYPE smallint;
ALTER TABLE widgetconfig_draft ALTER COLUMN framepos TYPE smallint;

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
  ('00000000000001_schemaPort','entando','liquibase/port/00000000000001_schemaPort.xml','2022-03-03 12:12:32.446304',1,'EXECUTED','8:ca0f8957e59d0a535f286d630767e814','createTable tableName=sysconfig; createTable tableName=categories; createTable tableName=localstrings; createTable tableName=pagemodels; createTable tableName=pages; createTable tableName=pages_metadata_online; addForeignKeyConstraint baseTableNam...','',NULL,'4.4.3',NULL,NULL,'6305951931'),
  ('00000000000001_dataPort_production','entando','liquibase/port/00000000000001_dataPort_production.xml','2022-03-03 12:12:32.72679',2,'EXECUTED','8:f1d2f81737cddd29dfa883fdc990faa0','insert tableName=categories; insert tableName=localstrings; insert tableName=localstrings; insert tableName=localstrings; insert tableName=localstrings; insert tableName=localstrings; insert tableName=localstrings; insert tableName=localstrings; i...','',NULL,'4.4.3','production',NULL,'6305951931'),
  ('00000000000001_de_defaultResources_dataPort_production','entando','liquibase/defaultResources/port/00000000000001_dataPort_production.xml','2022-03-03 12:12:35.532485',6,'EXECUTED','8:7de85f47914acfe5b22c8c7db3228f09','insert tableName=widgetcatalog; insert tableName=guifragment; insert tableName=localstrings; insert tableName=localstrings; insert tableName=localstrings; insert tableName=localstrings; insert tableName=localstrings; insert tableName=localstrings;...','',NULL,'4.4.3','production',NULL,'6305955238');         

ALTER TABLE contentattributeroles ALTER COLUMN attrname DROP NOT NULL;
ALTER TABLE contentattributeroles ALTER COLUMN rolename DROP NOT NULL;
ALTER TABLE contentsearch ALTER COLUMN attrname DROP NOT NULL;
ALTER TABLE workcontentattributeroles ALTER COLUMN attrname DROP NOT NULL;
ALTER TABLE workcontentattributeroles ALTER COLUMN rolename DROP NOT NULL;
ALTER TABLE workcontentsearch ALTER COLUMN attrname DROP NOT NULL;
ALTER TABLE resourcerelations ALTER COLUMN refcategory DROP NOT NULL;

ALTER TABLE workcontentattributeroles ALTER COLUMN rolename TYPE varchar(30);

INSERT INTO databasechangelog (id, author, filename, dateexecuted, orderexecuted, exectype, md5sum, description, comments, tag, liquibase, contexts, labels, deployment_id) VALUES
  ('00000000000001_jacms_schemaPort','entando','liquibase/jacms/port/00000000000001_schemaPort.xml','2022-03-03 12:12:34.162904',3,'EXECUTED','8:4b138e1a635c359489a8b4dbeae439cb','createTable tableName=contentmodels; createTable tableName=contents; createIndex indexName=contents_contenttype_idx, tableName=contents; createIndex indexName=contents_lastmodified_idx, tableName=contents; createIndex indexName=contents_maingroup_...','',NULL,'4.4.3',NULL,NULL,'6305953882'),
  ('00000000000001_jacms_dataPort_production','entando','liquibase/jacms/port/00000000000001_dataPort_production.xml','2022-03-03 12:12:34.262709',4,'EXECUTED','8:563907e57e3ac8a1e6e8f1dc4a7467d0','insert tableName=sysconfig; insert tableName=sysconfig; insert tableName=sysconfig; insert tableName=sysconfig; insert tableName=localstrings; insert tableName=localstrings; insert tableName=localstrings; insert tableName=localstrings; insert tabl...','',NULL,'4.4.3','production',NULL,'6305953882');

ALTER TABLE jpversioning_versionedcontents ALTER COLUMN descr TYPE varchar(256);

INSERT INTO databasechangelog (id, author, filename, dateexecuted, orderexecuted, exectype, md5sum, description, comments, tag, liquibase, contexts, labels, deployment_id) VALUES
  ('00000000000001_jpversioning_schemaPort','entando','liquibase/jpversioning/port/00000000000001_schemaPort.xml','2022-03-03 12:12:35.877767',7,'EXECUTED','8:fa3f7bbe1e84964a52b5d49609df135b','createTable tableName=jpversioning_trashedresources; createTable tableName=jpversioning_versionedcontents; addUniqueConstraint constraintName=jpvers_contentvers_key, tableName=jpversioning_versionedcontents','',NULL,'4.4.3',NULL,NULL,'6305955828');

INSERT INTO databasechangelog (id, author, filename, dateexecuted, orderexecuted, exectype, md5sum, description, comments, tag, liquibase, contexts, labels, deployment_id) VALUES
  ('00000000000001_jpmail_dataPort_production','entando','liquibase/jpmail/port/00000000000001_dataPort_production.xml','2022-03-03 12:12:34.621258',5,'EXECUTED','8:a7e88f5e191c357dd3e503963b0730ba','insert tableName=sysconfig','',NULL,'4.4.3','production',NULL,'6305954611');

INSERT INTO databasechangelog (id, author, filename, dateexecuted, orderexecuted, exectype, md5sum, description, comments, tag, liquibase, contexts, labels, deployment_id) VALUES
  ('00000000000001_jpseo_schemaPort','entando','liquibase/jpseo/port/00000000000001_schemaPort.xml','2022-03-03 12:12:36.252658',8,'EXECUTED','8:0cac575831787759faf345608323ed1f','createTable tableName=jpseo_friendlycode','',NULL,'4.4.3',NULL,NULL,'6305956213'),
  ('00000000000001_jpseo_dataPort_production','entando','liquibase/jpseo/port/00000000000001_dataPort_production.xml','2022-03-03 12:12:36.280257',9,'EXECUTED','8:283a24fe0ff8c95c4806a1fc9ecc5fa9','insert tableName=widgetcatalog; insert tableName=guifragment; insert tableName=guifragment','',NULL,'4.4.3','production',NULL,'6305956213');

INSERT INTO databasechangelog (id, author, filename, dateexecuted, orderexecuted, exectype, md5sum, description, comments, tag, liquibase, contexts, labels, deployment_id)
  VALUES('00000000000001_jpcontentscheduler_dataPort_production', 'entando', 'liquibase/jpcontentscheduler/port/00000000000001_dataPort_production.xml', '2022-03-03 15:44:33.948', 8, 'EXECUTED', '8:ea617c252a9fd7ef7158ce075325969b', 'insert tableName=sysconfig', '', NULL, '4.4.3', 'production', NULL, '6318673902');
