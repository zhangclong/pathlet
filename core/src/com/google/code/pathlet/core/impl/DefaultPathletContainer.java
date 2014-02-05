package com.google.code.pathlet.core.impl;

import static com.google.code.pathlet.core.PathletConstants.CONTAINER_SCOPE;
import static com.google.code.pathlet.core.PathletConstants.DEFAULT_SCOPES;
import static com.google.code.pathlet.core.PathletConstants.SETTINGS_FACTORY_PATH;
import static com.google.code.pathlet.core.PathletConstants.SETTING_SCOPE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.code.pathlet.config.ConfigException;
import com.google.code.pathlet.config.ConfigInstanceEvent;
import com.google.code.pathlet.config.ConfigManager;
import com.google.code.pathlet.config.ResourceConfig;
import com.google.code.pathlet.config.def.AdviceConfig;
import com.google.code.pathlet.config.def.BeanResourceConfig;
import com.google.code.pathlet.core.InstanceSpace;
import com.google.code.pathlet.core.Module;
import com.google.code.pathlet.core.ModuleManager;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.PathClassLoader;
import com.google.code.pathlet.core.PathPattern;
import com.google.code.pathlet.core.PathletContainer;
import com.google.code.pathlet.core.Resource;
import com.google.code.pathlet.core.ResourceFactory;
import com.google.code.pathlet.core.ResourceInstanceEvent;
import com.google.code.pathlet.core.SpaceInstanceEvent;
import com.google.code.pathlet.core.exception.ResourceDuplicationException;
import com.google.code.pathlet.core.exception.ResourceNotFoundException;

public class DefaultPathletContainer implements PathletContainer {
	
	private ConfigManager configManager;
	
	private ModuleManager moduleManager = null;
	
	private String[] scopes;
	
	private PathClassLoader pathClassLoader;
	
	//Default container PathClassLoader, this will use the current ClassLoader to load class. 
	//private PathClassLoader defaultPathletClassLoader = null;
	
	private InstanceSpace settingSpace = null;
	
	private InstanceSpace containerSpace = null;

	private Map<Path, ResourceFactory> factoriesMap = null;
	
	private Map<Path, Resource> resourceCache = null;
	
	private List<ResourceInstanceEvent> instanceEvents = null;
	
	private List<SpaceInstanceEvent> spaceInstanceEvents = null;
	
	//每一个InstanceSpace实例会绑定一个对象, 为了能够快算定位到不同对象和生命周期绑定的InstanceSapce,
	//此属性设计为两级式的Map对象: 
	//  第一级的key为scope name, 第一级的value是在这个scope下的对象和InstanceSpace的Map对象。
	//  第二级即第一级的value中存放的Map对象：key为绑定对象，value为InstanceSpace实例。
	//如：在web应用中我们会对每个ServletRequest绑定一个InstanceSpace
	private Map<String, Map<Object, InstanceSpace>> scopeSpaceMap = null;
	
	//Store the alias mapping. Map from the Map.key to Map.value
	private Map<Path, Path> aliases = null;
	
	//Store the paths from the bean configure property "initInstance" 
	//The key is the scope name, value is the list of paths of the "initInstance" beans.
	private Map<String, List<Path>> initInstancePaths = null;
	
	public DefaultPathletContainer(ConfigManager cfgManager) {
		this(cfgManager, DEFAULT_SCOPES);
	}
	
