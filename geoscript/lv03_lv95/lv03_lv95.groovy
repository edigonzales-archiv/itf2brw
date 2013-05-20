@Grab(group='org.apache.poi', module='poi', version= '3.9')
@Grab(group='org.apache.poi', module='poi-ooxml', version= '3.9')
@Grab(group='log4j', module='log4j', version= '1.2.16')

import groovy.util.logging.* 
import org.apache.log4j.* 

import geoscript.workspace.PostGIS
import geoscript.workspace.Directory
import geoscript.feature.Field

import java.io.FileOutputStream;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.CreationHelper;

final OUTPUTDIR = "/home/stefan/tmp/"
final EXCELFILENAME = "lv03_lv95"
final SHAPEFILENAME = "lv03_lv95"
final date = new Date().format("yyyy-MM-dd")

// create db connection and query
pg = new PostGIS([schema: 'av_dm01avch24d_lv03', user: 'mspublic', password: 'mspublic'], 'rosebud2')

sql = """SELECT c.nummer, c.nbident, c.flaeche_gerundet as lv03_area, d.flaeche_gerundet as lv95_area, (d.flaeche_gerundet - c.flaeche_gerundet) as diff, c.geometrie, c.gem_bfs, c.los, c.lieferdatum, current_date as datum FROM
(
 SELECT b.nummer, b.nbident, ST_Area(a.geometrie) as flaeche, round(ST_Area(a.geometrie)::numeric, 0) as flaeche_gerundet, a.geometrie, a.gem_bfs, a.los, a.lieferdatum FROM
 av_dm01avch24d_lv03.liegenschaften_liegenschaft as a, 
 av_dm01avch24d_lv03.liegenschaften_grundstueck as b
 WHERE a.liegenschaft_von = b.tid
) as c,
(
 SELECT b.nummer, b.nbident, ST_Area(a.geometrie) as flaeche, round(ST_Area(a.geometrie)::numeric, 0) as flaeche_gerundet, a.gem_bfs, a.los, a.lieferdatum FROM
 av_dm01avch24d_lv95.liegenschaften_liegenschaft as a, 
 av_dm01avch24d_lv95.liegenschaften_grundstueck as b
 WHERE a.liegenschaft_von = b.tid
) as d
WHERE c.nummer = d.nummer
AND c.nbident = d.nbident"""

layer = pg.addSqlQuery(SHAPEFILENAME + "_" + date, sql, new Field("geometrie", "Polygon", "EPSG:21781"), [])

// export shapefile
Directory dir = new Directory(OUTPUTDIR)
dir.add(layer)

/*
// create excel workbook
XSSFWorkbook workbook = new XSSFWorkbook();
XSSFSheet sheet = workbook.createSheet("flaechenvergleich_1");

// create some styles
CreationHelper createHelper = workbook.getCreationHelper();
CellStyle cellStyle = workbook.createCellStyle();
cellStyle.setDataFormat( createHelper.createDataFormat().getFormat( "dd.mm.yyyy" ) );

// write header
int rownum = 0;
Row row = sheet.createRow(rownum++);

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
    FileOutputStream out = new FileOutputStream(new File(OUTPUTDIR + EXCELFILENAME + "_" + date + ".xlsx"));
    workbook.write(out);
    out.close();
    println("Excel written successfully..");
} 
catch ( e ) 
{
    e.printStackTrace();
} 

// close db connection
pg.close()

*/
