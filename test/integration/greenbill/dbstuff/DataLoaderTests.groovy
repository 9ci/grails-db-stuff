package greenbill.dbstuff

import grails.test.GrailsUnitTestCase
import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import org.springframework.jdbc.datasource.DriverManagerDataSource


class DataLoaderTests extends GrailsUnitTestCase {
    def dataSource
    def dc
    boolean transactional = false
    protected void setUp() {
        super.setUp()
        dc = new DataLoader()

    }

    protected void tearDown() {
        super.tearDown()
    }

    // MySql

    def setupCreateDataSourceMySql(){
        def username = "root"
        def password = "xxx"
        def url = CH.config.dataLoad.createUrl + CH.config.dataLoad.createDbName
        def driverClassName = "com.mysql.jdbc.Driver"
        return new DriverManagerDataSource(driverClassName,url,username,password)
    }

    def testSchemaLoadMySql(){
        dc.dataSource=setupCreateDataSourceMySql()
        assertEquals("MySQL",CH.config.dataLoad.platform)
        dc.loadSchema(CH.config.dataLoad.schemaFiles,CH.config.dataLoad.createDbName)
    }


    def testDataLoadMySql(){
        dc.dataSource=setupCreateDataSourceMySql()
        assertEquals("MySQL",CH.config.dataLoad.platform)
        dc.load(CH.config.dataLoad.seedFiles)
    }

    // MsSql

    def setupCreateDataSourceMsSql(){
        def username = "sa"
        def password = "xxx"
        def url = CH.config.dataLoadMsSql.createUrl + CH.config.dataLoadMsSql.createDbName
        def driverClassName = "net.sourceforge.jtds.jdbc.Driver"
        return new DriverManagerDataSource(driverClassName,url,username,password)
    }

    def testSchemaLoadMsSql(){
        dc.dataSource=setupCreateDataSourceMsSql()
        assertEquals("MsSql",CH.config.dataLoadMsSql.platform)
        dc.loadSchema(CH.config.dataLoadMsSql.schemaFiles,CH.config.dataLoad.createDbName)
    }


    def testDataLoadMsSql(){
        dc.dataSource=setupCreateDataSourceMsSql()
        assertEquals("MsSql",CH.config.dataLoadMsSql.platform)
        dc.load(CH.config.dataLoadMsSql.seedFiles)
    }


    // Oracle

    def setupCreateDataSourceOracle(){
        def username = "system"
        def password = "oracle"
        def url = CH.config.dataLoadOracle.createUrl
        def driverClassName = "oracle.jdbc.OracleDriver"
        return new DriverManagerDataSource(driverClassName,url,username,password)
    }

    def testSchemaLoadOracle(){
        dc.dataSource=setupCreateDataSourceOracle()
        assertEquals("Oracle",CH.config.dataLoadOracle.platform)
        dc.loadSchema(CH.config.dataLoadOracle.schemaFiles,CH.config.dataLoad.createDbName)
    }


    def testDataLoadOracle(){
        dc.dataSource=setupCreateDataSourceOracle()
        assertEquals("Oracle",CH.config.dataLoadOracle.platform)
        dc.load(CH.config.dataLoadOracle.seedFiles)
    }

}
