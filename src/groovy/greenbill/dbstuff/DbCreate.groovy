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
    
            -- ROLES
            GRANT "OLAP_XS_ADMIN" TO ${dbname} ;
            GRANT "WFS_USR_ROLE" TO ${dbname} ;
            GRANT "DELETE_CATALOG_ROLE" TO ${dbname} ;
            GRANT "TT_CACHE_ADMIN_ROLE" TO ${dbname} ;
            GRANT "HS_ADMIN_SELECT_ROLE" TO ${dbname} ;
            GRANT "CWM_USER" TO ${dbname} ;
            GRANT "SPATIAL_WFS_ADMIN" TO ${dbname} ;
            GRANT "OLAP_DBA" TO ${dbname} ;
            GRANT "OWB\$CLIENT" TO ${dbname} ;
            GRANT "XFILES_ADMINISTRATOR" TO ${dbname} ;
            GRANT "RESOURCE" TO ${dbname} ;
            GRANT "APEX_ADMINISTRATOR_ROLE" TO ${dbname} ;
            GRANT "OWB_DESIGNCENTER_VIEW" TO ${dbname} ;
            GRANT "CTXAPP" TO ${dbname} ;
            GRANT "SPATIAL_CSW_ADMIN" TO ${dbname} ;
            GRANT "GATHER_SYSTEM_STATISTICS" TO ${dbname} ;
            GRANT "AUTHENTICATEDUSER" TO ${dbname} ;
            GRANT "CONNECT" TO ${dbname} ;
            GRANT "HS_ADMIN_EXECUTE_ROLE" TO ${dbname} ;
            GRANT "LOGSTDBY_ADMINISTRATOR" TO ${dbname} ;
            GRANT "JAVADEBUGPRIV" TO ${dbname} ;
            GRANT "XDB_WEBSERVICES_WITH_PUBLIC" TO ${dbname} ;
            GRANT "XDBADMIN" TO ${dbname} ;
            GRANT "XDB_WEBSERVICES_OVER_HTTP" TO ${dbname} ;
            GRANT "EXP_FULL_DATABASE" TO ${dbname} ;
            GRANT "CSW_USR_ROLE" TO ${dbname} ;
            GRANT "XFILES_USER" TO ${dbname} ;
            GRANT "OLAPI_TRACE_USER" TO ${dbname} ;
            GRANT "JAVAIDPRIV" TO ${dbname} ;
            GRANT "DBFS_ROLE" TO ${dbname} ;
            GRANT "ADM_PARALLEL_EXECUTE_TASK" TO ${dbname} ;
            GRANT "AQ_ADMINISTRATOR_ROLE" TO ${dbname} ;
            GRANT "JAVA_DEPLOY" TO ${dbname} ;
            GRANT "OEM_MONITOR" TO ${dbname} ;
            GRANT "XDB_WEBSERVICES" TO ${dbname} ;
            GRANT "JAVAUSERPRIV" TO ${dbname} ;
            GRANT "MGMT_USER" TO ${dbname} ;
            GRANT "OWB_USER" TO ${dbname} ;
            GRANT "JAVA_ADMIN" TO ${dbname} ;
            GRANT "JMXSERVER" TO ${dbname} ;
            GRANT "EXECUTE_CATALOG_ROLE" TO ${dbname} ;
            GRANT "SCHEDULER_ADMIN" TO ${dbname} ;
            GRANT "DATAPUMP_IMP_FULL_DATABASE" TO ${dbname} ;
            GRANT "WM_ADMIN_ROLE" TO ${dbname} ;
            GRANT "ORDADMIN" TO ${dbname} ;
            GRANT "AQ_USER_ROLE" TO ${dbname} ;
            GRANT "DATAPUMP_EXP_FULL_DATABASE" TO ${dbname} ;
            GRANT "SELECT_CATALOG_ROLE" TO ${dbname} ;
            GRANT "RECOVERY_CATALOG_OWNER" TO ${dbname} ;
            GRANT "OLAP_USER" TO ${dbname} ;
            GRANT "DBA" TO ${dbname} ;
            GRANT "JAVASYSPRIV" TO ${dbname} ;
            GRANT "XDB_SET_INVOKER" TO ${dbname} ;
            GRANT "IMP_FULL_DATABASE" TO ${dbname} ;
            GRANT "HS_ADMIN_ROLE" TO ${dbname} ;
            GRANT "APEX_GRANTS_FOR_NEW_USERS_ROLE" TO ${dbname} ;
            GRANT "EJBCLIENT" TO ${dbname} ;
            GRANT "OEM_ADVISOR" TO ${dbname} ;
    
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