	public DefaultPathletContainer(ConfigManager cfgManager, String[] scopes) {
		this.configManager = cfgManager; 
		this.scopes = scopes;
		checkScopes(scopes);

		this.resourceCache = new HashMap<Path, Resource>();
		this.factoriesMap =  new LinkedHashMap<Path, ResourceFactory>();
		this.instanceEvents =  new ArrayList<ResourceInstanceEvent>();
		this.spaceInstanceEvents = new ArrayList<SpaceInstanceEvent>();
		this.pathClassLoader = new SimplePathClassLoader();
		
		//Set create the ModuleManager, if has the modules settings. 
		if(configManager.getModules() != null && configManager.getModules().size() > 0) {
			this.moduleManager = new DefaultModuleManager(configManager.getModules(), this);
			this.pathClassLoader = moduleManager.getPathClassLoader();
			this.spaceInstanceEvents.add(moduleManager.getSpaceInstanceEvent());
		}
		
		//Add the default ResourceInstanceEvent objects.
		//this.instanceEvents.add(moduleManager.getResourceInstanceEvent());
		this.instanceEvents.add(new AopInstanceEvent(this.configManager));
		this.instanceEvents.add(new ConfigInstanceEvent());
		
		//Initialize the scopeSpaceMap subordinate Map(s): Map<Object, InstanceSpace>.
		//Each scope will create a Map<Object, InstanceSpace> and put into the scopeSpaceMap.
		this.scopeSpaceMap = new LinkedHashMap<String, Map<Object, InstanceSpace>>(scopes.length);
		for(String scope : scopes) {
			Map<Object, InstanceSpace> spaceBindingMap = new LinkedHashMap<Object, InstanceSpace>();
			this.scopeSpaceMap.put(scope, spaceBindingMap);
		}
		
		
		this.aliases = configManager.getAliases(); //The aliases should be initialized before the creators creation
		Collection<BeanResourceConfig> beanConfigs = configManager.getBeanConfigs();
		

		//Initialize the settingSpace, which is the InstanceSpace to create all ResourceFactory(s)
		initSettingSpace(beanConfigs);
		
		//Skip the initInstance process, because the initInstancePaths has not initialized yet. 
		//Create the default and single container InstanceSpace and set the parent as the settingSpace, 
		//and bind the container with containerSpace
		this.containerSpace = new DefaultInstanceSpace(this.settingSpace, CONTAINER_SCOPE, this, this.spaceInstanceEvents);
		scopeSpaceMap.get(CONTAINER_SCOPE).put(this, this.containerSpace); 

		//Store the bean configure paths, which contains the property initInstance=true. 
		this.initInstancePaths = new HashMap<String, List<Path>>();
		for(BeanResourceConfig beanConfig : beanConfigs) {
			Path factoryPath = beanConfig.getPath();
			
			if(beanConfig.isInitInstance()) {
				String scope = beanConfig.getScope();
				List<Path> paths = initInstancePaths.get(scope);
				if(paths == null) {
					paths = new ArrayList<Path>();
					initInstancePaths.put(scope, paths);
				}
				
				paths.add(beanConfig.getPath());
			}
		}
		
		//Instance the advisor one by one from the configManager.
		Map<Path, List<AdviceConfig>> advices = configManager.getAdvices();
		if(advices != null) {
			for(Path advicePath : advices.keySet()) {
				Object advisorObject = getInstance(advicePath);
				if(advisorObject == null) {
					throw new ConfigException("Failed to load instance the to advice path='" + advicePath + "', Please check your advice configuration!");
				}
			}
		}
		
		//Instance the "initInstance" property beans, the getInstance(Path) method will be invoke to instance it.
		toInitInstance(this.containerSpace);
		
		//Start all modules, if any exists.
		if(moduleManager != null) {
			Collection<Module> modules = moduleManager.getModules();
			for(Module module : modules) {
				moduleManager.startModule(module.getId());
			}
		}
		
	}
	
	protected void initSettingSpace(Collection<BeanResourceConfig> beanConfigs) {
		
		//Create a BeanResourceFactory for settings.
		//It is a temporary ResourceFacotry, will be remove after the settingSpace has been completely created.
		BeanResourceFactory settingsResourceFactory = new BeanResourceFactory();
		settingsResourceFactory.setContainer(this);
		settingsResourceFactory.setDefaultScope(SETTING_SCOPE);
		factoriesMap.put(SETTINGS_FACTORY_PATH, settingsResourceFactory);
		
		//Create the setting space, and bind the container with settingSpace
		this.settingSpace = new DefaultInstanceSpace(null, SETTING_SCOPE, this, this.spaceInstanceEvents);
		scopeSpaceMap.get(SETTING_SCOPE).put(this, this.settingSpace); 
		
		//Initialize ResourceFacotory(s) from configManager
		for(BeanResourceConfig beanConfig : beanConfigs) {
			
			//Find the ResourceFactory(s) beans and add them into factoriesMap
			if(SETTING_SCOPE.equals(beanConfig.getScope())) {
				BeanResource settingResource = (BeanResource)getResource(beanConfig.getPath());
				
				if(ResourceFactory.class.isAssignableFrom(settingResource.getBeanClazz())) {
	 				ResourceFactory factory = (ResourceFactory)settingSpace.getInstance(beanConfig.getPath());
					factory.setContainer(this);
					factoriesMap.put(beanConfig.getPath(), factory);
				}
			}
		}
		
		factoriesMap.remove(SETTINGS_FACTORY_PATH);
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}
	
	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	public String[] getScopes() {
		return scopes;
	}

	public PathClassLoader getPathClassLoader() {
		return pathClassLoader;
	}

	public Map<Path, Path> getAliases() {
		return aliases;
	}

	public synchronized Resource getResource(Path resourcePath) {
		Resource resource = resourceCache.get(resourcePath);
		boolean beCached = true;
		if(resource == null) {
			beCached = false;
			resource = fetchResource(resourcePath);
			if(resource != null) {
				resourceCache.put(resourcePath, resource);
			}
			else {
				throw new ResourceNotFoundException("Failed to find resource path=" + resourcePath, resourcePath);
			}
		}
		
		return resource;
	}
	
	public synchronized void addResource(Resource resource) throws ResourceDuplicationException {

		if(resourceCache.containsKey(resource.getPath())) {
			throw new ResourceDuplicationException("Already existing resoure in same path!", resource.getPath());
		}
		
		resourceCache.put(resource.getPath(), resource);
	}

	public List<ResourceInstanceEvent> getInstanceEvents() {
		return instanceEvents;
	}

	public void flush(PathPattern matchPattern) {
		//getResource method will be invoked in flushContexts, so context must flush before the resources flush.
		flushSpaces(matchPattern);
		flushResources(matchPattern);
	}
	
