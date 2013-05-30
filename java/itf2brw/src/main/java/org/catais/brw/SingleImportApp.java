package org.catais.brw;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.catais.brw.process.Processes;
import org.catais.brw.utils.IOUtils;
import org.catais.brw.utils.ReadProperties;
import org.catais.brw.utils.Reindex;
import org.catais.brw.utils.Vacuum;
import org.catais.brw.interlis.*;

import ch.interlis.ili2c.Ili2cException;


public class SingleImportApp {

	private static Logger logger = Logger.getLogger(App.class);
	
	String srcdir = null;

	public static void main(String[] args) {
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
			
			// set random dbschema			
		    Random r = new Random();
		    String alphabet = "abcdefghijklmnoqrstuvwxyz";
		    char prefix = alphabet.charAt( r.nextInt( alphabet.length() ) );
			long milli = new Date().getTime();
			String dbschema = prefix + Long.toString( milli );
			params.put( "dbschema", dbschema );
			
			// import itf
			int gem_bfs = 2479;
			String itf = "../../data/Daten_Niedergoe_030912.itf";
			IliReader iliReader = new IliReader( itf, "21781", params );
			//iliReader.read( gem_bfs, 0 );

			// reindex tables
			logger.info("Start Reindexing...");
			//Reindex reindex = new Reindex( params );
			//reindex.run();
			logger.info("End Reindexing.");
			
			// vacuum tables
		     logger.info("Start Vacuum...");
		     //Vacuum vacuum = new Vacuum( params );
		     //vacuum.run();
             logger.info("End Vacuum.");
             
             // Processs
             Processes processes = new Processes();
             processes.run();
		
			
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
