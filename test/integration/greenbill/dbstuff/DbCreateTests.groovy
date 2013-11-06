package greenbill.dbstuff

import groovy.sql.Sql
import java.sql.SQLException
import org.springframework.jdbc.datasource.DriverManagerDataSource
import grails.test.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

class DbCreateTests extends GrailsUnitTestCase {
	def dataSource
	def dbc
	boolean transactional = false
    protected void setUp() {
        super.setUp()
		dbc = new DbCreate()

    }

    protected void tearDown() {
        super.tearDown()
    }

    // MySql

    def setupCreateDataSourceMySql(){
        def username = "root"
        def password = "xxx"
        def url = CH.config.dataLoad.createUrl
        def driverClassName = "com.mysql.jdbc.Driver"
        return new DriverManagerDataSource(driverClassName,url,username,password)
    }

    void testDropMySql() {
        dbc.dataSource=setupCreateDataSourceMySql()
        assertEquals("MySQL",CH.config.dataLoad.platform)
        dbc.dropMySql(CH.config.dataLoad.createDbName)
    }

    void testCreateMySql() {
        dbc.dataSource=setupCreateDataSourceMySql()
        assertEquals("MySQL",CH.config.dataLoad.platform)
        dbc.createMySql(CH.config.dataLoad.createDbName)
    }

    // MsSql

    def setupCreateDataSourceMsSql(){
        def username = "sa"
        def password = "999plazadrive"
        def url = CH.config.dataLoad.createUrl
        def driverClassName = "net.sourceforge.jtds.jdbc.Driver"
        return new DriverManagerDataSource(driverClassName,url,username,password)
    }

    void testDropMsSql() {
        dbc.dataSource=setupCreateDataSourceMsSql()
		assertEquals("MsSql",CH.config.dataLoad.platform)
		dbc.dropMsSql(CH.config.dataLoad.createDbName,
			CH.config.dataSourceMsSqlTests.driverClassName,CH.config.dataLoad.createUrl,
			CH.config.dataSourceMsSqlTests.username,CH.config.dataSourceMsSqlTests.password)
    }

	void testCreateMsSql() {
        dbc.dataSource=setupCreateDataSourceMsSql()
        assertEquals("MsSql",CH.config.dataLoad.platform)
		dbc.createMsSql(CH.config.dataLoad.createDbName,CH.config.dataLoad.createDbPath,
			CH.config.dataSourceMsSqlTests.driverClassName,CH.config.dataLoad.createUrl,
			CH.config.dataSourceMsSqlTests.username,CH.config.dataSourceMsSqlTests.password)
    }

    // Oracle

    def setupCreateDataSourceOracle(){
        def username = "sa"
        def password = "999plazadrive"
        def url = CH.config.dataLoad.createUrl
        def driverClassName = "net.sourceforge.jtds.jdbc.Driver"
        return new DriverManagerDataSource(driverClassName,url,username,password)
    }

    void testDropMsSqlOracle() {
        dbc.dataSource=setupCreateDataSourceOracle()
        assertEquals("MsSql",CH.config.dataLoad.platform)
        dbc.dropMsSql(CH.config.dataLoad.createDbName,
                CH.config.dataSourceMsSqlTests.driverClassName,CH.config.dataLoad.createUrl,
                CH.config.dataSourceMsSqlTests.username,CH.config.dataSourceMsSqlTests.password)
    }

    void testCreateMsSqlOracle() {
        dbc.dataSource=setupCreateDataSourceOracle()
        assertEquals("MsSql",CH.config.dataLoad.platform)
        dbc.createMsSql(CH.config.dataLoad.createDbName,CH.config.dataLoad.createDbPath,
                CH.config.dataSourceMsSqlTests.driverClassName,CH.config.dataLoad.createUrl,
                CH.config.dataSourceMsSqlTests.username,CH.config.dataSourceMsSqlTests.password)
    }

}
