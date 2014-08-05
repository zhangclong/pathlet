package com.google.code.pathlet.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.pathlet.config.ConfigException;
import com.google.code.pathlet.util.IOUtils;

public class BuddyXmlReader extends BuddyReader {

	private Properties properties;
	
	public BuddyXmlReader(Class buddyClazz, String fileName) {
		super(buddyClazz, fileName);
	}
	
	public BuddyXmlReader(Class buddyClazz, String fileName, String fileCharset) {
		super(buddyClazz, fileName, fileCharset);
	}
	
	public String get(String key) {
		return this.properties.getProperty(key);
	}
	
	protected void parseData() {
		try {
			this.properties = new Properties();
			this.properties.loadFromXML(getFileInputStream());
		} 
		catch (Exception e) {
			throw new ConfigException("Failed to parse XML content! ", e);
		} 
	}
	
}
