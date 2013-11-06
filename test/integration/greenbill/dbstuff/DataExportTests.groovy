package greenbill.dbstuff

import grails.test.GrailsUnitTestCase
import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import org.springframework.jdbc.datasource.DriverManagerDataSource

class DataExportTests extends GrailsUnitTestCase {
    def dataSource
    def de
    boolean transactional = false
    protected void setUp() {
        super.setUp()
        de = new DataExport()

    }

    protected void tearDown() {
        super.tearDown()
    }


    def testTrue(){
        assert true
    }

    // MySql

    def setupCreateDataSourceMySql(){
        def username = "root"
        def password = "xxx"
        def url = CH.config.dataLoad.createUrl + CH.config.dataLoad.createDbName
        def driverClassName = "com.mysql.jdbc.Driver"
        return new DriverManagerDataSource(driverClassName,url,username,password)
    }

    def testExportMySql(){
        de.dataSource=setupCreateDataSourceMySql()
        assertEquals("MySQL",CH.config.dataLoad.platform)
        de.export("Notes,Parameters", "/home/holger/9ci/target/export/")
    }

    def testExportDiffMySql(){
        de.dataSource=setupCreateDataSourceMySql()
        assertEquals("MySQL",CH.config.dataLoad.platform)
        de.exportDiff(CH.config.dataLoad.seedFiles, "/home/holger/9ci/target/export/")
    }


//
//    // MsSql
//
//    def setupCreateDataSourceMsSql(){
//        def username = "sa"
//        def password = "xxx"
//        def url = CH.config.dataLoadMsSql.createUrl
//        def driverClassName = "net.sourceforge.jtds.jdbc.Driver"
//        return new DriverManagerDataSource(driverClassName,url,username,password)
//    }
//
//    // Oracle
//
//    def setupCreateDataSourceOracle(){
//        def username = "system"
//        def password = "oracle"
//        def url = CH.config.dataLoadOracle.createUrl
//        def driverClassName = "net.sourceforge.jtds.jdbc.Driver"
//        return new DriverManagerDataSource(driverClassName,url,username,password)
//    }
}
