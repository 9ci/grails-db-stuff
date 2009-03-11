import groovy.sql.Sql
import java.sql.SQLException

//includeTargets << grailsScript("Init")
//includeTargets << grailsScript("_GrailsCompile")
includeTargets << grailsScript("Bootstrap")

target(main: "Export table data to files") {
	depends(parseArguments,bootstrap)

	def resPath = argsMap.params[1] ? argsMap.params[1] : "sql/data/out"
	def tables = argsMap.params[0]  ? argsMap.params[0] : "*" //* is all, export all table data if not specified
	
	def dl = grailsApp.classLoader.loadClass("greenbill.dbstuff.DataExport").newInstance()
	dl.dataSource=appCtx.getBean('dataSource')
	dl.export(tables.split(","),resPath)

}

setDefaultTarget(main)
