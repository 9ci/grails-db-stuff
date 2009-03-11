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
package greenbill.dbstuff;
import groovy.sql.Sql
import java.sql.SQLException

import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.commons.ApplicationHolder

import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext
	
import org.apache.ddlutils.io.DatabaseDataIO;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;

public class DbCreate {

	def dataSource
	def platform
	
	def dropAndCreate(dbname,path) {
		 platform = PlatformFactory.createNewPlatformInstance(dataSource)
		def platformName = platform.name.toLowerCase();
		if (platformName.contains("mssql")){
			dropMsSql(dbname)
			createMsSql(dbname,path)
		}else if (platformName.contains("mysql")){
			dropMySql(dbname)
			createMySql(dbname)
		}else if (platformName.contains("HsqlDb")){
			dropHsql(dbname) 
		}else throw new IllegalArgumentException("Drop and Create not supported for this databse yet")
	}
	def create(dbname,path) {
		platform = PlatformFactory.createNewPlatformInstance(dataSource)
		platformName = platform.name.toLowerCase();
		if (platformName.contains("mssql")){
			createMsSql(dbname,path)
		}else if (platformName.contains("mysql")){
			createMySql(dbname)
		}else if (platformName.contains("HsqlDb")){
		}else throw new IllegalArgumentException("Create not supported for this databse yet")
	}
	
	def dropMsSql(dbname) {
		runSql(	"""
				if db_id(\'${dbname}\') is not null
				BEGIN
				Alter database ${dbname} set single_user with rollback immediate; 
				use tempdb;
				drop database ${dbname};
				END
		""")
	}
	
	def dropMySql(dbname) {
		runSql(	"""
				use mysql;
				drop database if exists ${dbname};
		""")
	}
	def dropHsql(dbname) {
		if(!ApplicationHolder.application.config.dataLoad.createUrl.contains("mem")){
			runSql("DROP SCHEMA PUBLIC CASCADE")
		}
	}

	def createMsSql(dbname,path) {
		runSql(	"""
			if db_id(\'${dbname}\') is not null
			BEGIN
			create database ${dbname} ON PRIMARY(
				NAME=\"${datasource.dbname}\",
				FILENAME=\"${path}\\${dbname}.mdf\"
			);
			alter database ${dbname} set recovery simple, auto_shrink on;
			END
			""")
		
	}
	def createMySql(dbname) {
		runSql("create database IF NOT EXISTS ${dbname};")
	
	}
	def runSql(sql) {
		//try {
			println sql
			platform.evaluateBatch(sql,false)
			//def db = new Sql(dataSource)
		   	//db.execute(sql)
		//} catch(Exception e){
		//	e.printStackTrace()
		//}
	}
}
