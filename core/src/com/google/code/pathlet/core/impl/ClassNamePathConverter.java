/*
 * Copyright 2010-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.code.pathlet.core.impl;

import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.impl.DefaultPathConverter;

/**
 * Provide the common java class name convert function.
 * For example: <br/>
 *  If you want to map path "/somepackage/ServiceName" to java class "somepackage.support.PrefixServiceNameSuffix", <br/> 
 *  you should create a new ServicePathConverter("support", "Prefix", "Suffix") setting it into Resource factory.
 * 
 * @author Charlie Zhang
 *
 */
public class ClassNamePathConverter extends DefaultPathConverter {
	
	private String appendPackage = "";
	
	private String namePrefix = "";
	
	private String nameSuffix = "";
	
	
	public ClassNamePathConverter() {  }
	
	public ClassNamePathConverter(String appendPackage, String namePrefix, String nameSuffix) {
		setAppendPackage(appendPackage);
		setNamePrefix(namePrefix);
		setNameSuffix(nameSuffix);
		
		
	}
	
	public void setAppendPackage(String appendPackage) {
		if(appendPackage != null) {
			this.appendPackage = appendPackage.trim();
		}
	}

	public void setNamePrefix(String namePrefix) {
		if(namePrefix != null) {
			this.namePrefix = namePrefix.trim();
		}
	}

	public void setNameSuffix(String nameSuffix) {
		if(nameSuffix != null) {
			this.nameSuffix =  nameSuffix.trim();
		}
	}
	
	public String getAppendPackage() {
		return appendPackage;
	}

	public String getNamePrefix() {
		return namePrefix;
	}

	public String getNameSuffix() {
		return nameSuffix;
	}

	@Override
	protected String convertToClassName(Path resourcePath, String prePathStr,
			String basePackage) {
		String relativePackage = (resourcePath.getDirectory()
				.substring(prePathStr.length())).replace(Path.SEPARATOR, '.');
		
		String fullClassName = basePackage + '.' + relativePackage
		+ ((this.appendPackage.length() > 0) ? (this.appendPackage + '.') : "")    
		+ this.namePrefix + resourcePath.getNameWithoutSuffix() + this.nameSuffix;
		
		return fullClassName;
	}

	
	
}
