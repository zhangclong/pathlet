package com.google.code.pathlet.core;

import junit.framework.TestCase;


/**
 * 
 * Test the <code>Path</code> utility class. 
 * 
 * 
 * 
 * @author Charlie Zhang
 *
 */
public class PathTest 
    extends TestCase
{
	

    public PathTest(String testName ) throws Exception
    {
        super( testName );
    }
	

	public void testPath()
    {
		Path path = new Path("/eshop/party/web/AuthenticationAction/login.do");
		assertEquals("do", path.getSuffix());
		assertEquals("login.do", path.getName());
		assertEquals("login", path.getNameWithoutSuffix());
		assertEquals("/eshop/party/web/AuthenticationAction/", path.getParent().toString());
    }
    
    
}
