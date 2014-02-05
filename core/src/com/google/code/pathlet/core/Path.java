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
package com.google.code.pathlet.core;

import com.google.code.pathlet.core.exception.PathException;
import com.google.code.pathlet.core.impl.PathHelper;


/**
 * 
 * An abstract representation of request path.
 * This class presents an
 * abstract, system-independent view of hierarchical fullPaths.  An
 * <em>abstract fullPath</em> has two components:
 *
 * <ol>
 * <li> An optional system-dependent <em>prefix</em> string <code>"/"</code>
 * <li> A sequence of zero or more string <em>names</em>.
 * </ol>
 * 
 * @author Charlie Zhang
 *
 */
public class Path implements Comparable<Path> {
	
	public static final char SEPARATOR = '/';
	
	public static final String SEPARATOR_STRING = "/";
	
	public static final char SUFFIX_SEPARATOR = '.';
	
    /**
     * This abstract fullPath's normalized fullPath string.  A normalized
     * fullPath string uses the default <code>SEPARATOR</code> character and does not
     * contain any duplicate or redundant separators.
     *
     * @serial
     */
    protected String fullPath;
    
    protected String name = null;
    
    protected String suffix = null;
    
    protected String nameWithoutSuffix = null;
    
    protected String directory = null;
    
    /**
     * The elements <code>name</code> and <code>path</code> is lazy initilization.
     * To indicate whether <code>name</code> and <code>path</code> has be parsed and initilized.
     */
    private boolean parsedFullPath = false;
    
    
    private boolean parsedSuffix = false;
    
    public Path(String rawFullPath) throws PathException {
    	this(rawFullPath, true);
    }
    
    /**
     * 
     * 
     * @param rawFullPath
     * @param filterOutRedundant 
     *     <code>true</code> Filter out parent path String <tt>"/."</tt> <tt>"/.."</tt> to be effect and removed.
     *     <code>false</code> 
     * @throws PathException
     */
    public Path(String rawFullPath, boolean filterOutRedundant) throws PathException {
		if(rawFullPath.indexOf('?') >= 0) {
			throw new PathException("Could not contains '?' in path:" + rawFullPath);
		}
		
    	if(filterOutRedundant) {    		
    		this.fullPath = PathHelper.filterOutRedundant(PathHelper.normalize(rawFullPath.trim()));
    	}
    	else {
    		this.fullPath = rawFullPath;
    	}
    }
    
    /**
     * 
     * @param parent must be isAbsolute()
     * @param child the sub child path  or  path and name
     * @throws PathException
     */
    public Path(Path parent, String child) throws PathException {
    	if(parent == null) {
    		throw new PathException("Parent path is null!");
    	}
    	
    	if(child == null) {
    		throw new PathException("Argument child is null!");
    	}
    	else if(child.charAt(0) == Path.SEPARATOR) {
    		throw new PathException("Argument child could not be leaded with char '" + Path.SEPARATOR + "'");
    	}
    	
    	
		if(parent.isAbsolute() == false) {
			throw new PathException("The paremeter currentPath must be absolute path. " +
					"parent=" + parent);
		}
		
		int currentPathLen = parent.fullPath.length();
		int fullPathLen = child.length();
		StringBuilder buf = new StringBuilder(currentPathLen + fullPathLen + 1);
		buf.append(parent.fullPath);
		if(parent.fullPath.charAt(currentPathLen - 1) != SEPARATOR) {
			buf.append(SEPARATOR);
		}
		buf.append(child);
		
		this.fullPath = PathHelper.filterOutRedundant(PathHelper.normalize(buf.toString()));
    }
    
    
    public String getFullPath() {
    	return fullPath;
    }
    
    /**
     * Last string separated by '/' 
     * @return
     */
    public String getName() {
    	if(parsedFullPath == false) {
    		parseFullPath();
    		parsedFullPath = true;
    	}
    	return this.name;
    }
    
    /**
     * Get suffix of this path.<br/>
     * For example: When name is "myName.do", this function will return "do".
     * @return
     */
    public String getSuffix() {
    	if(parsedSuffix == false) {
    		parseSuffix();
    		parsedSuffix = true;
    	}
    	return this.suffix;
    }
    
    
    /**
     * Return the name without suffix <br/>
     * For example: When name is "myName.do", this function will return "myName".
     * @return 
     */
    public String getNameWithoutSuffix() {
    	if(parsedSuffix == false) {
    		parseSuffix();
    		parsedSuffix = true;
    	}
    	return this.nameWithoutSuffix;
    }
    
    /**
     * 
     * @return Directory string of this path, which be end with '/' for existing one.
     */
    public String getDirectory() {
    	if(parsedFullPath == false) {
    		parseFullPath();
    		parsedFullPath = true;
    	}

    	return this.directory;
    }
    
    public Path getParent() throws PathException {
    	String parentPathName = PathHelper.parentOrNull(this.fullPath);
    	if(parentPathName != null) {
    		return new Path(parentPathName, false);
    	}
    	else {
    		return null;
    	}
    }
    
    /**
     * convert to 
     * @param rootParent  root parent path for this one. Must be absolute path
     */
    public void convertToAbsolute(Path rootParent) throws PathException {
    	if(isAbsolute() == false) {
    		if(rootParent.isAbsolute() == false) {
    			throw new PathException("The paremeter currentPath must be absolute path. " +
    					"currentPath=" + rootParent);
    		}
    		
    		int currentPathLen = rootParent.fullPath.length();
    		int fullPathLen = this.fullPath.length();
    		StringBuilder buf = new StringBuilder(currentPathLen + fullPathLen + 1);
    		buf.append(rootParent.fullPath);
    		if(rootParent.fullPath.charAt(currentPathLen - 1) != Path.SEPARATOR) {
    			buf.append(Path.SEPARATOR);
    		}
    		buf.append(this.fullPath);
    		
    		this.fullPath = PathHelper.filterOutRedundant(PathHelper.normalize(buf.toString()));
    	}
    }
    

    /**
     * 
     * Tests whether this abstract fullPath is absolute.  When fullPath is
     * absolute if its prefix is <code>"/"</code>.  
     *
     * @return  <code>true</code> if it is absolute,
     *          <code>false</code> otherwise
     */
    public boolean isAbsolute() {
    	return PathHelper.isAbsolute(fullPath);
    }
    
	@Override
	public String toString() {
		return fullPath;
	}
	
	@Override
	public int hashCode() {
		return fullPath.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Path) {
			return fullPath.equals( ((Path)obj).fullPath );
		}
		else {
			return false;
		}
	}

	public int compareTo(Path other) {
		return this.fullPath.compareTo(other.fullPath);
	}
	
	
	private void parseFullPath() {
		int len = this.fullPath.length();
		int idx = this.fullPath.lastIndexOf(Path.SEPARATOR);
		if(idx >= 0) {
			if( (idx + 1) < len) {
				this.name = this.fullPath.substring(idx + 1);
				this.directory = this.fullPath.substring(0, idx + 1);
			}
			else {
				this.name = null;
				this.directory = this.fullPath.substring(0, idx + 1);
			}
		}
		else {
			this.name = this.fullPath;
			this.directory = null;
		}
	}
	
	private void parseSuffix() {
		String n = getName();
		int suffixIdx = n.lastIndexOf(Path.SUFFIX_SEPARATOR);
		if(suffixIdx >= 0) {
			this.suffix = n.substring(suffixIdx + 1);
			this.nameWithoutSuffix = n.substring(0, suffixIdx);
		}
		else {
			this.nameWithoutSuffix = getName();
		}
	}
	
    
}
