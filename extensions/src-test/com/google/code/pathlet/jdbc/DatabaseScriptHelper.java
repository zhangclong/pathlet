package com.google.code.pathlet.jdbc;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.sql.DataSource;

import org.springframework.util.ClassUtils;

import com.google.code.pathlet.core.PathletContainer;
import com.google.code.pathlet.util.ClassPathResource;
import com.google.code.pathlet.util.StaticContainerHelper;
import com.google.code.pathlet.util.ScriptRunner;


public class DatabaseScriptHelper 
{
	
	public static void prepare() {
		
	}

	public static void main() throws Exception {
		
		File[] configFiles = { (new ClassPathResource("/app-config.json")).getFile() };
		File[] propertyFiles = { (new ClassPathResource("/dataSource.properties")).getFile() };
		PathletContainer container = StaticContainerHelper.getContainer("test", configFiles, propertyFiles, "UTF-8");

		DataSource dataSource = (DataSource)container.getInstance("/dataSource");
		
		ScriptRunner scriptRunner = new ScriptRunner(dataSource.getConnection(), true, true);
		
		InputStream is = new ClassPathResource("/init.sql", ClassUtils.getDefaultClassLoader()).getInputStream();
		InputStreamReader reader = new InputStreamReader(is, "UTF-8");
		try {
			scriptRunner.runScript(reader);
		}
		finally {
			reader.close();
			is.close();
		}	

	}
	

    
}
