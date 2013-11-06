hibernate {
	cache.use_second_level_cache=true
	cache.use_query_cache=true
	cache.provider_class='org.hibernate.cache.EhCacheProvider'
	show_sql = false
	hibernate.format_sql = true
}

dataLoad{
	//createDbPath = "F:\\MSSQL\\Data" //for dbs like MsSql this is the directory the database will create the db files in
	schemaFiles = "file:db/schema/*.xml" //the tables/indexes and foreign keys ddl
	sqlFiles = "file:db/sqlscripts/mysql/*.sql" //this goes to ant so it just needs a directory
	seedFiles = "file:db/data/base/*.xml" //the base data the should go into the core database after the schema
	loadType = "drop-create" //create,drop-create, insert, insert-fresh
	platform="MySQL" //MySQL,MsSql platform will be autodetected if it can be but its better to specify it if you can. see http://db.apache.org/ddlutils/ for list
    createUrl = "jdbc:mysql://127.0.0.1/" //the base url to use to try and create the dabase
}

dataLoadMsSql{
    createDbPath = "F:\\MSSQL\\Data" //for dbs like MsSql this is the directory the database will create the db files in
    schemaFiles = "file:db/schema/*.xml" //the tables/indexes and foreign keys ddl
    sqlFiles = "file:db/sqlscripts/mssql/*.sql" //this goes to ant so it just needs a directory
    seedFiles = "file:db/data/base/*.xml" //the base data the should go into the core database after the schema
    loadType = "drop-create" //create,drop-create, insert, insert-fresh
    platform="MsSql" //MySQL,MsSql platform will be autodetected if it can be but its better to specify it if you can. see http://db.apache.org/ddlutils/ for list
    createUrl = "jdbc:sqlserver://127.0.0.1/" //the base url to use to try and create the dabase
}

dataLoadOracle{
    //createDbPath = "F:\\MSSQL\\Data" //for dbs like MsSql this is the directory the database will create the db files in
    schemaFiles = "file:db/schema/*.xml" //the tables/indexes and foreign keys ddl
    sqlFiles = "file:db/sqlscripts/oracle/*.sql" //this goes to ant so it just needs a directory
    seedFiles = "file:db/data/base/*.xml" //the base data the should go into the core database after the schema
    loadType = "drop-create" //create,drop-create, insert, insert-fresh
    platform="Oracle" //MySQL,MsSql platform will be autodetected if it can be but its better to specify it if you can. see http://db.apache.org/ddlutils/ for list
    createUrl = "jdbc:oracle:thin://127.0.0.1/" //the base url to use to try and create the dabase
}




dataSource {
	pooled = false
	driverClassName = "com.mysql.jdbc.Driver"
	dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
    username =	"root"
    password = "xxx"
}

dataSourceMsSql {
    pooled = false
    driverClassName = "com.microsoft.jdbc.sqlserver.SQLServerDriver"
    dialect = "org.hibernate.dialect.SQLServerDialect"
    username =	"sa"
    password = "xxx"
}

dataSourceOracle {
    pooled = false
    driverClassName = "oracle.jdbc.driver.OracleDriver"
    dialect = "org.hibernate.dialect.OracleDialect"
    username =	"root"
    password = "xxx"
}

environments {

	development {
		dataLoad {
			createUrl = "jdbc:mysql://127.0.0.1/" //the base url to use to try and create the dabase
			createDbName = "dbstufftest" //name of the database to create
			dataFiles = "file:db/data/demo/*.xml" //the data files for test or a demo database
			dataFilesExtra = "file:db/data/demoExtra/*.xml" //the data files for test or a demo database
		}
		dataSource {
			url = "jdbc:mysql://127.0.0.1/dbstufftest"
			username =	"root"
			password = "xx"
		}
	}

	test {
		dataLoad{
			createUrl = "jdbc:mysql://127.0.0.1/" //the base url to use to try and create the dabase
			createDbName = "gbtest" //name of the database to create
			dataFiles = "file:db/data/test/*.xml" //the data files for test or a demo database
		}
		dataSource {
			url = "jdbc:mysql://127.0.0.1/dbstufftest"
			username =	"root"
			password = "xxx"
		}
	}

	production {
		dataLoad {
			createUrl = "jdbc:mysql://127.0.0.1/" //the base url to use to try and create the dabase
			createDbName = "greenbill" //name of the database to create
			dataFiles = "file:db/data/demo/*.xml" //the data files for test or a demo database
		}
		dataSource {
			url = "jdbc:mysql://127.0.0.1/dbstufftest"
			username =	"root"
			password = "xxx"
		}
	}

}
