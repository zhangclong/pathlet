package com.google.code.pathlet.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import com.google.code.pathlet.config.def.AdviceConfig;
import com.google.code.pathlet.config.def.PointcutConfig;
import com.google.code.pathlet.core.ConfigManagerAccessor;
import com.google.code.pathlet.core.Module;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.util.ClassPathResource;
import com.google.code.pathlet.util.ClassUtils;

/**
 * <p>Test the {@link com.google.code.pathlet.core.ConfigManager} implementations. The target is 
 * to validate the loading and parsing for the configuration files.
 * By now, Pathlet only supports the JSON type configuration file, which could be parameterized by properties files!</p> 
 * 
 * 
 * @author Charlie Zhang
 * @see com.google.code.pathlet.core.ConfigManagerAccessor
 * 
 */
public class ConfigManagerTest {

	@Test
	public void test() throws Exception {
		ClassLoader cl = this.getClass().getClassLoader();
		String packagePath = ClassUtils.getPackageName(this.getClass()).replace('.', '/');
		//InputStream jsonIn = new ClassPathResource(packagePath + "/testConfig.json", cl).getInputStream();
		//InputStream propertiesIn = new ClassPathResource(packagePath + "/testConfig.properties", cl).getInputStream();
		//String jsonContent = IOUtils.toString(jsonIn, "UTF-8");
		//Properties placeholderProperties = new Properties();
		//placeholderProperties.load(propertiesIn);
		//ConfigManager configManager = new JsonConfigManager(jsonContent, placeholderProperties);
		
		File[] configFiles ={ 
				(new ClassPathResource(packagePath + "/testConfig1.json", cl)).getFile(), 
				(new ClassPathResource(packagePath + "/testConfig2.json", cl)).getFile() };
		
		File[] propertyFiles = {(new ClassPathResource(packagePath + "/testConfig.properties", cl)).getFile()};

		ConfigManagerAccessor configManagerAcc = new ConfigManagerAccessor(configFiles, propertyFiles, "UTF-8");
		
		ConfigManager configManager = configManagerAcc.loadConfigManager();
		
		Collection<Module> modules = configManager.getModules();
		assertEquals(modules.size(), 2);
		Iterator<Module> it = modules.iterator();

		Module module1 = it.next();
		assertEquals(module1.getId(), "config");
		assertEquals(module1.getClassPaths().length, 1);
		assertTrue(module1.getClassPaths()[0].toString().startsWith("file:"));
		assertEquals(module1.getResourcePattern().getIncludes()[0], "/config/**/*");
		assertEquals(module1.getResourcePattern().getIncludes().length, 1);
		assertEquals(module1.isAutoReload(), true);
		File module1file = new File(module1.getClassPaths()[0].toURI());
		assertEquals(module1file.getCanonicalFile().getName(), "target");
		
		Module module2 = configManager.getModule("party");
		assertEquals(module2.getId(), "party");
		assertEquals(module2.isAutoReload(), false);
		
		Collection<AdviceConfig> adviceCfg = configManager.getAdices(new Path("/interceptor/ConfigInterceptor"));
		AdviceConfig advice0 = adviceCfg.iterator().next();
		
		assertEquals(advice0.getAdvisorPath().toString(), "/interceptor/ConfigInterceptor");
		assertEquals(advice0.getAdviceMethod(), "aroundOuter");
		
		assertNotNull(advice0.getPointcut());
		
		PointcutConfig pointcutCfg = advice0.getPointcut();
		assertEquals(pointcutCfg.getId(),"pointCutUserService");
		
		//If has not scopes in cutpoint configuration, isMatchByScope will always return true.
		assertTrue(pointcutCfg.isMatchByScope("anyScope"));
		
		assertEquals(pointcutCfg.getPathPattern().getIncludes().length, 1);
		assertEquals(pointcutCfg.getPathPattern().getIncludes()[0], "/**/UserService");
		
		assertEquals(pointcutCfg.getPathPattern().getExcludes().length, 0);
		
		assertEquals(pointcutCfg.getMethods().length, 1);
		assertEquals(pointcutCfg.getMethods()[0], "getUser");
		
		
		pointcutCfg = configManager.getPointcut("pointCutCategoryService");
		assertEquals(pointcutCfg.getId(),"pointCutCategoryService");
		
		assertEquals(pointcutCfg.getScopes()[0], "container");
		assertEquals(pointcutCfg.getScopes()[1], "request");
		assertTrue(pointcutCfg.isMatchByScope("request"));
		assertFalse(pointcutCfg.isMatchByScope("notascope"));
		
		assertEquals(pointcutCfg.getPathPattern().getIncludes().length, 2);
		assertEquals(pointcutCfg.getPathPattern().getIncludes()[0], "/**/CategoryService");
		assertEquals(pointcutCfg.getPathPattern().getIncludes()[1], "/**/MenuItemService");
		
		assertEquals(pointcutCfg.getPathPattern().getExcludes().length, 1);
		assertEquals(pointcutCfg.getPathPattern().getExcludes()[0], "/**/UserService");
		
		assertEquals(pointcutCfg.getMethods().length, 4);
		assertEquals(pointcutCfg.getMethods()[0], "save*");
		assertEquals(pointcutCfg.getMethods()[1], "update*");
		assertEquals(pointcutCfg.getMethods()[2], "insert*");
		assertEquals(pointcutCfg.getMethods()[3], "delete*");
		
		
		assertEquals(configManager.getAliases().size(), 2);
		assertEquals(configManager.getAliases().get(new Path("/bean2")), new Path("/TestWeaveBean2"));
		assertEquals(configManager.getAliases().get(new Path("/bean")), new Path("/TestWeaveBean"));
		
	}

}
