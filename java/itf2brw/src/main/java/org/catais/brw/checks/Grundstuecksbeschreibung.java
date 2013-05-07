package org.catais.brw.checks;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.catais.brw.interlis.IliReader;
import org.geotools.data.DataStore;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.jdbc.VirtualTable;


public class Grundstuecksbeschreibung 
{	
	private static Logger logger = Logger.getLogger(Grundstuecksbeschreibung.class);
	
	private HashMap params = null;
    private String dbhost = null;
    private String dbport = null;
    private String dbname = null;
    private String dbschema = null;
    private String dbuser = null;
    private String dbpwd = null;	
	
	public Grundstuecksbeschreibung(HashMap params) throws IllegalArgumentException 
    {
    	logger.setLevel(Level.DEBUG);

    	// get parameters
    	this.params = params;
    	readParams();   

    }
	
	
	public void run() throws IOException
	{
		Map params= new HashMap();
		params.put( "dbtype", "postgis" );        
		params.put( "host", this.dbhost );        
		params.put( "port", this.dbport );  
		params.put( "database", this.dbname ); 
		params.put( "schema", this.dbschema );
		params.put( "user", this.dbuser );        
		params.put( "passwd", this.dbpwd ); 
		params.put( PostgisNGDataStoreFactory.VALIDATECONN, true );
		params.put( PostgisNGDataStoreFactory.MAX_OPEN_PREPARED_STATEMENTS, 100 );
		params.put( PostgisNGDataStoreFactory.LOOSEBBOX, true );
		params.put( PostgisNGDataStoreFactory.PREPARED_STATEMENTS, true );
		
		DataStore datastore = new PostgisNGDataStoreFactory().createDataStore( params );
		
		//String sql = "";
		
		
		//VirtualTable vt = new VirtualTable(vtName, sql );
        //((JDBCDataStore) datastore).addVirtualTable(vt);
		
	}

	
    private void readParams() 
    {		
    	this.dbhost = (String) params.get( "dbhost" );
		logger.debug( "dbhost: " + this.dbhost );
		if ( this.dbhost == null ) 
		{
			throw new IllegalArgumentException( "'dbhost' not set." );
		}	
		
    	this.dbport = (String) params.get( "dbport" );
		logger.debug( "dbport: " + this.dbport );		
		if ( this.dbport == null ) 
		{
			throw new IllegalArgumentException( "'dbport' not set." );
		}		
		
    	this.dbname = (String) params.get( "dbname" );
		logger.debug( "dbport: " + this.dbname );		
		if ( this.dbname == null ) 
		{
			throw new IllegalArgumentException( "'dbname' not set." );
		}	
		
    	this.dbschema = (String) params.get( "dbschema" );
		logger.debug( "dbschema: " + this.dbschema );		
		if ( this.dbschema == null ) 
		{
			throw new IllegalArgumentException( "'dbschema' not set." );
		}			

    	this.dbuser = (String) params.get( "dbuser" );
		logger.debug( "dbuser: " + this.dbuser );		
		if ( this.dbuser == null ) 
		{
			throw new IllegalArgumentException( "'dbuser' not set." );
		}	
    	
    	this.dbpwd = (String) params.get( "dbpwd" );
		logger.debug( "dbpwd: " + this.dbpwd );		
		if ( this.dbpwd == null ) 
		{
			throw new IllegalArgumentException( "'dbpwd' not set." );
		}				
	}

}
