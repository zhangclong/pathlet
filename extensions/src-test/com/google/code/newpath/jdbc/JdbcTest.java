package com.google.code.newpath.jdbc;

import junit.framework.TestCase;

import org.junit.Test;

import com.google.code.newpath.jdbc.example.UserService;
import com.google.code.newpath.jdbc.example.vo.UserQueryVO;
import com.google.code.newpath.jdbc.example.vo.UserVO;
import com.google.code.pathlet.core.InstanceSpace;
import com.google.code.pathlet.core.PathletContainer;
import com.google.code.pathlet.vo.QueryParamVo;
import com.google.code.pathlet.vo.QueryResultVo;
import com.mchange.v2.c3p0.ComboPooledDataSource;


/**
 * Rethful simple core API test
 */
public class JdbcTest 
    extends TestCase
{
	
	private PathletContainer container;
	
	private UserService userService;
	
    public JdbcTest(String testName )
    {
        super( testName );
    }
	
//	protected void setUp() throws Exception {
//		///////////////////////////////////////
//		//ResourceFactory Creation
//		///////////////////////////////////////
//		ResourceFactory resourceFactory = new ConfigResourceFactory();
//		
//		String scopeName = PathletContainer.CONTAINER_SCOPE;
//
//    	//1. Create and register PathfulBeanFactory for service instances.
//		//   append in into rootResourceFactory
//		BeanPathConverter servicePathConverter = new ClassNamePathConverter("impl", null, "Impl");
//    	BeanResourceFactory serviceResourceFactory = new BeanResourceFactory(
//    			new Path("/"), "com.google.code.pathlet.jdbc.example", servicePathConverter, scopeName);
//    
//    	resourceFactory.registerCreator("/**/*", serviceResourceFactory);
//    	
//
//    	
//    	// 1. Create datasourceBeanResouce
//    	//    serviceResourceFactory.addResource(datasourceBeanResouce);
//    	// 2. Create transactionManagerBeanResouce
//    	//    serviceResourceFactory.addResource(transactionManagerBeanResouce);
//    	// 3. getTarget("datasouce") from context, and set the properties against it.
//    	// 4. getTartet("transactionManager") from context, and set the propertist agaist it.
//    	// 5. getTarget("dbInterceptor")
//    	BeanResource dataSourceBeanRes = new BeanResource(serviceResourceFactory, 
//    			new Path("/dataSource"), "com.mchange.v2.c3p0.ComboPooledDataSource");
//    	resourceFactory.addResource(dataSourceBeanRes);
//    	
//    	BeanResource transacitonManagerBeanRes = new BeanResource(serviceResourceFactory, 
//    			new Path("/transactionManager"), "org.springframework.jdbc.datasource.DataSourceTransactionManager");
//    	resourceFactory.addResource(transacitonManagerBeanRes);
//    
//    	BeanResource dbInterceptorBeanRes = new BeanResource(serviceResourceFactory, 
//    			new Path("/dbInterceptor"), "com.google.code.pathlet.jdbc.example.DbTransactionInterceptor");
//    	resourceFactory.addResource(dbInterceptorBeanRes);
//    	
//
//    	
//    	
//		//2. Application Context Creation
//		appContext = new BaseContext(null, scopeName, resourceFactory);
//		
//		
//		//3. Initialize the dataSource, transactioinManager and databaseInterceptor 
//		ComboPooledDataSource dataSource = (ComboPooledDataSource)appContext.getTarget("/dataSource");
//		dataSource.setDriverClass("com.mysql.jdbc.Driver");
//		dataSource.setJdbcUrl("jdbc:mysql://localhost/cpic?useUnicode=true&characterEncoding=utf-8");
//		dataSource.setUser("root");
//		dataSource.setPassword("123456");
//		dataSource.setInitialPoolSize(3);
//		dataSource.setMinPoolSize(3);
//		dataSource.setMaxPoolSize(20);
//		dataSource.setAcquireIncrement(3);
//		dataSource.setIdleConnectionTestPeriod(60);
//		dataSource.setAcquireIncrement(2);
//		dataSource.setMaxStatements(0);
//		//TODO the close method of datasource should be invocate after the context destory
//		
//		DataSourceTransactionManager transactionManager =
//				(DataSourceTransactionManager)appContext.getTarget("/transactionManager");		
//		transactionManager.setDataSource(dataSource);
//		
//		//getTarget will initialize the DBIntercepto	r
//		appContext.getTarget("/dbInterceptor");
//		
//		userService = (UserService)appContext.getTarget("/UserService");
//		
//		//TODO add method property wired supporting
//		userService.setDataSource(dataSource);
//	}
	


    
	@Override
	protected void tearDown() throws Exception {
		
		ComboPooledDataSource datasource = (ComboPooledDataSource)container.getInstance("/dataSource");
		datasource.close();
		
		super.tearDown();
	}

	@Test
	public void testUserService() throws Throwable {
		try {
			userService.saveUser(new UserVO("fuzi", "kong", "Fuzi Kong", "fuzikong@gmail.com"));
			UserVO gotUser = userService.getUser("fuzi");
			
			assertEquals(gotUser.getUsername(), "fuzi");
			assertEquals(gotUser.getName(), "Fuzi Kong");
			assertEquals(gotUser.getEmail(), "fuzikong@gmail.com");
			assertEquals(gotUser.getPassword(), "kong");
			
			userService.updateUser(new UserVO("fuzi", "kong1", "Fuzi Kong1", "fuzikong@gmail.com1"));
			UserVO updatedUser = userService.getUser("fuzi");
			
			assertEquals(updatedUser.getUsername(), "fuzi");
			assertEquals(updatedUser.getName(), "Fuzi Kong1");
			assertEquals(updatedUser.getEmail(), "fuzikong@gmail.com1");
			assertEquals(updatedUser.getPassword(), "kong1");
		}
		finally {
			userService.deleteUsers(new String[] {"fuzi"});
		}
		
	}
	
	
	public void testUserServiceQuery() throws Throwable {

		String[] deletingUsernames = new String[100];
		try {
			//Prepare the query data
			for(int i=0 ; i<100 ; i++) {
				String username = "testUser" + i;
				UserVO user = new UserVO(username, "kong" + i, "Test User" + i, "testUser" + i + "@gmail.com");
				deletingUsernames[i] = username;
				userService.saveUser(user);
			}
			
			//Giving the different query parameters, to verify the results is correct.
			UserQueryVO queryParam = new UserQueryVO();
			queryParam.setFetchSize(10);
			queryParam.setStartIndex(0);
			queryParam.setUsername("testUser");
			queryParam.setSortName("username");
			queryParam.setSortOrder(QueryParamVo.SORT_ORDER_ASC);
			
			
			QueryResultVo<UserVO> result = userService.queryUserList(queryParam);
			UserVO getUser = result.getDataList().get(0);
			assertEquals(result.getRowCount(), new Integer(100));
			assertEquals(result.getDataList().size(), 10);
			assertEquals(getUser.getUsername(), "testUser0");
			assertEquals("Test User0", getUser.getName());
			
			
			queryParam.setFetchSize(20);
			queryParam.setStartIndex(10);
			result = userService.queryUserList(queryParam);
			getUser = result.getDataList().get(0);
			assertEquals(result.getRowCount(), new Integer(100));
			assertEquals(result.getDataList().size(), 20);
			assertEquals(getUser.getUsername(), "testUser18");
			assertEquals("Test User18", getUser.getName());
	
			
			queryParam.setFetchSize(10);
			queryParam.setStartIndex(0);
			queryParam.setSortOrder(QueryParamVo.SORT_ORDER_DESC);
			result = userService.queryUserList(queryParam);
			getUser = result.getDataList().get(0);
			assertEquals(result.getRowCount(), new Integer(100));
			assertEquals(result.getDataList().size(), 10);
			assertEquals(getUser.getUsername(), "testUser99");
			assertEquals("Test User99", getUser.getName());
			
			
			queryParam.setEmail("testUser88@gmail.com");
			result = userService.queryUserList(queryParam);
			getUser = result.getDataList().get(0);
			assertEquals(result.getRowCount(), new Integer(1));
			assertEquals(result.getDataList().size(), 1);
			assertEquals(getUser.getUsername(), "testUser88");
			assertEquals("Test User88", getUser.getName());
		
		} 
		finally {
			userService.deleteUsers(deletingUsernames);	
		}
		
	}
	
	
    
    
}
