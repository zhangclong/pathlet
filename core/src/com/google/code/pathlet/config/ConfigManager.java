package com.google.code.pathlet.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.code.pathlet.config.def.AdviceConfig;
import com.google.code.pathlet.config.def.BeanResourceConfig;
import com.google.code.pathlet.config.def.PointcutConfig;
import com.google.code.pathlet.core.Module;
import com.google.code.pathlet.core.Path;

public interface ConfigManager {
	
	public Module getModule(String id);
	
	public Collection<Module> getModules();

	public BeanResourceConfig getBeanConfig(Path path);
	
	/**
	 * Get all bean's configurations.
	 * @return
	 */
	public Collection<BeanResourceConfig> getBeanConfigs();
	
	public PointcutConfig getPointcut(String id);
	
	public AdviceConfig getAdice(String id);
	
	/**
	 * Get list of adivces against one path.
	 * @param path
	 * @return
	 */
	public List<AdviceConfig> getAdices(Path path);
	
	/**
	 * Get all advices configuration.
	 * @return the key is the advisor path, value is the AdviceConfig(s) that use this advisor.
	 */
	public Map<Path, List<AdviceConfig>> getAdvices();
	
	/**
	 * Get aliases configuration.
	 * @return
	 */
	public Map<Path, Path> getAliases();

	
}