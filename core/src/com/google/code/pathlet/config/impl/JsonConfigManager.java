package com.google.code.pathlet.config.impl;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.pathlet.config.ConfigException;
import com.google.code.pathlet.config.ConfigManager;
import com.google.code.pathlet.config.def.AdviceConfig;
import com.google.code.pathlet.config.def.BeanResourceConfig;
import com.google.code.pathlet.config.def.PointcutConfig;
import com.google.code.pathlet.core.Module;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.PathPattern;
import com.google.code.pathlet.core.PathletConstants;
import com.google.code.pathlet.util.ClassUtils;
import com.google.code.pathlet.util.ValueUtils;

public class JsonConfigManager implements ConfigManager {
	
	private Map<Path, BeanResourceConfig> beanConfigMap = new LinkedHashMap<Path, BeanResourceConfig>();
	
	//Map.key is the path of advice, one path may have one or more advice
	private Map<Path, List<AdviceConfig>> adviceConfigPathMap = new LinkedHashMap<Path, List<AdviceConfig>>();
	
	//Map.key is the id of advice
	private Map<String, AdviceConfig> adviceConfigIdMap = new LinkedHashMap<String, AdviceConfig>();
	
	private Map<String, PointcutConfig> pointcutConfigMap = new LinkedHashMap<String, PointcutConfig>();
	
	private Map<String, Module> moduleMap = new LinkedHashMap<String, Module>();
	
	private Map<Path, Path> aliases = new LinkedHashMap<Path, Path>();

	/**
	 * 
	 * @param jsonContents  give one or more json content string, each content must contained the completed JSON definition.
	 * @param jsonContentNames give the jsonContents's name, just for more user friendly exception prompt. Must be same order with jsonContents array.
	 * @param placeholderProperties
	 * @throws ConfigException
	 */
	public JsonConfigManager(String[] jsonContents, String[] jsonContentNames, Properties placeholderProperties) throws ConfigException {
		JsonNode[] rootNodes = new JsonNode[jsonContents.length];
		
		for(int i=0 ; i<jsonContents.length ; i++) {
			String jsonContent = jsonContents[i];
			try {
				String replacedJsonContent = replaceHolder(jsonContent, placeholderProperties);
				
				JsonFactory jsonFactory = new JsonFactory(); // or, for data binding, org.codehaus.jackson.mapper.MappingJsonFactory
				JsonParser jp = jsonFactory.createJsonParser(replacedJsonContent);
				jp.enable(Feature.ALLOW_COMMENTS);
				jp.enable(Feature.ALLOW_UNQUOTED_FIELD_NAMES);
				jp.enable(Feature.ALLOW_SINGLE_QUOTES);
				
				ObjectMapper mapper = new ObjectMapper();
				rootNodes[i] = mapper.readTree(jp);
				
			} 
			catch (Exception e) {
				throw new ConfigException("Failed to parse json content! In JSON content name=\"" + jsonContentNames[i] + "\"\n", e);
			} 
		}
		
		parseRootNode(rootNodes);
	}
	

	private String replaceHolder(String jsonContent, Properties placeholderProperties) {
		if(placeholderProperties != null && placeholderProperties.size() > 0) {
			PropertyPlaceholderHelper holderHelp = new PropertyPlaceholderHelper();
			return holderHelp.replacePlaceholders(jsonContent, placeholderProperties);
		}
		else {
			return jsonContent;
		}
	}
	
