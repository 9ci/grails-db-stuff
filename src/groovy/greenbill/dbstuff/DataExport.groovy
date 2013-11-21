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
import org.dbunit.dataset.xml.*;
import org.dbunit.dataset.excel.*;
import org.dbunit.dataset.csv.*
import org.apache.ddlutils.io.DatabaseDataDiffIO
import org.apache.ddlutils.PlatformFactory
import org.apache.ddlutils.model.Database

public class DataExport {
	def dataSource

	def exportDiff(inputPath,outputPath){
		def appCtx = ApplicationHolder.application.parentContext
        def platform = PlatformFactory.createNewPlatformInstance(dataSource)
        def model
        if(platform.name != "Oracle") {
            model = platform.readModelFromDatabase(null);
        } else {
            platform = PlatformFactory.createNewPlatformInstance("Oracle10")
            platform.setDataSource(dataSource)
            def dbName = ((String)dataSource.username).toUpperCase()
            model = platform.readModelFromDatabase(dbName,null, dbName, null)
        }
        DatabaseDataDiffIO dataio = new DatabaseDataDiffIO();
		def toCompare = appCtx.getResources(inputPath).collect{it.inputStream} as InputStream[]
		try{
			dataio.writeDiffDataToXML(platform, model, outputPath, toCompare)
		}catch(e){
			println "!!!!! error writing data"
			e.printStackTrace() 
		}finally{
			toCompare.each(){it.close() }
		}
	}
	
	def export(tables,outPath) {

		String[] tableArray = tables.split(",")

        def platform = PlatformFactory.createNewPlatformInstance(dataSource)

        Database model
        if(platform.name != "Oracle") {
            model = platform.readModelFromDatabase(null);
        } else {
            platform = PlatformFactory.createNewPlatformInstance("Oracle10")
            platform.setDataSource(dataSource)
            def dbName = ((String)dataSource.username).toUpperCase()
            model = platform.readModelFromDatabase(dbName,null, dbName, null)
        }

        DatabaseDataDiffIO dataio = new DatabaseDataDiffIO();
		try{
            dataio.writeDataToXML(platform,model, (tableArray as List), outPath)
		}catch(e){
			println "!!!!! error writing data"
			e.printStackTrace() 
		}
	}
	
	def writeTableFile(dataset,path,fileName){
		def _format ="xml"
		def filepath = "${path}/${fileName}.xml"
		(new File(path)).mkdirs()
		(new File(filepath)).createNewFile()
		OutputStream out = new FileOutputStream(filepath);
       try{
		   if (_format.equalsIgnoreCase("xml")){
               FlatXmlWriter writer = new FlatXmlWriter(out, "UTF-8");
	           writer.write(dataset);
           }
           else if (_format.equalsIgnoreCase("csv")){
               CsvDataSetWriter.write(dataset, filepath);
           }
           else if (_format.equalsIgnoreCase("xls")){
               XlsDataSet.write(dataset, out);
           }
       }
       finally
       {
           out.close();
       }
	}

	
}
