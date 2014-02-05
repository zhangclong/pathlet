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

import java.util.ArrayList;
import java.util.List;

import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.exception.PathException;

/**
 * 
 * @author Charlie Zhang
 *
 */
public class PathHelper {
	
	public static String filterOutRedundant(String rawPathname) throws PathException{
    	
		char sep = Path.SEPARATOR;
		

		int last = rawPathname.length() - 1;
		int idx = last;
		int adjacentDots = 0;
		//int nonDotCount = 0;
		
		int ignoreBegin = -1, ignoreEnd = -1;
		int ignoreParent = 0;
		
		List<int[]> ignoreList = new ArrayList<int[]>(1);
		
		while (idx >= 0) {
			char c = rawPathname.charAt(idx);
			if (c == '.') {
				++adjacentDots;
			} 
			else if (c == sep) {
				if (adjacentDots == 1 ) {
					checkAddingList(ignoreList, new int[]{idx - 1, idx});
				}
				else if(adjacentDots == 2) {
					if(ignoreParent <=0) {
						ignoreEnd = idx + 2;
					}
					++ignoreParent;
				}
				else if(ignoreParent > 0) {
					--ignoreParent;
					if(ignoreParent <= 0) {
						ignoreBegin = idx;
						checkAddingList(ignoreList, new int[]{ignoreBegin, ignoreEnd});
					}
				}
				
				adjacentDots = 0;
			} 
			else {
				adjacentDots = 0;
			}
			--idx;
		}
		
		if(ignoreParent != 0) {
			throw new PathException("Failed to filterOutRedundant from pathname: '" + rawPathname + "'");
		}

		if(ignoreList.size() > 0) {
			
			StringBuilder buf = new StringBuilder(rawPathname.length() - 3);
			
			int endIdx = ignoreList.size() - 1;
			int[] lastIgn = ignoreList.get(0);
			
			buf.append(rawPathname.substring(0, lastIgn[0]));
			
			if(endIdx >= 1) {
				for(int i=1 ; i<=endIdx ; i++) {
					int ign[] = ignoreList.get(i);
					
					buf.append(rawPathname.substring(lastIgn[1] + 1, ign[0]));
					
					lastIgn = ign;
				}
			}
			
			if((lastIgn[1] + 1) < rawPathname.length()) {
				buf.append(rawPathname.substring(lastIgn[1] + 1, rawPathname.length()));
			}
			return buf.toString();
		}
		else {
			return rawPathname;
		}
    }
	
	public static void checkAddingList(List<int[]> ignoreList, int[] value) {
    	if(ignoreList.size() > 0) {
    		int[] lastValue = ignoreList.get(0);
    		if(value[1] >  lastValue[1]) {
    			lastValue[1] = value[1];
    			lastValue[0] = value[0];
    		}
    		else if(value[1] >  lastValue[0]) {
    			lastValue[0] = value[0];
    		}
    		else {
    			ignoreList.add(0, value);
    		}
    	}
    	else {
    		ignoreList.add(0, value);
    	}
    }
	
	/*
	 * A normal Unix pathname contains no duplicate slashes and does not end
	 * with a slash. It may be the empty string.
	 */

	/*
	 * Normalize the given pathname, whose length is len, starting at the given
	 * offset; everything before this offset is already normal.
	 */
	public static String normalize(String pathname, int len, int off) {
		if (len == 0)
			return pathname;
		int n = len;
		while ((n > 0) && (pathname.charAt(n - 1) == '/'))
			n--;
		if (n == 0)
			return "/";
		StringBuilder sb = new StringBuilder(pathname.length());
		if (off > 0)
			sb.append(pathname.substring(0, off));
		char prevChar = 0;
		for (int i = off; i < n; i++) {
			char c = pathname.charAt(i);
			if ((prevChar == '/') && (c == '/'))
				continue;
			sb.append(c);
			prevChar = c;
		}
		return sb.toString();
	}

	
	
	/*
	 * Check that the given pathname is normal. If not, invoke the real
	 * normalizer on the part of the pathname that requires normalization. This
	 * way we iterate through the whole pathname string only once.
	 */
	public static String normalize(String pathname) {
		int n = pathname.length();
		char prevChar = 0;
		for (int i = 0; i < n; i++) {
			char c = pathname.charAt(i);
			if ((prevChar == '/') && (c == '/'))
				return normalize(pathname, n, i - 1);
			prevChar = c;
		}
		if (prevChar == '/')
			return normalize(pathname, n, n - 1);
		return pathname;
	}
    
    /**
     * parse pathname and get the parent path
     * 
     * @param pathname
     * @return
     */
	public static String parentOrNull(String pathname) {
		char sep = Path.SEPARATOR;
		
		int last = pathname.length() - 1;
		int idx = last - 1;
		int adjacentDots = 0;
		int nonDotCount = 0;
		while (idx >= 0) {
			char c = pathname.charAt(idx);
			if (c == '.') {
				if (++adjacentDots >= 2) {
					// Punt on pathnames containing . and ..
					return null;
				}
			} 
			else if (c == sep) {
				if (adjacentDots == 1 && nonDotCount == 0) {
					// Punt on pathnames containing . and ..
					return null;
				}
				if(idx == 0 && pathname.charAt(idx) == sep && pathname.charAt(idx + 1) != sep) {
				    // the parent path is root path "/"
				    return Character.toString(c);
				}
				if (idx == 0 || idx >= last - 1 || pathname.charAt(idx - 1) == sep) {
					// Punt on pathnames containing adjacent slashes
					// toward the end
					return null;
				}
				return pathname.substring(0, idx + 1);
			} 
			else {
				++nonDotCount;
				adjacentDots = 0;
			}
			--idx;
		}
		return null;
	}
	
	public static boolean isAbsolute(String pathname) {
		return pathname.charAt(0) == Path.SEPARATOR;
	}
	

}