	private void parseRootNode(JsonNode[] rootNodes) {
		
		
		//Parse the "modules" node
		for(JsonNode rootNode : rootNodes) {
			JsonNode modulesNode = rootNode.get("modules");
			if(modulesNode != null) {
				int size = modulesNode.size();
				for(int i=0 ; i<size ; i++) {
					JsonNode moduleNode = modulesNode.get(i);
					parseModuleNode(moduleNode);
				}
			}
		}
		
		//Parse the "beans" node 
		for(JsonNode rootNode : rootNodes) {
			JsonNode beansNode = rootNode.get("beans");
			if(beansNode != null) {
				int beansSize = beansNode.size();
				for(int i=0 ; i<beansSize ; i++) {
					JsonNode beanNode = beansNode.get(i);
					parseBeanNode(beanNode);
				}
			}
		}
		
		//Parse the "pointcuts" node
		for(JsonNode rootNode : rootNodes) {
			JsonNode pointcutsNode = rootNode.get("pointcuts");
			if(pointcutsNode != null) {
				int size = pointcutsNode.size();
				for(int i=0 ; i<size ; i++) {
					JsonNode pointcutNode = pointcutsNode.get(i);
					parsePointcutNode(pointcutNode);
				}
			}
		}

		//Parse the "advices" node
		for(JsonNode rootNode : rootNodes) {
			JsonNode advicesNode = rootNode.get("advices");
			if(advicesNode != null) {
				int size = advicesNode.size();
				for(int i=0 ; i<size ; i++) {
					JsonNode adviceNode = advicesNode.get(i);
					parseAdviceNode(adviceNode);
				}
			}
		}
		
		//Parse the "aliases" node
		for(JsonNode rootNode : rootNodes) {
			JsonNode aliasesNode = rootNode.get("aliases");
			if(aliasesNode != null) {
				int size = aliasesNode.size();
				for(int i=0 ; i<size ; i++) {
					JsonNode aliasNode = aliasesNode.get(i);
					parseAliasNode(aliasNode);
				}
			}
		}
		
	}
	
	private void parseModuleNode(JsonNode moduleNode) {
		try {
			String id = moduleNode.get("id").asText().trim();
			String clazz = moduleNode.path("class").asText().trim();
			JsonNode classPathsNode = moduleNode.get("classPaths");
			
			if(ValueUtils.isEmpty(clazz)) {
				clazz = PathletConstants.DEFAULT_MODULE_CLASS;
			}
			
			JsonNode autoReloadNode = moduleNode.path("autoReload");
			boolean autoReload = false;
			if(autoReloadNode != null) {
				autoReload = autoReloadNode.asBoolean();
			}
	
			int size = classPathsNode.size();
			URL[] classPaths = new URL[size];
			for(int i=0 ; i<size ; i++) {
				JsonNode classPathNode = classPathsNode.get(i);
				String classPath = classPathNode.asText().trim();
				classPaths[i] = new File(classPath).toURI().toURL();
			}
			
			PathPattern pathPatternNode = JsonParseHelper.parsePathPattern(moduleNode.get("resourcePattern"));
			
			JsonNode listenerNode = moduleNode.get("listener");

			Class<?> moduleClazz = ClassUtils.getDefaultClassLoader().loadClass(clazz);
			Module module = (Module)moduleClazz.newInstance();
			module.setId(id);
			module.setClassPaths(classPaths);
			module.setAutoReload(autoReload);
			module.setResourcePattern(pathPatternNode);
			module.setListener((listenerNode == null) ? null : listenerNode.asText());
			
			moduleMap.put(id, module);
		} 
		catch (Exception e) {
			throw new ConfigException("Error configuration! Failed to parse the module node: " + moduleNode);
		} 
		
	}
	
	
	private void parsePointcutNode(JsonNode pointcutNode) {
		String id = pointcutNode.path("id").asText().trim();
		if(ValueUtils.isEmpty(id)) {
			throw new ConfigException("Error configuration value! 'id' property could not be empty in pointcut node: " + pointcutNode);
		}
		if(pointcutConfigMap.containsKey(id)) {
			throw new ConfigException("Duplicated define the pointcut node as same id='" + id + "' !");
		}
		
		JsonNode scopesNode = pointcutNode.get("scopes");
		String[] scopes = null;
		if(scopesNode != null) {
			scopes = scopesNode.asText().trim().split(",");
		}
		else {
			scopes = new String[0];
		}
		
		JsonNode pathPatternNode = pointcutNode.get("pathPattern");
		PathPattern pathPattern = JsonParseHelper.parsePathPattern(pathPatternNode);
		
		String methodsStr = pointcutNode.path("methods").asText();
		String[] methods = null;
		if(ValueUtils.notEmpty(methodsStr)) {
			methods = methodsStr.trim().split(",");
		}
		else {
			throw new ConfigException("Error configuration value!  \"methods\" property could not be empty in pointcut node: " + pointcutNode);
		}

		PointcutConfig pointcutConfig = new PointcutConfig(id, scopes, pathPattern, methods);
		pointcutConfigMap.put(id, pointcutConfig);
	}
	
