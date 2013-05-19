package org.catais.brw.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ReadProperties {
	private static Logger logger = Logger.getLogger(ReadProperties.class);
	
	String fileName = null;
	
    public ReadProperties( String fileName ) {
		logger.setLevel(Level.DEBUG);
		
    	this.fileName = fileName;
    }
    
    public HashMap read() throws FileNotFoundException, IOException {    	
		Properties properties = new Properties();
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(fileName));
		properties.load(stream);
		stream.close();

    	HashMap params = new HashMap();

		// Database parameters
    	String host = properties.getProperty("dbhost");
    	if (host != null) {
        	params.put("dbhost", host.trim());
    	} else {
			throw new IllegalArgumentException("'dbhost' parameter not set.");
		}
		logger.debug("dbhost: " + host);
	
    	String port = properties.getProperty("dbport");
    	if (port != null) {
        	params.put("dbport", port.trim());
    	} else {
			throw new IllegalArgumentException("'dbport' parameter not set.");
		}
		logger.debug("dbport: " + port);		
		
    	String dbname = properties.getProperty("dbname");
    	if (dbname != null) {
        	params.put("dbname", dbname.trim());
    	} else {
			throw new IllegalArgumentException("'dbname' parameter not set.");
		}
		logger.debug("dbname: " + dbname);		
		
    	String schema = properties.getProperty("dbschema");
    	if (schema != null) {
        	params.put("dbschema", schema.trim());
    	} else {
			throw new IllegalArgumentException("'dbschema' parameter not set.");
		}
		logger.debug("dbschema: " + schema);		
		
    	String user = properties.getProperty("dbuser");
    	if (user != null) {
        	params.put("dbuser", user.trim());
    	} else {
			throw new IllegalArgumentException("'dbuser' parameter not set.");
		}
    	logger.debug("dbuser: " + user);		
		
    	String pwd = properties.getProperty("dbpwd");
    	if (pwd != null) {
        	params.put("dbpwd", pwd.trim());
    	} else {
			throw new IllegalArgumentException("'dbpwd' parameter not set.");
		} 
    	logger.debug("dbpwd: " + pwd);		

    	String admin = properties.getProperty("dbadmin");
    	if (admin != null) {
        	params.put("dbadmin", admin.trim());
    	} else {
			throw new IllegalArgumentException("'dbadmin' parameter not set.");
		}
    	logger.debug("dbadmin: " + admin);		
		
    	String adminpwd = properties.getProperty("dbadminpwd");
    	if (adminpwd != null) {
        	params.put("dbadminpwd", adminpwd.trim());
    	} else {
			throw new IllegalArgumentException("'dbadminpwd' parameter not set.");
		}
    	logger.debug("dbadminpwd: " + adminpwd);	
    	
    	// source directory
    	String srcdir = properties.getProperty("srcdir");
    	if (srcdir != null) {
    		params.put("srcdir", srcdir.trim());
    	} else {
			throw new IllegalArgumentException("'srcdir' parameter not set.");
		}
    	
    	// reference frame
    	String frame = properties.getProperty("frame");
    	if (frame != null) {
    		params.put("frame", frame.trim());
    	} else {
			throw new IllegalArgumentException("'frame' parameter not set.");
		}
    	
    	return params;
    }

}


