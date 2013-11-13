package greenbill.dbstuff

import grails.test.GrailsUnitTestCase
import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import org.springframework.jdbc.datasource.DriverManagerDataSource

class T03_DataExportTests extends GrailsUnitTestCase {
    def dataSource
    def de
    boolean transactional = false
    def outPath
    protected void setUp() {
        super.setUp()
        de = new DataExport()
       outPath ="/home/holger/9ci/target/"
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
        de.export("Notes,Parameters", outPath)
    }

    def testExportDiffMySql(){
        de.dataSource=setupCreateDataSourceMySql()
        assertEquals("MySQL",CH.config.dataLoad.platform)
        de.exportDiff(CH.config.dataLoad.seedFiles, outPath)
    }



    // MsSql

    def setupCreateDataSourceMsSql(){
        def username = "sa"
        def password = "xxx"
        def url = CH.config.dataLoadMsSql.createUrl + CH.config.dataLoadMsSql.createDbName
        def driverClassName = "net.sourceforge.jtds.jdbc.Driver"
        return new DriverManagerDataSource(driverClassName,url,username,password)
    }

    def testExportMsSql(){
        de.dataSource=setupCreateDataSourceMsSql()
        assertEquals("MsSql",CH.config.dataLoadMsSql.platform)
        de.export("Notes,Parameters", outPath)
    }

    def testExportDiffMsSql(){
        de.dataSource=setupCreateDataSourceMsSql()
        assertEquals("MsSql",CH.config.dataLoadMsSql.platform)
        de.exportDiff(CH.config.dataLoadMsSql.seedFiles, outPath)
    }

    // Oracle

    def setupCreateDataSourceOracle(){
        def username = "dbstufftest"
        def password = "oracle"
        def url = CH.config.dataLoadOracle.createUrl + ":" + CH.config.dataLoadOracle.createDbName
        def driverClassName = "oracle.jdbc.OracleDriver"
        return new DriverManagerDataSource(driverClassName,url,username,password)
    }

    def testExportOracle(){
        de.dataSource=setupCreateDataSourceOracle()
        assertEquals("Oracle10",CH.config.dataLoadOracle.platform)
        de.export("Notes,Parameters", outPath)
    }

    def testExportDiffOracle(){
        de.dataSource=setupCreateDataSourceOracle()
        assertEquals("Oracle10",CH.config.dataLoadOracle.platform)
        de.exportDiff(CH.config.dataLoadOracle.seedFiles, outPath)
    }
}
