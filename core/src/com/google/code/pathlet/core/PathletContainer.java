package com.google.code.pathlet.core;

import java.util.List;
import java.util.Map;

import com.google.code.pathlet.config.ConfigManager;
import com.google.code.pathlet.core.exception.ResourceDuplicationException;
import com.google.code.pathlet.core.exception.ResourceException;
import com.google.code.pathlet.core.impl.BeanPathConverter;

/**
 * TODO 主要改动的细想: 去掉PathletContainer对外的配置接口， 其主要职能是存取resource和 instance
 *      配置项的信息可都通过 ConfigManager 进行插拔处理。
 * TODO 一般构造函数构造时直接初始化， 不用在调用 init函数，也就是取消init函数。
 * TODO 加入 alias功能
 * 
 * 
 * 1. Contained all <code>ResourceFactory</code> objects. 
 * 2. Provide the filter mechanism to located different <code>ResourceFactory</code> against different path.
 * 3. Cache all resources which be retrieved by getResource method.
 * 4. 
 * 
 * @author Charlie Zhang
 *
 */
public interface PathletContainer {
	
	/**
	 * Get alias map object. Map.key is the "to" property, May.value is the "from" property
	 * @return
	 */
	public Map<Path, Path> getAliases();
	
	public ConfigManager getConfigManager();
	
	public ModuleManager getModuleManager();
	
	public String[] getScopes();
	
	/**
	 * PathClassLoader is readonly property. It could not be changed after the PathletContainer construct.
	 * @return
	 */
	public PathClassLoader getPathClassLoader();
	
	/**
	 * Add <code>Resource</code> directory into this <code>ResourceFactory</code>.
	 * <code>ResourceFactory</code> will cache this added <code>Resource</code>.
	 * 
	 * 
	 * @param resource
	 * @throws ResourceDuplicationException when exists a resource in same path, this exception will be thrown.
	 */
	public void addResource(Resource resource) throws ResourceDuplicationException;
	
	
	/**
	 * Get Resource by path
	 * 
	 * @param fullPath
	 * @return
	 * @throws ResourceException
	 */
	public Resource getResource(Path fullPath) throws ResourceException;

	public List<ResourceInstanceEvent> getInstanceEvents();
	
	/**
	 * Get target by path.
	 * The corresponding target object will be cached after first invoke this method by corresponding path. 
	 * Equals to invoke method: getTarget(path, true, false)
	 */
	public Object getInstance(Path path);
	
	public Object getInstance(String path);
	
	
	public Object getInstance(String scope, Object bindObject, Path path);
	
	public Object getInstance(String scope, Object bindObject, String path);
	
	
	/**
	 * 
	 * Flush the matched resource in cache and targets in all contexts cache.
	 * FIXME Please think about the multithread environments, and be verify the multiple threads resource synchronizing.
	 * 
	 * @param mathPattern
	 */
	public void flush(PathPattern matchPattern);
	
	
	public InstanceSpace getSpace(String scope, Object bindingObject, boolean create);
	
	
	/**
	 * Container has its own InstanceSapce, which will create and destroy with the container create and destroy.
	 * @return
	 */
	public InstanceSpace getContainerSpace();
	
	/**
	 * Setting's InstanceSapce, all the setting beans will created in this space.
	 * Setting bean includes: {@link ResourceFactory}, {@link BeanPathConverter}.
	 * @return
	 */
	public InstanceSpace getSettingSpace();
	
	/**
	 * Destroy the InstanceSapce which be bound with the "boundObject"
	 * @param scope
	 * @param boundObject
	 */
	public void destroySpace(String scope, Object boundObject);
	
	/**
	 * Destroy the container.
	 */
	public void destroy();


}
