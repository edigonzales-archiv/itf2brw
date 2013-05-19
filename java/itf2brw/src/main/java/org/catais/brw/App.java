package org.catais.brw;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.catais.brw.utils.IOUtils;
import org.catais.brw.utils.ReadProperties;
import org.catais.brw.utils.Reindex;
import org.catais.brw.utils.Vacuum;
import org.catais.brw.checks.Grundstuecksbeschreibung;
import org.catais.brw.interlis.*;

import ch.interlis.ili2c.Ili2cException;


public class App 
{
	private static Logger logger = Logger.getLogger(App.class);
	
	String srcdir = null;
	
    public static void main( String[] args )
    {
    	logger.setLevel(Level.DEBUG);

    	try {
	    	// read log4j properties file
	    	File tempDir = IOUtils.createTempDirectory( "itf2brw" );
			InputStream is =  App.class.getResourceAsStream( "log4j.properties" );
			File log4j = new File( tempDir, "log4j.properties" );
			IOUtils.copy(is, log4j);
	
			// configure log4j with properties file
			PropertyConfigurator.configure( log4j.getAbsolutePath() );
	
			// begin logging
			logger.info( "Start: "+ new Date() );
			
			// read the properties file with all the things we need to know
			// filename is itf2brw.properties
			String iniFileName = (String) args[0];
			ReadProperties ini = new ReadProperties(iniFileName);
			HashMap params = ini.read();
			logger.debug(params);
			
			// read directory
			String srcdir = (String) params.get("srcdir");
		
			File dir = new File(srcdir);
			String[] fileList = dir.list(new FilenameFilter() {
			    public boolean accept(File d, String name) {
			       return name.toLowerCase().endsWith(".itf");
			    }
			});
			
			for ( int i = 0; i < fileList.length; i++ )
			{
			  String itf = fileList[i];
			  System.out.println("Element " + i + ": " + dir.getAbsolutePath() + dir.separator + itf);

			  int gem_bfs;
			  int los;
			  String epsg = null;
			  
			  String frame = (String) params.get("frame");
			  if ( frame.equalsIgnoreCase("LV95") )
			  {
				  gem_bfs = Integer.valueOf(itf.substring(8, 12)).intValue();
				  los = Integer.valueOf(itf.substring(12, 14)).intValue();
				  epsg = "2056";
			  }
			  else
			  {
				  gem_bfs = Integer.valueOf(itf.substring(3, 7)).intValue();
				  los = Integer.valueOf(itf.substring(7, 9)).intValue();
				  epsg = "21781";
			  }
			  
			  logger.debug(gem_bfs +" "+ los);

			  IliReader iliReader = new IliReader( dir.getAbsolutePath() + dir.separator + itf, epsg, params );
			  
			  if ( frame.equalsIgnoreCase("LV95") )
			  {
				  iliReader.setTidPrefix( itf.substring(8, 12) + itf.substring(12, 14) );
			  }
			  else
			  {
				  iliReader.setTidPrefix( itf.substring(3, 7) + itf.substring(7, 9) );
			  }	  
			  iliReader.delete( gem_bfs, los );
			  iliReader.read( gem_bfs, los );
			}
			

			// reindex tables
			logger.info("Start Reindexing...");
			Reindex reindex = new Reindex( params );
			reindex.run();
			logger.info("End Reindexing.");
			
			// vacuum tables
		     logger.info("Start Vacuum...");
		     Vacuum vacuum = new Vacuum( params );
		     vacuum.run();
             logger.info("End Vacuum.");
			
			
			// Checks
			// Verschnitt BB-LS (Grundstücksbeschreibung)
			//Grundstuecksbeschreibung be = new Grundstuecksbeschreibung( params );
			//be.run();
			
			
			System.out.println ("should not reach here in case of errors.." );


    	
    	} 
    	catch ( Ili2cException ex )
    	{
    		ex.printStackTrace();
			logger.fatal( ex.getMessage() );
    	}
    	catch ( IOException ex ) 
    	{
    		ex.printStackTrace();
			logger.fatal( ex.getMessage() );
    	} 
    	catch ( NullPointerException ex ) 
    	{
    		ex.printStackTrace();
			logger.fatal( ex.getMessage() );
    	} 
    	catch ( IllegalArgumentException ex ) 
    	{
    		ex.printStackTrace();
			logger.fatal( ex.getMessage() );
    	} 
    	catch ( ClassNotFoundException ex )
    	{
    		ex.printStackTrace();
			logger.fatal( ex.getMessage() );
    	}
    	catch ( SQLException ex )
    	{
    		ex.printStackTrace();
			logger.fatal( ex.getMessage() );
    	}
    	catch ( Exception ex ) 
    	{
    		ex.printStackTrace();
    		logger.fatal( ex.getMessage() );
    	} 
    	finally {
    		// stop logging
			logger.info( "Ende: "+ new Date() );
    	}
        System.out.println( "Hello Stefan!" );
    }
}
