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
		dbc.dataSource=setupCreateDataSource()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testDrop() {
		assertEquals("MsSql",CH.config.dataLoad.platform)
		dbc.dropMsSql(CH.config.dataLoad.createDbName,
			CH.config.dataSourceMsSqlTests.driverClassName,CH.config.dataLoad.createUrl,
			CH.config.dataSourceMsSqlTests.username,CH.config.dataSourceMsSqlTests.password)
    }

	void testCreate() {
		assertEquals("MsSql",CH.config.dataLoad.platform)
		dbc.createMsSql(CH.config.dataLoad.createDbName,CH.config.dataLoad.createDbPath,
			CH.config.dataSourceMsSqlTests.driverClassName,CH.config.dataLoad.createUrl,
			CH.config.dataSourceMsSqlTests.username,CH.config.dataSourceMsSqlTests.password) 
    }

	def setupCreateDataSource(){
		def username = "sa" 
	    def password = "999plazadrive"
	    def url = CH.config.dataLoad.createUrl 
	    def driverClassName = "net.sourceforge.jtds.jdbc.Driver"
		return new DriverManagerDataSource(driverClassName,url,username,password)
	}
}
