package com.google.code.pathlet.core;

import com.google.code.pathlet.core.impl.ClassPathModule;

public class PathletConstants {
	
	public final static String INSTANCE_INJECT = "$instance:";
	
	public final static String SPACE_INJECT = "$space:";
	
	public final static String RESOURCE_INJECT = "$resource:";

	public final static String CONTAINER_INJECT = "$container";
 	
	public final static String SETTING_SCOPE = "setting";
	
	public final static String CONTAINER_SCOPE = "container";
	
	public final static String[] DEFAULT_SCOPES = {SETTING_SCOPE, CONTAINER_SCOPE};
	
	public final static String SYS_ATTR_CLASSES_BASEDIR = "classes.basedir";
	
	//public final static PathPattern SETTINGS_FACTORIES_MATCH_PATTERN = new PathPattern(new String[]{"/factories/**/*"});
	
	public final static Path SETTINGS_FACTORY_PATH = new Path("/factories/settingResourceFactory");
	
	public final static String DEFAULT_MODULE_CLASS = ClassPathModule.class.getCanonicalName();
}
