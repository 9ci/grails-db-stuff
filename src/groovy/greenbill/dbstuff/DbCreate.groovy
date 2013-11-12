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

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.apache.ddlutils.PlatformFactory;

public class DbCreate {

	def dataSource
	def platform
	
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
		}else if (platformName.contains("HsqlDb")){
			dropHsql(dbname) 
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
		fireMsSql(dbname,driverClassName,url,username,password,sql)
	}
	
	def dropMySql(dbname) {
		runSql(	"""
				use mysql;
				drop database if exists ${dbname};
		""")
	}

    def dropOracle(dbname) {
        runSql(	"""
				BEGIN
                    EXECUTE IMMEDIATE 'DROP USER ${dbname}';
                EXCEPTION
                    WHEN OTHERS THEN
                        IF SQLCODE != -1918 THEN
                            RAISE;
                        END IF;
                END;
		""")
    }

    def dropTableOracle(tblname) {
        runSql("""
                BEGIN
                    EXECUTE IMMEDIATE 'DROP TABLE ${tblname}';
                EXCEPTION
                    WHEN OTHERS THEN
                        IF SQLCODE != -942 THEN
                            RAISE;
                        END IF;
                END;
        """)
    }

	def dropHsql(dbname) {
		if(!ApplicationHolder.application.config.dataLoad.createUrl.contains("mem")){
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
		//println sql
		fireMsSql(dbname,driverClassName,url,username,password,sql)
	}    

	def createMySql(dbname) {
		runSql("create database IF NOT EXISTS ${dbname};")
	
	}
    
    def createOracle(dbname){
        runSql("""
            -- USER SQL
                CREATE USER ${dbname} IDENTIFIED BY ${dbname} ;
    
            -- SYSTEM PRIVILEGES
    
            -- QUOTAS
    
        """)
    }

	def runSql(sql) {
		//try {
            platform = PlatformFactory.createNewPlatformInstance(dataSource)
			println sql
			platform.evaluateBatch(sql,false)
			//def db = new Sql(dataSource)
		   	//db.execute(sql)
		//} catch(Exception e){
		//	e.printStackTrace()
		//}
	}
	
	def fireMsSql(dbname,driverClassName,url,username,password,sql) {
		def ant = new AntBuilder()
		ant.sql(print:true,onerror:"continue", autocommit:true, keepformat:true, delimitertype:"row",
			driver:"${driverClassName}",
			url:"${url}", userid:"${username}", password:"${password }", output:"println"){
				transaction(sql)
		}
	}
}
