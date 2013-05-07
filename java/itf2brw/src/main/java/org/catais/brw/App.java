package org.catais.brw;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.catais.brw.utils.IOUtils;
import org.catais.brw.utils.ReadProperties;
import org.catais.brw.checks.Grundstuecksbeschreibung;
import org.catais.brw.interlis.*;

import ch.interlis.ili2c.Ili2cException;


public class App 
{
	private static Logger logger = Logger.getLogger(App.class);
	
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
			ReadProperties ini = new ReadProperties();
			HashMap params = ini.read();
			logger.debug(params);
			
			// import itf
			//String itf = "data/ch_252400.itf";
			String itf = "../../data/ch_252400.itf";
			IliReader iliReader = new IliReader( itf, "21781", params );
			//iliReader.read();
			
			// Checks
			// Verschnitt BB-LS (Grundstücksbeschreibung)
			Grundstuecksbeschreibung be = new Grundstuecksbeschreibung( params );
			
			
			
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
