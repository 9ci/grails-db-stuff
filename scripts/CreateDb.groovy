import groovy.sql.Sql
import java.sql.SQLException
//import greenbill.dataloader.DataLoader
import org.springframework.jdbc.datasource.DriverManagerDataSource

includeTargets << grailsScript("Bootstrap")
//includeTargets << new File("/path/to/my/script.groovy")
def dsFile = new File("${basedir}/grails-app/conf/DataSource.groovy")
dsConfig = new ConfigSlurper(grailsEnv).parse(dsFile.text)


target(createDb: "Load the data from the specified directory into the database") {
	depends(parseArguments,packageApp,loadApp)
	
	def createdrop = argsMap.params? argsMap.params[0] : "create"
	
	def dbc = grailsApp.classLoader.loadClass("greenbill.dbstuff.DbCreate").newInstance()
	dbc.dataSource=getCreateDataSource() 
	if(createdrop == "clean") {
		println "about to drop and recreate the database for ${dsConfig.dataLoad.createDbName}"
		dbc.dropAndCreate(dsConfig.dataLoad.createDbName,dsConfig.dataLoad.createDbPath)
	}else if (createdrop == "create") {
		println "attempting to create the database for ${dsConfig.dataLoad.createDbName}"
		dbc.create(dsConfig.dataLoad.createDbName,dsConfig.dataLoad.createDbPath)
	}
	//load the schema
	def dl = grailsApp.classLoader.loadClass("greenbill.dbstuff.DataLoader").newInstance()
	dl.dataSource=getDataSource()
	println "creating tables from schema files ${dsConfig.dataLoad.schemaFiles}" 
	dl.loadSchema(dsConfig.dataLoad.schemaFiles,true)
	
	println "creating the base seed data from files ${dsConfig.dataLoad.seedFiles}" 
	dl.load(dsConfig.dataLoad.seedFiles,null)
	
	
	println "loading the data files from files ${dsConfig.dataLoad.dataFiles}" 
	dl.load(dsConfig.dataLoad.dataFiles,null)
	
	println "running the scripts in ${dsConfig.dataLoad.sqlFiles}" 
	ant.sql(print:true, autocommit:true, keepformat:true,delimitertype:"row",
			driver:"${dsConfig.dataSource.driverClassName}",
			url:"${dsConfig.dataSource.url}", userid:"${dsConfig.dataSource.username}", password:"${dsConfig?.dataSource?.password }"){
				
	    fileset(dir: "${basedir}", includes: "${dsConfig.dataLoad.sqlFiles}")
	}
	
}

setDefaultTarget(createDb)

def getCreateDataSource(){

    def username = dsConfig?.dataSource?.username ?: 'sa'
    def password = dsConfig?.dataSource?.password ?: ''
    def url = dsConfig?.dataLoad?.createUrl ?: 'jdbc:hsqldb:mem:testDB'
    def driverClassName = dsConfig?.dataSource?.driverClassName ?: 'org.hsqldb.jdbcDriver'
	def ds = new DriverManagerDataSource(driverClassName,url,username,password)
	return ds
}

def getDataSource(){
    def username = dsConfig?.dataSource?.username ?: 'sa'
    def password = dsConfig?.dataSource?.password ?: ''
    def url = dsConfig?.dataSource?.url ?: 'jdbc:hsqldb:mem:testDB'
    def driverClassName = dsConfig?.dataSource?.driverClassName ?: 'org.hsqldb.jdbcDriver'
	def ds = new DriverManagerDataSource(driverClassName,url,username,password)
	return ds
}

