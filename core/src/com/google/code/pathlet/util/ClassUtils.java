package com.google.code.pathlet.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessControlException;


/**
 * 
 * Miscellaneous class utility methods. Mainly for internal use within the
 * framework;
 * 
 * @author clzhang
 *
 */

public class ClassUtils {
	
	/** The package separator character '.' */
	private static final char PACKAGE_SEPARATOR = '.';
	
	/**
	 * Return the default ClassLoader to use: typically the thread context
	 * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
	 * class will be used as fallback.
	 * <p>Call this method if you intend to use the thread context ClassLoader
	 * in a scenario where you absolutely need a non-null ClassLoader reference:
	 * for example, for class path resource loading (but not necessarily for
	 * <code>Class.forName</code>, which accepts a <code>null</code> ClassLoader
	 * reference as well).
	 * @return the default ClassLoader (never <code>null</code>)
	 * @see java.lang.Thread#getContextClassLoader()
	 */
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl =  null;
		
		try {
			cl =  ClassUtils.class.getClassLoader();
			if(cl == null) {
				cl = Thread.currentThread().getContextClassLoader();
			}
		}
		catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back to system class loader...
		}
		return cl;
	}
	
	/**
	 * Given an input class object, return a string which consists of the
	 * class's package name as a pathname, i.e., all dots ('.') are replaced by
	 * slashes ('/'). Neither a leading nor trailing slash is added. The result
	 * could be concatenated with a slash and the name of a resource and fed
	 * directly to <code>ClassLoader.getResource()</code>. For it to be fed to
	 * <code>Class.getResource</code> instead, a leading slash would also have
	 * to be prepended to the returned value.
	 * @param clazz the input class. A <code>null</code> value or the default
	 * (empty) package will result in an empty string ("") being returned.
	 * @return a path which represents the package name
	 * @see ClassLoader#getResource
	 * @see Class#getResource
	 */
	public static String classPackageAsResourcePath(Class<?> clazz) {
		if (clazz == null) {
			return "";
		}
		String className = clazz.getName();
		int packageEndIndex = className.lastIndexOf('.');
		if (packageEndIndex == -1) {
			return "";
		}
		String packageName = className.substring(0, packageEndIndex);
		return packageName.replace('.', '/');
	}
	
	
	/**
	 * Given a method, which may come from an interface, and a target class used
	 * in the current reflective invocation, find the corresponding target method
	 * if there is one. E.g. the method may be <code>IFoo.bar()</code> and the
	 * target class may be <code>DefaultFoo</code>. In this case, the method may be
	 * <code>DefaultFoo.bar()</code>. This enables attributes on that method to be found.
	 * <p><b>
	 */
	public static Method getMostSpecificMethod(Method method, Class<?> targetClass) {
		Method specificMethod = null;
		if (method != null && isOverridable(method, targetClass) &&
				targetClass != null && !targetClass.equals(method.getDeclaringClass())) {
			try {
				specificMethod = ReflectionUtils.findMethod(targetClass, method.getName(), method.getParameterTypes());
			} catch (AccessControlException ex) {
				// security settings are disallowing reflective access; leave
				// 'specificMethod' null and fall back to 'method' below
			}
		}
		return (specificMethod != null ? specificMethod : method);
	}
	

	/**
	 * Determine whether the given method is overridable in the given target class.
	 * @param method the method to check
	 * @param targetClass the target class to check against
	 */
	private static boolean isOverridable(Method method, Class targetClass) {
		if (Modifier.isPrivate(method.getModifiers())) {
			return false;
		}
		if (Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers())) {
			return true;
		}
		return getPackageName(method.getDeclaringClass()).equals(getPackageName(targetClass));
	}
	
	/**
	 * Determine the name of the package of the given class:
	 * e.g. "java.lang" for the <code>java.lang.String</code> class.
	 * @param clazz the class
	 * @return the package name, or the empty String if the class
	 * is defined in the default package
	 */
	public static String getPackageName(Class<?> clazz) {
		String className = clazz.getName();
		int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		return (lastDotIndex != -1 ? className.substring(0, lastDotIndex) : "");
	}

}
