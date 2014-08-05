package com.google.code.pathlet.core;

import java.io.File;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import com.google.code.pathlet.core.instanceroot.TestWeaveBean;
import com.google.code.pathlet.core.instanceroot.common.DataInitService;
import com.google.code.pathlet.core.instanceroot.menu.CategoryService;
import com.google.code.pathlet.core.instanceroot.user.User;
import com.google.code.pathlet.core.instanceroot.user.UserService;
import com.google.code.pathlet.util.ClassPathResource;
import com.google.code.pathlet.util.ClassUtils;
import com.google.code.pathlet.util.StaticContainerHelper;

public class JsonConfigResourceTest 
    extends TestCase
{
	
	private final static String REQUEST_SCOPE = "request";
	
	public JsonConfigResourceTest() throws Exception  {
		String packagePath = ClassUtils.getPackageName(this.getClass()).replace('.', '/');
		
		File[] configFiles ={ 
				(new ClassPathResource(packagePath + "/instanceroot/testConfig.json")).getFile()};
		
		String[] scopes = {PathletConstants.SETTING_SCOPE, PathletConstants.CONTAINER_SCOPE, REQUEST_SCOPE};
		//ConfigManagerAccessor configManagerAcc = new ConfigManagerAccessor(configFiles, propertyFiles, "UTF-8");
		//ConfigManager configManager = configManagerAcc.loadConfigManager();
		//container = new DefaultPathletContainer(configManager, new String[]{PathletConstants.SETTING_SCOPE, PathletConstants.CONTAINER_SCOPE, REQUEST_SCOPE});
		
		StaticContainerHelper.getContainer("test", configFiles, null, "UTF-8", scopes);
	}


	@Test
	public void test() throws Exception {
		PathletContainer container = StaticContainerHelper.getContainer("test");
		
		//test the configuration using the defaultScope.
		TestWeaveBean appBeanWithDefaultScope = (TestWeaveBean)container.getInstance("/TestWeaveBean2");
		assertNotNull(appBeanWithDefaultScope);
		
		TestWeaveBean appBean = (TestWeaveBean)container.getInstance("/TestWeaveBean");

		assertTrue(appBean.isInitialized());
		assertFalse(appBean.isDestroied());
		
		assertEquals(appBean.getStringProp(), "applicationBean");
		assertEquals(appBean.isBooleanProp(), true);
		assertEquals(appBean.getIntProp(), 9);
		assertEquals(appBean.getLongProp(), 999999L);
		assertEquals(appBean.getFloatProp(), 9.99F);
		assertEquals(appBean.getDoubleProp(), 9.9999D);
		
		assertNotNull(appBean.getRefBean());
		assertNotNull(appBean.getContainerSpace());
		
		assertNotNull(appBean.getListProp());
		assertTrue(appBean.getListProp().size() == 3);
		assertEquals(appBean.getListProp().get(0), "3");
		assertEquals(appBean.getListProp().get(1), "4");
		assertEquals(appBean.getListProp().get(2), "5");
		
		assertNotNull(appBean.getSetProp());
		assertTrue(appBean.getSetProp().size() == 3);
		assertTrue(appBean.getSetProp().contains("6"));
		assertTrue(appBean.getSetProp().contains("7"));
		assertTrue(appBean.getSetProp().contains("8"));
		
		assertNotNull(appBean.getMapProp());
		assertTrue(appBean.getMapProp().size() == 3);
		assertEquals(appBean.getMapProp().get("a"), "1");
		assertEquals(appBean.getMapProp().get("b"), "2");
		assertEquals(appBean.getMapProp().get("c"), "3");
		
		assertEquals(appBean.getMatchPattern().getIncludes()[0], "pattern1");
		assertEquals(appBean.getMatchPattern().getIncludes()[1], "pattern2");
		assertEquals(appBean.getMatchPattern().getIncludes()[2], "pattern3");
		assertEquals(appBean.getMatchPattern().getIncludes().length, 3);

		assertEquals(appBean.getMatchPattern().getExcludes()[0], "pattern4");
		assertEquals(appBean.getMatchPattern().getExcludes()[1], "pattern5");
		assertEquals(appBean.getMatchPattern().getExcludes().length, 2);
		
		assertEquals(appBean.getMatchPatternIncludes().getIncludes()[0], "pattern11");
		assertEquals(appBean.getMatchPatternIncludes().getIncludes().length, 1);
		
		assertEquals(appBean.getPath(), new Path("/user/UserService"));
		
		assertNotNull(appBean.getContainer());
		
		UserService listUserService = (UserService)appBean.getRefList().get(0);
		CategoryService listCategoryService = (CategoryService)appBean.getRefList().get(1);
		assertNotNull(listUserService);
		assertNotNull(listCategoryService);
		
		Map<String, Object> refMap = (Map<String, Object>)appBean.getRefMap();
		assertTrue(refMap.size() == 2);
		UserService mapUserService = (UserService)refMap.get("userService");
		CategoryService mapCategoryService = (CategoryService)refMap.get("categoryService");
		assertNotNull(mapUserService);
		assertNotNull(mapCategoryService);
		
		String requestBoundObject = "request1";
		container.getSpace(REQUEST_SCOPE, requestBoundObject, true); //Create a new InstanceSpace binding with the requestBoundObject
		
		TestWeaveBean reqBean = (TestWeaveBean)container.getInstance(REQUEST_SCOPE, requestBoundObject, "/TestWeaveRequestBean");
		
		assertTrue(reqBean.isInitialized());
		assertFalse(reqBean.isDestroied());
		
		assertEquals(reqBean.getStringProp(), "requestBean");
		assertEquals(reqBean.isBooleanProp(), false);
		assertEquals(reqBean.getIntProp(), 9);
		assertEquals(reqBean.getLongProp(), 999999L);
		assertEquals(reqBean.getFloatProp(), 9.99F);
		assertEquals(reqBean.getDoubleProp(), 9.9999D);
		assertNotNull(appBean.getContainerSpace());
		assertNotNull(reqBean.getRequestSpace());
		assertEquals(reqBean.getRequestSpace().getScope(), "request");
		assertEquals(reqBean.getContainerSpace().getScope(), "container");

		Object aliasBean = container.getInstance("/bean");
		Object aliasBean2 = container.getInstance("/bean2");
		assertEquals(aliasBean, appBean);
		assertEquals(aliasBean2, appBeanWithDefaultScope);
		
		
		/////////////////////////////////////////ion test.
		//////////////////////////////////////////////
		//PathletContainer container = StaticContainerHelper.getContainer("test");
		
		DataInitService dataInitService = (DataInitService)container.getInstance("/common/DataInitService");
		
		//Initialize the database data
		dataInitService.saveInit();
		UserService userService = (UserService)container.getInstance("/user/UserService");
		User user = userService.getUser("ch"); //will append in interceptors, after intercepted the real parameter was changed to "charlie"
		assertNotNull(user);

		//The original method will return "Charlie Zhang", 
		// in this test, the string " got a inspiration!" will append from the interceptors operations.
		assertEquals("Charlie Zhang got a inspiration!", user.getName());
		
		
		//Flush all bean in the InstanceSpace
		//When destroying, the destoryMethod will be invoked from the InstanceSpace.
		container.flush(null);
		assertTrue(appBean.isDestroied());
		assertTrue(reqBean.isDestroied());
		
	}
}
