package com.google.code.pathlet.jdbc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.sql.DataSource;

import org.junit.Test;

import com.google.code.pathlet.core.PathletContainer;
import com.google.code.pathlet.jdbc.ExtJdbcTemplate;
import com.google.code.pathlet.jdbc.instance.transaction.ConfUserService;
import com.google.code.pathlet.test.ContainerTestHelper;
import com.google.code.pathlet.util.ClassPathResource;
import com.google.code.pathlet.util.ClassUtils;
import com.google.code.pathlet.util.IOUtils;

public class JdbcTransactionalTest {
	
	
	protected PathletContainer getContainer() throws IOException {
		PathletContainer container = ContainerTestHelper.getContainer("testapp");
		if(container == null) {
			File[] configFiles ={
					(new ClassPathResource("/com/google/code/pathlet/jdbc/jdbc-test-app.json")).getFile()
					};
			//File[] propertyFiles = {
			//		(new ClassPathResource("/dataSource.properties")).getFile(),
			//		(new ClassPathResource("/dataSourceDw.properties")).getFile()
			//		};
			return ContainerTestHelper.getContainer("testapp", configFiles, null, "UTF-8");
		}
		else {
			return container;
		}
	}
	
	@Test
	public void test() throws Exception {
		ConfUserService confUserService = (ConfUserService)getContainer().getInstance("/transaction/ConfUserService");
		
		
	}
}
