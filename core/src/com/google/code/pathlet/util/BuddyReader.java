package com.google.code.pathlet.util;

import java.io.IOException;
import java.io.InputStream;

import com.google.code.pathlet.util.ClassPathResource;
import com.google.code.pathlet.util.ClassUtils;
import com.google.code.pathlet.util.IOUtils;

public abstract class BuddyReader {
	public final static String DEFAULT_CHARSET = "UTF-8";
	
	private Class buddyClazz;
	
	private String filePathName;
	
	private String fileCharset;
	
	public BuddyReader(Class buddyClazz, String fileName) {
		this(buddyClazz, fileName, null);
	}
	
	public BuddyReader(Class buddyClazz, String filePathName, String fileCharset) {
		this.buddyClazz = buddyClazz;
		this.filePathName = filePathName;
		if(fileCharset != null) {
			this.fileCharset = fileCharset;
		}
		else {
			this.fileCharset = DEFAULT_CHARSET;
		}
		
		this.parseData();
	}
	
	public Class getBuddyClazz() {
		return buddyClazz;
	}

	public String getFilePathName() {
		return filePathName;
	}

	public String getFileCharset() {
		return fileCharset;
	}

	public InputStream getFileInputStream() throws IOException  {
		ClassLoader cl = buddyClazz.getClassLoader();
		String packagePath = ClassUtils.getPackageName(buddyClazz).replace('.', '/');
		return (new ClassPathResource(packagePath + "/" + filePathName, cl)).getInputStream();
	}
	
	public String getFileText() throws IOException  {
		return IOUtils.toString(getFileInputStream(), fileCharset);
	}
	
	public String getFile() throws IOException  {
		return IOUtils.toString(getFileInputStream(), fileCharset);
	}
	
	
	abstract public String get(String propName);
	
	abstract protected void parseData();

}
