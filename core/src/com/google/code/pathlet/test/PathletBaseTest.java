package com.google.code.pathlet.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.google.code.pathlet.core.PathletContainer;
import com.google.code.pathlet.util.ClassPathResource;
import com.google.code.pathlet.util.ClassUtils;
import com.google.code.pathlet.util.IOUtils;

/**
 * FIXME this file should be deleted!
 * @author clzhang
 *
 */
public class PathletBaseTest {
	
	
	protected PathletContainer getContainer() throws IOException {
		PathletContainer container = ContainerTestHelper.getContainer("testapp");
		if(container == null) {
			File[] configFiles ={
					(new ClassPathResource("/app-config.json")).getFile(),
					(new ClassPathResource("/test-datasource-config.json")).getFile() 
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
	
	protected String loadThisPackageText(String fileName, String charset) throws IOException  {
		ClassLoader cl = this.getClass().getClassLoader();
		String packagePath = ClassUtils.getPackageName(this.getClass()).replace('.', '/');
		File jsonFile = (new ClassPathResource(packagePath + "/" + fileName, cl)).getFile();
		
		return IOUtils.toString(new FileInputStream(jsonFile), charset);
	}
	
	
}
