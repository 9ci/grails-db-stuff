/** New DataSource.groovy instructions:
 *	QUICK START:
 *      -DBMS defaults to mysql, so you can leave that out if you like.
 *      microsoft dcsdev:        g -DBMS=ms dev create-db clean
 *      microsoft dcstest:       g -DBMS=ms test create-db clean
 *      mysql gbdev:             g -DBMS=my dev create-db clean
 *      mysql gbtest:            g -DBMS=my test create-db clean
 *      microsoft DcsDevHistory: g -Dgrails.env=hist -DB=DcsDevHistory create-db clean
 *
 *  Don't forget to run 'g dbm-update' afterward.
 *	EVERYTHING can be overridden in an external config, like dcs-config.groovy.
 */

mysqldefaults {	// -DBMS=my pulls these in:
    createDbPath    = null	// not used by mysql
    createUrl       = 'jdbc:mysql://127.0.0.1/'
    dbNamePrefix    = 'gb'
    dialect         = 'org.hibernate.dialect.MySQL5InnoDBDialect'
    driverClassName = 'com.mysql.jdbc.Driver'
    username        = 'root'
    password        = 'xxx'
    platform        = 'MySQL'
    sqlFiles        = 'file:db/sqlscripts/mysql/*.sql'
}

microsoftdefaults {	// -DBMS=ms pulls these in:
    createDbPath    = 'C:\\Program Files\\Microsoft SQL Server\\MSSQL10.SQLEXPRESS\\MSSQL\\DATA'	// where the database files are stored by mssql
    createUrl       = 'jdbc:jtds:sqlserver://192.168.1.154:1433/'
    dbNamePrefix    = 'dcs'
    dialect         = 'org.hibernate.dialect.SQLServerDialect'
    driverClassName = 'net.sourceforge.jtds.jdbc.Driver'
    username        = 'sa'
    password        = 'xxx'
    platform        = 'MsSQL'
    sqlFiles        = 'file:db/sqlscripts/sqlserver/*/*.sql'
}

oracledefaults {	// -DBMS=or pulls these in:
    createDbPath    = null
    createUrl       =  'jdbc:oracle:thin:@192.168.1.139:1521:orcl'
    dbNamePrefix    = 'nineci'
    dialect         = 'org.hibernate.dialect.Oracle10gDialect'
    driverClassName = 'oracle.jdbc.OracleDriver'
    username        = 'dbstufftest'
    password        = 'oracle'
    model           = 'gbtest'
    platform        = 'Oracle10'
    sqlFiles        = 'file:db/sqlscripts/oracle/*.sql'
}


// Pull in an explicit dbms directive:  'my' = mysql;  'ms' = microsoft

def defaults
switch(System.properties['BMS']) {
    case 'ms': defaults = microsoftdefaults;   break;
    case 'my': defaults = mysqldefaults;       break;
    case 'or': defaults = oracledefaults;      break;
    default:
        println 'No default dbms specified, going with mysql'
        defaults = mysqldefaults;
}
//def defaults = (System.properties['BMS'] == 'or' ? oracledefaults : mysqldefaults)


// Pull in an explicit database name directive:  (e.g. 'dcsdevHistory')
def database = System.properties['B']

hibernate {
    cache.provider_class         = 'org.hibernate.cache.EhCacheProvider'
    cache.use_query_cache        = true
    cache.use_second_level_cache = true
    format_sql                   = false
    naming_strategy              = 'org.hibernate.cfg.DefaultNamingStrategy'
    show_sql                     = false
}

dataLoad{
//	createDbName = Constructed for each target and dbms
    createDbPath = defaults.createDbPath
    createUrl    = defaults.createUrl          // The base url to use to try and create the database
//	dataFiles    = Defined in the environment  // The extra data files for test or a demo database
    loadType     = 'drop-create'               // Create,drop-create, insert, insert-fresh
    platform     = defaults.platform           // MySQL,MsSql. see http://db.apache.org/ddlutils/ for list
    schemaFiles  = 'file:db/schema/*.xml'      // The tables/indexes and foreign keys ddl
    seedFiles    = 'file:db/data/base/*.xml'   // The base data the should go into the core database after the schema
    sqlFiles     = defaults.sqlFiles           // This goes to ant so it just needs a directory
}

dataSource {
    dbCreate        = 'none'
    dialect         = defaults.dialect
    driverClassName = defaults.driverClassName
    username        = defaults.username
    password        = defaults.password
    pooled          = false
}

environments {
    development {
        dataLoad.createDbName = (database?:"${defaults.dbNamePrefix}dev")
        dataLoad.dataFiles    = 'file:db/data/demo/*.xml'
        dataSource.url        = "${defaults.createUrl}${dataLoad.createDbName}"
    }

    test {
        dataLoad.createDbName = (database?:"${defaults.dbNamePrefix}test")
        dataLoad.dataFiles    = 'file:db/data/test/*.xml'
       // dataSource.url        = "${defaults.createUrl}${dataLoad.createDbName}"
        dataSource.url        = defaults.platform == 'Oracle10' ? "${defaults.createUrl}" : "${defaults.createUrl}${dataLoad.createDbName}"
    }

    oracleDevelopment {
        dataLoad.createDbName = (database?:"${defaults.dbNamePrefix}dev")
        dataLoad.dataFiles    = 'file:db/data/demo/*.xml'
        dataSource.url        = "jdbc:oracle:thin:@localhost:1521:orcl"
    }
}
