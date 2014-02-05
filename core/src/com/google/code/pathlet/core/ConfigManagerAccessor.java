package com.google.code.pathlet.core;

import static com.google.code.pathlet.core.PathletConstants.SYS_ATTR_CLASSES_BASEDIR;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.code.pathlet.config.ConfigManager;
import com.google.code.pathlet.config.impl.JsonConfigManager;
import com.google.code.pathlet.util.ClassPathResource;
import com.google.code.pathlet.util.IOUtils;

public class ConfigManagerAccessor {
	
	private String configEncoding;
	
	private File[] configFiles;
	
	private File[] propertyFiles;
	
	public ConfigManagerAccessor(File[] configFiles, File[] propertyFiles, String configEncoding) {
		this.configFiles = configFiles;
		this.propertyFiles = propertyFiles;
		this.configEncoding = configEncoding;
	}
	
	public String getConfigEncoding() {
		return configEncoding;
	}

	public File[] getConfigFiles() {
		return configFiles;
	}

	public File[] getPropertyFiles() {
		return propertyFiles;
	}


	/**
	 * Load configurations and instance the ConfigManager by them.
	 * @return
	 * @throws IOException
	 */
	public ConfigManager loadConfigManager() throws IOException {
		String[] jsonContents = new String[configFiles.length];
		String[] jsonContentNames = new String[configFiles.length];
		for(int i=0 ; i<configFiles.length ; i++) {
			File configFile = configFiles[i];
			if(configFile != null) {
				jsonContents[i] = IOUtils.toString(new FileInputStream(configFile), configEncoding);
				jsonContentNames[i] = configFile.getAbsolutePath();
			}
		}

		Properties prop = loadPlaceholder();
	    return new JsonConfigManager(jsonContents, jsonContentNames, prop);
    }
	
	
	protected void loadSysProperty(Properties props) throws IOException {
		ClassPathResource cpr = new ClassPathResource("/", ConfigManagerAccessor.class);

		String classesBasedir = cpr.getFile().getAbsolutePath();
		if(classesBasedir.endsWith(File.separator) == false) {
			classesBasedir += File.separator;
		}
		classesBasedir = classesBasedir.replace("\\", "/");
		
		props.setProperty(SYS_ATTR_CLASSES_BASEDIR, classesBasedir);
	}
	
	/**
	 * Load placeholder properties file content into Properties, and return it.
	 * 
	 * @return
	 * @throws IOException
	 */
	protected Properties loadPlaceholder() throws IOException {
    	Properties props = new Properties();
    	
    	if(propertyFiles != null) {
	    	//Load properties files one by one
	    	for(File propertyFile : propertyFiles) {
	    		InputStream is = null;

	    		try{
			    	is = new FileInputStream(propertyFile);
			    	Properties p = new Properties();
			    	p.load(is);
			    	props.putAll(p);
		    	}
				finally {
					if(is != null) is.close();
				}
	    	}
    	}
    	
    	loadSysProperty(props);
    	
    	return props;
	}

}
