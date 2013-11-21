/* Copyright 2006-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

includeTargets << new File("$databaseMigrationPluginDir/scripts/_DatabaseMigrationCommon.groovy")
includeTargets << new File("${dbStuffPluginDir}/scripts/_ConfigDataSource.groovy")

target(dbCreate: 'Updates database to current version') {

    depends(parseArguments,packageApp,loadApp,dbmInit)

    def dbc = grailsApp.classLoader.loadClass("greenbill.dbstuff.DbCreate").newInstance()
    dbc.dataSource=getCreateDataSource()
    println "about to drop and recreate the database for ${dsConfig.dataLoad.createDbName}"
    println "dbs.datasource.url ${dbc.dataSource.url}"
     if (!dbc.dataSource.url.contains("Oracle")){
        dbc.dropAndCreate(dsConfig.dataLoad.createDbName,dsConfig)
     }
     println "created ${dsConfig.dataLoad.createDbName}"

    try {
        MigrationUtils.executeInSession {
            database = MigrationUtils.getDatabase(defaultSchema)
            liquibase = MigrationUtils.getLiquibase(database, 'schema.groovy')

            def ldsconfig = config.dataSource
            String dbDesc = dsConfig.jndiName ? "JNDI $ldsconfig.jndiName" : "$ldsconfig.username @ $ldsconfig.url"
            echo "Starting $hyphenatedScriptName for database $dbDesc"
            liquibase.update contexts
            echo "Finished $hyphenatedScriptName"
        }
    }

    catch (e) {
        ScriptUtils.printStackTrace e
        exit 1
    }
    finally {
        ScriptUtils.closeConnection database
    }



    def dl = grailsApp.classLoader.loadClass("greenbill.dbstuff.DataLoader").newInstance()
    dl.dataSource=getDataSource()
    println "creating the base seed data from files ${dsConfig.dataLoad.seedFiles}"
    dl.load(dsConfig.dataLoad.seedFiles,null)

    println "loading the data files from files ${dsConfig.dataLoad.dataFiles}"
    dl.load(dsConfig.dataLoad.dataFiles,null)

    println "loading another set of data files from files ${dsConfig.dataLoad.dataFilesExtra}"
    if(dsConfig.dataLoad.dataFilesExtra){
        dl.load(dsConfig.dataLoad.dataFilesExtra,null)
    }

    println "running the scripts in ${dsConfig.dataLoad.sqlFiles}"
    grailsApp.parentContext.getResources(dsConfig.dataLoad.sqlFiles).each{

        ant.sql(src:"${it.file.absolutePath}",showheaders:"false",showtrailers:"false",
                print:"false", autocommit:true, keepformat:true, delimitertype:"row",
                driver:"${dsConfig.dataSource.driverClassName}",
                url:"${dsConfig.dataSource.url}", userid:"${dsConfig.dataSource.username}", password:"${dsConfig?.dataSource?.password }"){}

    }

    doAndClose {
        liquibase.update contexts
    }
}

setDefaultTarget dbCreate

