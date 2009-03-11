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
import org.dbunit.database.*

import org.dbunit.dataset.xml.*;
import org.dbunit.dataset.excel.*;
import org.dbunit.dataset.csv.*;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.database.DatabaseDataSourceConnection

import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext

public class DataExport {
	def dataSource

	def export(tables,path) {
		def db = DbUnitUtil.getConnection(dataSource)
		def tableArray = []
		tables = tables?:"*"
		if(tables.size() == 1 && tables[0].equalsIgnoreCase("*")){
			tableArray=db.createDataSet().getTableNames()
		}else{
			tableArray=tables
		}
		def sql = new Sql(dataSource)
		tableArray.each{table->
			def cnt = sql.firstRow("select count(*) from "+ table)[0]
			if(cnt>0){
				try{
					println "*** Exporting table $table to $path"
					QueryDataSet queryDataSet = new QueryDataSet(db)
					queryDataSet.addTable(table)
					writeTableFile(queryDataSet,path,table)
				}catch(e){
					println "!!!!! error exporting data from $table"
					println e
				}
				
			}
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
	           //writer.setDocType(_doctype);
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
