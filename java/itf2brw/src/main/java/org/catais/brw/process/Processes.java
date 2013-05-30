package org.catais.brw.process;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.geotools.feature.NameImpl;
import org.geotools.process.ProcessExecutor;
import org.geotools.process.Processors;
import org.geotools.process.Progress;
import org.geotools.process.Process;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.factory.StaticMethodsProcessFactory;
import org.geotools.text.Text;
import org.geotools.util.KVP;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class Processes 
{
	private static Logger logger = Logger.getLogger( Processes.class );
	
	public Processes() {}
	
	public void run () throws ParseException, InterruptedException, ExecutionException 
	{
		logger.setLevel(Level.DEBUG);

		WKTReader wktReader = new WKTReader(new GeometryFactory());
		Geometry geom = wktReader.read("MULTIPOINT (1 1, 5 4, 7 9, 5 5, 2 2)");
		
		NameImpl name = new NameImpl("tutorial","octagonalEnvelope");
        Process process = Processors.createProcess( name );
        logger.debug(process);
        
        ProcessExecutor engine = Processors.newProcessExecutor(2);
		
        Map<String,Object> input = new KVP("geom", geom);
        Progress working = engine.submit( process, input );
		
//        if( working.isCancelled() ){
//            return;
//        }
        
        Map<String,Object> result = working.get(); // get is BLOCKING
        //Geometry octo = (Geometry) result.get("result");
        
        //System.out.println( octo );
        
		
		//Geometry result = ProcessTutorial.octagonalEnvelope( geom );
		//logger.debug(result);
		
		logger.debug("Hallo Welt.");
	}


}