	private void parseAdviceNode(JsonNode adviceNode) {
		String id = adviceNode.get("id").asText().trim();
		String advisorPathStr = adviceNode.get("advisorPath").asText().trim();
		String pointcutId = adviceNode.get("pointcutId").asText().trim();
		String adviceMethod = adviceNode.get("adviceMethod").asText().trim();
		
		
		if(adviceConfigIdMap.containsKey(id)) {
			throw new ConfigException("Duplicated define the advice node as same id='" + id + "' !");
		}
		
		PointcutConfig pointcutConfig = pointcutConfigMap.get(pointcutId);
		if(pointcutConfig == null) {
			throw new ConfigException("Failed to found the reference pointcut note for advice node: '" + adviceNode + "' by pointcutId=" + pointcutId);
		}
		
		Path advisorPath = new Path(advisorPathStr);
		
		//Create the new AdviceConfig
		AdviceConfig config = new AdviceConfig(id, advisorPath, adviceMethod, pointcutConfig);
		
		//Add the new AdviceConfig into id indexed map
		adviceConfigIdMap.put(id, config);
		
		//Add the new AdviceConfig into path indexed map
		List<AdviceConfig> advices = adviceConfigPathMap.get(advisorPath);
		if(advices == null) {
			advices = new ArrayList<AdviceConfig>();
			adviceConfigPathMap.put(advisorPath, advices);
		}
		advices.add(config);
	}
	
	
	private void parseBeanNode(JsonNode beanNode) {
		BeanResourceConfig beanConfig = new BeanResourceConfig();
		
		beanConfig.setPath(new Path(beanNode.get("path").asText()));
		
		//If has no indicated property, it will return String("");
		//So the scope, class, initMethod, destroy
		beanConfig.setClassName(beanNode.path("class").asText());
		beanConfig.setScope(beanNode.path("scope").asText());
		beanConfig.setInitMethod(beanNode.path("initMethod").asText());
		beanConfig.setDestroyMethod(beanNode.path("destroyMethod").asText());
		JsonNode initInstanceNode = beanNode.get("initInstance");
		if(initInstanceNode != null) {
			beanConfig.setInitInstance(initInstanceNode.asBoolean());
		}
		
		//Set properties 
		JsonNode propertiesNode = beanNode.get("properties");
		if(propertiesNode != null && propertiesNode.size() > 0) {
			beanConfig.setProperties(propertiesNode);
		}

		if(beanConfigMap.containsKey(beanConfig.getPath())) {
			throw new ConfigException("Duplication defined the beans config for path:" + beanConfig.getPath());
		}
		
		beanConfigMap.put(beanConfig.getPath(), beanConfig);
	}
	
	private void parseAliasNode(JsonNode aliasNode) {
		Path from = new Path(aliasNode.get("from").asText().trim());
		Path to = new Path(aliasNode.get("to").asText().trim());

		
		if(aliases.containsKey(to)) {
			throw new ConfigException("Duplication defined alias: to=" + to + ", from=" + from + "");
		}
		
		aliases.put(to, from);
	}
	
	public Collection<Module> getModules() {
		return moduleMap.values();
	}
	
	public Module getModule(String id) {
		return moduleMap.get(id);
	}

	public BeanResourceConfig getBeanConfig(Path path) {
		return beanConfigMap.get(path);
	}

	public Collection<BeanResourceConfig> getBeanConfigs() {
		return beanConfigMap.values();
	}

	public PointcutConfig getPointcut(String id) {
		return pointcutConfigMap.get(id);
	}

	public AdviceConfig getAdice(String id) {
		return adviceConfigIdMap.get(id);
	}

	public List<AdviceConfig> getAdices(Path path) {
		return adviceConfigPathMap.get(path);
	}

	public Map<Path, List<AdviceConfig>> getAdvices() {
		return adviceConfigPathMap;
	}

	public Map<Path, Path> getAliases() {
		return aliases;
	}

	
}
