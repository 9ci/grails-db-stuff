

Summary
=======
Description
===========

DB schema managment and data import/export. Generate generic xml schema files and import or export base/seed/test data into your database in the xml format dbunit uses.
Based on DdlUtils from the Apache DB Project http://db.apache.org/ddlutils/ Keeps the schema in a generic xml file that can then be used to creates the db schema in any of the supported databases. Data can also be extrernalized in files and then loaded via a grails script or at application startup.

Load base/seed data into you database. Store/export the data in xml and this will load the database with your applications base records

This can and is used for integration testing.

This is also used for populating a database with its necessary records to do an initial install.

We use it to load tables like configuration params, ACL, acegi requestmaps etc.. We also use it for complete database dumps and restores

NOTE: Currently this has only ben tested to work with MYSQL and SQL SERVER. DDL Utils works with Hsqldb,MsSql,MySql,Oracle,db2 and others but it may take some testing to get those to work.

 *These commands make it easy to drop and/or overwrite your data. This can cause brain damage, sever bouts of anger and could lead to a divorce if not used carefully.*

Usage
=====

### Install

```
grails install-plugin db-stuff
```

### Setting up DataSource.groovy
example:

```Groovy
dataLoad{
   createUrl = "jdbc:mysql://localhost/"
   createDbName = "greenbill" //name of the database to create
   createDbPath = "F:\\MSSQL\\Data"" //for dbs like MsSql need path
   schemaFiles = "file:./sql/schema/*.xml" //the ddlutils files

   //the base data the should go into the core database after the schema
   seedFiles = "file:./sql/data/base/*.xml"
   //the data files for test or a demo database
   dataFiles = "file:./sql/data/demo/*.xml"
   //the .sql files can be any valid sql statments.
   //multiple statments in a single file should end with ;
   sqlFiles = "file:./sql/scripts/mysql/*.sql"
  //MySQL,MsSql platform will try to be autodetected if it can be
   //but its better to specify it if you can. see http://db.apache.org/ddlutils/ for list
   platform="MySQL"
}
```

### create-db

```
grails create-db clean
```
### clean will try and drop the database first
1. this will try and create the db if it can using the createUrl and createDbName. only supported for hsql,mysq and sql server right now
2. runs the schemaFiles to generate the tables, indexes and foreign keys
3. populates the db with seedFiles data from the xml format like dbunits
4. populates from dataFiles
5. runs the sqlFiles scripts

```
grails create-db
```
will assume the database already exists and just run the schema and data files

### export ddl schema

```
grails export-ddl
```
sends the schema and ddl info to the sql/schema/out directory

### export data

```
grails export-data
or
grails export-data tablename directory
```
sends the data to sql/data/out directory

data examples
-------------

#### schema files
There is good documentation on this on the DDLUtils site. http://db.apache.org/ddlutils/

```XML
<?xml version='1.0' encoding='UTF-8'?>
<?xml version='1.0' encoding='UTF-8'?>
<database>
  <table name="Books" description="You description here">
    <column name="Id" primaryKey="true" required="true" type="INTEGER" size="10" />
    <column name="Version" type="INTEGER" size="10" default="0" />
    <column name="Name" required="true" type="VARCHAR" size="50" />
    <column name="Description" type="VARCHAR" size="255" />
    <column name="Price" type="NUMERIC" size="21,6" />
    <column name="EditedDate" type="TIMESTAMP" />
  </table>
</database>
```
### Data files
The format is the same as DBUnit

```XML
<dataset>
  <Books Id="1" Name="Zen and the Art of Motorcycle Maintenance" Description="Are you a rationalist?"
	Price="11.95" EditedDate="2009-02-04 10:06:59.563" />
  <Books Id="2" Name="War and Peace" Price="11.95" />
</dataset>
```