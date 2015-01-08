/*
 *   Copyright 2008 Joshua Burnett, 9ci Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package greenbill.dbstuff

//import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.apache.ddlutils.PlatformFactory;

public class CreateDb {

	def dataSource
	def platform
	def grailsApplication
	
	def dropAndCreate(dbname,dsConfig) {
		 platform = PlatformFactory.createNewPlatformInstance(dataSource)
		def platformName = platform.name.toLowerCase();
		if (platformName.contains("mssql")){
			dropMsSql(dbname,
				dsConfig.dataSource.driverClassName,dsConfig.dataLoad.createUrl,
				dsConfig.dataSource.username,dsConfig.dataSource.password)
				
			createMsSql(dbname,dsConfig.dataLoad.createDbPath,
				dsConfig.dataSource.driverClassName,dsConfig.dataLoad.createUrl,
				dsConfig.dataSource.username,dsConfig.dataSource.password)
		}else if (platformName.contains("mysql")){
			dropMySql(dbname)
			createMySql(dbname)
		}else if (platformName.contains("hsqldb")){
			dropHsql(dbname)
        } else if (platformName.contains("oracle")){
            dropTablesOracle(dsConfig.dataSource.driverClassName,dsConfig.dataLoad.createUrl,
                    dsConfig.dataSource.username,dsConfig.dataSource.password)
		}else throw new IllegalArgumentException("Drop and Create not supported for this databse yet")
	}
	def create(dbname,dsConfig) {
		platform = PlatformFactory.createNewPlatformInstance(dataSource)
		def platformName = platform.name.toLowerCase();
		if (platformName.contains("mssql")){
			createMsSql(dbname,dsConfig.dataLoad.createDbPath,
				dsConfig.dataSource.driverClassName,dsConfig.dataLoad.createUrl,
				dsConfig.dataSource.username,dsConfig.dataSource.password)
		}else if (platformName.contains("mysql")){
			createMySql(dbname)
		}else if (platformName.contains("HsqlDb")){
		}else throw new IllegalArgumentException("Create not supported for this databse yet")
	}
	
	def dropMsSql(dbname,driverClassName,url,username,password) {
		//println "about to drop db in DbCreate"
		def sql = """
				if db_id(\'${dbname}\') is not null
				BEGIN
				Alter database ${dbname} set single_user with rollback immediate; 
				use tempdb;
				drop database ${dbname};
				END
		"""
		//println sql
        fireSqlAnt(driverClassName,url,username,password,sql)
	}
	
	def dropMySql(dbname) {
		runSql(	"""
				use mysql;
				drop database if exists ${dbname};
		""")
	}

    def dropTablesOracle(driverClassName,url,username,password) {
        def sql = """
                BEGIN
                  FOR i IN (SELECT us.sequence_name
                              FROM USER_SEQUENCES us) LOOP
                    EXECUTE IMMEDIATE 'drop sequence '|| i.sequence_name ||'';
                  END LOOP;
                  FOR i IN (SELECT ut.table_name
                              FROM USER_TABLES ut) LOOP
                    EXECUTE IMMEDIATE 'drop table '|| i.table_name ||' CASCADE CONSTRAINTS ';
                  END LOOP;
                  FOR i IN (SELECT uv.view_name
                              FROM USER_VIEWS uv) LOOP
                    EXECUTE IMMEDIATE 'drop view '|| i.view_name ||' CASCADE CONSTRAINTS ';
                  END LOOP;
                END;
        """
        fireSqlAnt(driverClassName,url,username,password,sql)
    }

	def dropHsql(dbname) {
		if(!grailsApplication.config.dataLoad.createUrl.contains("mem")){
			runSql("DROP SCHEMA PUBLIC CASCADE")
		}
	}

	def createMsSql(dbname,path,driverClassName,url,username,password) {
		//println "about to create db in DbCreate"
		def sql="""
			if db_id(\'${dbname}\') is null
			BEGIN
			use tempdb;
			create database ${dbname} ON PRIMARY(
				NAME=\"${dbname}\",
				FILENAME=\"${path}\\${dbname}.mdf\"
			);
			alter database ${dbname} set recovery simple, auto_shrink on;
			END
			"""
        fireSqlAnt(driverClassName,url,username,password,sql)
	}    

	def createMySql(dbname) {
		runSql("create database IF NOT EXISTS ${dbname};")
	
	}

	def runSql(sql) {
            platform = PlatformFactory.createNewPlatformInstance(dataSource)
			platform.evaluateBatch(sql,false)
	}
	
	def fireSqlAnt(driverClassName,url,username,password,sql) {
		def ant = new AntBuilder()
		ant.sql(print:true,onerror:"continue", autocommit:true, keepformat:true, delimitertype:"row",
			driver:"${driverClassName}",
			url:"${url}", userid:"${username}", password:"${password }", output:"println"){
				transaction(sql)
		}
	}
}
