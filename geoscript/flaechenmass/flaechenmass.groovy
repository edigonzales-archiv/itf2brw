@Grab(group='org.apache.poi', module='poi', version= '3.9')
@Grab(group='org.apache.poi', module='poi-ooxml', version= '3.9')
@Grab(group='log4j', module='log4j', version= '1.2.16')

import groovy.util.logging.* 
import org.apache.log4j.* 

import geoscript.workspace.PostGIS
import geoscript.feature.Field

import java.io.FileOutputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.CreationHelper;

// create db connection and add query
pg = new PostGIS([schema: 'av_dm01avch24d_lv03', user: 'mspublic', password: 'mspublic'], 'rosebud2')

sql = """SELECT b.nummer, b.nbident, ST_Area(a.geometrie) as flaeche, round(ST_Area(a.geometrie)::numeric, 0)::integer as flaeche_gerundet, a.flaechenmass,  (a.flaechenmass - round(ST_Area(a.geometrie)::numeric, 0)::integer)::integer as differenz, a.geometrie, a.gem_bfs, a.los, a.lieferdatum FROM
av_dm01avch24d_lv03.liegenschaften_liegenschaft as a, 
av_dm01avch24d_lv03.liegenschaften_grundstueck as b
WHERE a.liegenschaft_von = b.tid
AND (a.flaechenmass - round(ST_Area(a.geometrie)::numeric, 0)::integer)::integer <> 0"""
//LIMIT 1000"""

layer = pg.addSqlQuery("kontrolle_flaechenmasse_technische_flaeche", sql, new Field("geometrie", "Polygon", "EPSG:21781"), [])

// create excel workbook
XSSFWorkbook workbook = new XSSFWorkbook();
XSSFSheet sheet = workbook.createSheet("flaechenvergleich_1");

// create some styles
CreationHelper createHelper = workbook.getCreationHelper();
CellStyle cellStyle = workbook.createCellStyle();
cellStyle.setDataFormat( createHelper.createDataFormat().getFormat( "dd.mm.yyyy" ) );

int rownum = 0;
Row row = sheet.createRow(rownum++);

// write header
fields = layer.schema.fields
int cellnum = 0;
fields.each() 
{
    if ( !it.isGeometry() )
    {
        Cell cell = row.createCell( cellnum++ );
        cell.setCellValue( it.name );
    }
}

// write contents
layer.eachFeature() 
{
    line = it
    
    row = sheet.createRow(rownum++);
    cellnum = 0
    fields.each() 
    {
        if ( !it.isGeometry() )
        {
            Object obj = line.get( it.name )
            cell = row.createCell( cellnum++ );
            
            if ( obj instanceof Date ) 
            {
                cell.setCellStyle(cellStyle);
                cell.setCellValue((Date)obj);
            }
            else if ( obj instanceof Boolean )
            {
                cell.setCellValue((Boolean)obj);
            }
            else if ( obj instanceof String )
            {
                cell.setCellValue((String)obj);
            }
            else if ( obj instanceof Double )
            {
                cell.setCellValue((Double)obj);
            }
            else if ( obj instanceof Integer )
            {
                cell.setCellValue((Integer)obj);
            }            
        }
    }
}

try 
{
    FileOutputStream out = new FileOutputStream(new File("/home/stefan/tmp/test.xlsx"));
    workbook.write(out);
    out.close();
    println("Excel written successfully..");
} 
catch ( e ) 
{
    e.printStackTrace();
} 

pg.close()


