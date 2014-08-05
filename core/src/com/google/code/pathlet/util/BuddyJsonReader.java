package com.google.code.pathlet.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.pathlet.config.ConfigException;

public class BuddyJsonReader extends BuddyReader {

	private JsonNode rootNode;
	
	public BuddyJsonReader(Class buddyClazz, String fileName) {
		super(buddyClazz, fileName);
	}
	
	public BuddyJsonReader(Class buddyClazz, String fileName, String fileCharset) {
		super(buddyClazz, fileName, fileCharset);
	}
	
	public String get(String path) {
		return this.rootNode.get(path).asText();
	}
	
	protected void parseData() {
		try {
			JsonFactory jsonFactory = new JsonFactory(); // or, for data binding, org.codehaus.jackson.mapper.MappingJsonFactory
			JsonParser jp = jsonFactory.createJsonParser(getFileText());
			jp.enable(Feature.ALLOW_COMMENTS);
			jp.enable(Feature.ALLOW_UNQUOTED_FIELD_NAMES);
			jp.enable(Feature.ALLOW_SINGLE_QUOTES);
			
			ObjectMapper mapper = new ObjectMapper();
			this.rootNode = mapper.readTree(jp);
		} 
		catch (Exception e) {
			throw new ConfigException("Failed to parse json content! ", e);
		} 
	}
	
}
