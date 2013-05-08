package org.catais.brw.checks;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.catais.brw.interlis.IliReader;
import org.geotools.data.DataStore;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.Hints;
//import org.geotools.filter.Filter;
//import org.geotools.filter.FilterFactory;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.jdbc.VirtualTable;
import org.geotools.jdbc.VirtualTableParameter;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.data.Query;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.prep.PreparedPoint;


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
	
	public Grundstuecksbeschreibung( HashMap params ) throws IllegalArgumentException 
    {
    	logger.setLevel(Level.DEBUG);

    	// get parameters
    	this.params = params;
    	readParams();   

    }
	
	
	public void run() throws IOException, CQLException
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
		
		// Verschnitt BB/LS vorbereiten als parametrisierter VirtualTable
		// Eindeutigkeit der Nummern? egrid/nbident...?
		String sql = "SELECT  a.art, a.art_txt, sum(ST_Area(b.geometrie)) as flaeche " +
				"FROM " +
				this.dbschema + ".bodenbedeckung_boflaeche as a, " +
				"( " +
				" SELECT 1 as ogc_fid, ST_PointOnSurface(a.geom) as point, a.geom as geometrie  " +
				" FROM " +
				" ( " +
				"  SELECT 1 as ogc_fid, (ST_Dump(ST_Intersection(a.geometrie, b.geometrie))).geom " +
				"  FROM " +
				this.dbschema +".bodenbedeckung_boflaeche as a, " +
				"  ( " +
				"   SELECT c.*, b.nummer, b.egris_egrid, b.nbident " +
				"   FROM " + this.dbschema + ".liegenschaften_grundstueck as b, " + this.dbschema + ".liegenschaften_liegenschaft as c " +
				"   WHERE c.liegenschaft_von = b.tid " +
				"  ) as b " +
				"  WHERE ST_Intersects(b.geometrie, a.geometrie) " +
				"  AND b.nummer = '%nummer%' " +
				"  AND b.nbident = '%nbident%' " +
				" ) as a " +
				" WHERE geometrytype(a.geom) = 'POLYGON' AND ST_isValid(a.geom) " +
				") as b " +
				"WHERE a.geometrie && b.geometrie " +
				"AND ST_Distance(a.geometrie, b.point) = 0 " +
				"GROUP BY art, art_txt " +
				"ORDER BY art ";
		logger.debug(sql);
		
		
		VirtualTable vt = new VirtualTable( "verschnitt", sql );
		VirtualTableParameter p1 = new VirtualTableParameter( "nummer", "0" );
		VirtualTableParameter p2 = new VirtualTableParameter( "nbident", "0" );
		
		vt.addParameter( p1 );
		vt.addParameter( p2 );
		
		((JDBCDataStore) datastore).addVirtualTable( vt );
		
		Map<String,String> viewParams = new HashMap<String,String>();
        viewParams.put("nummer","135");
        viewParams.put("nbident", "SO0200002524");
        
		Hints hints = new Hints();
		hints.put(Hints.VIRTUAL_TABLE_PARAMETERS, viewParams);
		Query query = new Query(Query.ALL);
		query.setHints(hints);
		
		SimpleFeatureSource s2 = datastore.getFeatureSource("verschnitt");
		SimpleFeatureCollection fc2 = s2.getFeatures(query);
		logger.debug(fc2.size());

		FeatureIterator jt = fc2.features();
		try 
		{
			while( jt.hasNext() )
			{
				SimpleFeature feat = (SimpleFeature) jt.next();
				logger.debug((String) feat.getAttribute("art_txt") + (Double) feat.getAttribute("flaeche"));
			}
		}
		finally 
		{
		     jt.close();
		}

		

		
		
		// GB-Nummern aller Liegenschaften
		//logger.debug(datastore.getNames());
		
		SimpleFeatureSource s1 = datastore.getFeatureSource("liegenschaften_grundstueck");
						
		Filter f1 = CQL.toFilter("art_txt = 'Liegenschaft'");
		SimpleFeatureCollection fc1 = s1.getFeatures(f1);
		logger.debug(fc1.size());
		
		FeatureIterator it = fc1.features();
		try 
		{
			while( it.hasNext() )
			{
				SimpleFeature feat = (SimpleFeature) it.next();
				//logger.debug(feat.getAttribute("nummer"));
			}
		}
		finally 
		{
		     it.close();
		}
		
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
