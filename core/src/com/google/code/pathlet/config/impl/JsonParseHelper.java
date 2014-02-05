package com.google.code.pathlet.config.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.code.pathlet.config.ConfigException;
import com.google.code.pathlet.core.PathPattern;
import com.google.code.pathlet.util.ValueUtils;

public class JsonParseHelper {
	
	public static PathPattern parsePathPattern(JsonNode jsonNode) throws ConfigException {
		JsonNode includesNode = jsonNode.get("includes");
		JsonNode excludesNode = jsonNode.get("excludes");
		if(includesNode == null || includesNode.isNull() || ValueUtils.isEmpty(includesNode.asText())) {
			throw new ConfigException("Could not found the property 'includes' in json node: '" + jsonNode.toString() + "'");
		}
		
		String[] includes = includesNode.asText().split(",");
		String[] excludes;
		if(excludesNode != null && ValueUtils.notEmpty(excludesNode.asText())) {
			excludes = excludesNode.asText().split(",");
		}
		else {
			excludes = new String[0];
		}
		
		return new PathPattern(includes, excludes);
	}
	
}