	protected synchronized void flushResources(PathPattern matchPattern) {
		//Flush it's own cache for Reourses
		//To avoid the ConcurrentModificationException, we should do removing operations by following two step:
		//1 Find the match keys to be removed;
		Set<Path> keys = resourceCache.keySet();
		List<Path> removingKeys = new ArrayList<Path>();
		if(matchPattern != null) {
			for(Path key : keys) {
				if(matchPattern.isMatch(key)) {
					removingKeys.add(key);
				}
			}
		}
		else {
			for(Path key : keys) {
				removingKeys.add(key);
			}
		}
		
		//2.Actually remove the values from targetCache
		if(removingKeys.size() > 0) {
			for(Path removingKey : removingKeys) {
				resourceCache.remove(removingKey);
			}
		}
	}
	
	/**
	 * Check the scopes argument, which must contains two elements "setting" and "container".
	 * @param scopes
	 * @throws ConfigException
	 */
	protected void checkScopes(String[] scopes) throws ConfigException {
		boolean settingScopeFound = false;
		boolean containerScopeFound = false;
		for(String scope : scopes) {
			if(SETTING_SCOPE.equals(scope)) {
				settingScopeFound = true;
			}
			else if(CONTAINER_SCOPE.equals(scope)) {
				containerScopeFound = true;
			}
			
			if(settingScopeFound == true && containerScopeFound == true) {
				break;
			}
		}
		
		if(settingScopeFound == false) {
			throw new ConfigException("The scope \"" + SETTING_SCOPE + "\" must contains in the DefaultPathletContainer constructor scopes agruments!");
		}
		
		if(containerScopeFound == false) {
			throw new ConfigException("The scope \"" + CONTAINER_SCOPE + "\" must contains in the DefaultPathletContainer constructor scopes agruments!");
		}
	}
	
	protected void flushSpaces(PathPattern matchPattern) {
		synchronized(scopeSpaceMap) {
			for(Map.Entry<String, Map<Object, InstanceSpace>> entry : scopeSpaceMap.entrySet()) {

				//Flush the InstanceEvent, clear the data cached in InstanceEvent
				for(ResourceInstanceEvent instanceEvent : instanceEvents) {
					instanceEvent.flush(entry.getKey(), matchPattern);
				}
				
				for(InstanceSpace space : entry.getValue().values()) {
					space.flush(matchPattern);
				}
			}
		}
	}
	
	/**
	 * Actually get resource from sub ResourceFactory.
	 */
	private Resource fetchResource(Path fullPath) {
        for(ResourceFactory creator : factoriesMap.values()) {
        	if(creator.getPathPattern().isMatch(fullPath)) {
        		//If found the ResourceConfig for this path, create the new Resource by ResourceConfig.
        		ResourceConfig resourceConfig = configManager.getBeanConfig(fullPath);
        		if(resourceConfig != null) {
	        		return creator.createResource(resourceConfig);
        		}
        		else {
	        		Resource resource = creator.createResource(fullPath);
	        		if(resource != null) {
	        			return resource;
	        		}
        		}
        	}
        }
        return null;
	}
	
	public Object getInstance(Path path) {
		return getContainerSpace().getInstance(path);
	}

	public Object getInstance(String path) {
		return getContainerSpace().getInstance(new Path(path));
	}

	public Object getInstance(String scope, Object bindObject, Path path) {
		return getSpace(scope, bindObject, true).getInstance(path);
	}

	public Object getInstance(String scope, Object bindObject, String path) {
		return getSpace(scope, bindObject, true).getInstance(new Path(path));
	}
	

	public InstanceSpace getSpace(String scope, Object bindingObject, boolean create) {
		synchronized(scopeSpaceMap) {
			Map<Object, InstanceSpace> spaces = scopeSpaceMap.get(scope);
			InstanceSpace space = spaces.get(bindingObject);
			
			//Create new InstanceSpace if the existent one could not be found.
			if(create == true && space == null) {
				space = new DefaultInstanceSpace(containerSpace, scope, this, this.spaceInstanceEvents);
				spaces.put(bindingObject, space);
				
				//Instance the "initInstance" property beans
				toInitInstance(space);
			}
			
			return space;
		}
	}
	

	/**
	 * Instance the "initInstance" property beans
	 */
	private void toInitInstance(InstanceSpace space) {
		List<Path> initPaths = initInstancePaths.get(space.getScope());
		if(initPaths != null && initPaths.size() > 0) {
			for(Path p : initPaths) {
				space.getInstance(p);
			}
		}
		
	}

	public void destroySpace(String scope, Object bindingObject) {
		synchronized(scopeSpaceMap) {
			Map<Object, InstanceSpace> spaces = scopeSpaceMap.get(scope);
			InstanceSpace space = spaces.get(bindingObject);
			space.flush(null);
			spaces.remove(bindingObject);
		}
	}

	public InstanceSpace getContainerSpace() {
		return containerSpace;
	}
	
	

	public InstanceSpace getSettingSpace() {
		return settingSpace;
	}

	public void destroy() {
		flush(null);
	}
	

}
