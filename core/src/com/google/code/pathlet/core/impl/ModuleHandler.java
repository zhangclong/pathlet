package com.google.code.pathlet.core.impl;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xeustechnologies.jcl.JarClassLoader;

import com.google.code.pathlet.config.ConfigException;
import com.google.code.pathlet.core.Module;
import com.google.code.pathlet.core.ModuleListener;
import com.google.code.pathlet.core.PathletContainer;
import com.google.code.pathlet.core.exception.ResourceException;
import com.google.code.pathlet.util.ModificationScanner;
import com.google.code.pathlet.util.ValueUtils;

public class ModuleHandler {
	
	public static Log log = LogFactory.getLog(DefaultModuleManager.class);
	
	public final static String URL_FILE_PROTOCOL = "file";
	
	public final static long DEFAULT_SCAN_INTERVAL = 5000;

	private Module module = null;
	
	private JarClassLoader classLoader = null;
	
	private ModificationScanner scanner = null;
	
	private volatile boolean changed = false;
	
	private volatile boolean started = false;
	
	private volatile long scanInterval = DEFAULT_SCAN_INTERVAL;
	
	private ModuleListener listener;
	
	private ModulePathClassLoader loader;
	
	ModuleHandler(Module module, ModulePathClassLoader loader) {
		this.module = module;
		this.scanInterval = scanInterval;
		this.loader = loader;
		this.listener = null;
	}
	
	public boolean isChanged() {
		return changed;
	}
	
	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}
	
	public Module getModule() {
		return module;
	}

	/**
	 * Build or rebuild the ClassLoader against the module classpaths.
	 */
	public ClassLoader rebuildClassLoader() {
		classLoader = new JarClassLoader();
		for(URL classPath : module.getClassPaths()) {
			classLoader.add(classPath);
		}
		
		return classLoader;
	}
	
	public void startScanner() {
		if(module.isAutoReload()) {
			createScanner();
			scanner.start();
		}
	}
	
	public void stopScanner() {
		if(module.isAutoReload()) {
			scanner.stop();
			started = false;//Set the started flag to false
		}
	}
	
	public void listenerInit(PathletContainer container) throws ResourceException {
		if(ValueUtils.notEmpty(module.getListener())) {
			try {
				if(this.listener == null) {
					//Load the ModuleListener class from the module's classloader.
					Class<ModuleListener> listenerClass = loader.loadClass(module.getId(), module.getListener());
					this.listener = listenerClass.newInstance();
				}
				
				this.listener.init(container, module);
			} 
			catch (Exception e) {
				throw new ResourceException(e);
			}
		}
	}
	
	public void listenerDestory(PathletContainer container) {
		if(this.listener != null) {
			this.listener.destroy(container, module);
			this.listener = null;
		}
	}
	
	
	private void createScanner() {
		if( this.scanner == null) {
			this.scanner = new ModificationScanner();
			
			File[] scanDirs = new File[module.getClassPaths().length];
			for(int i=0; i<module.getClassPaths().length ; i++) {
				URL classpath = module.getClassPaths()[i];
				if(URL_FILE_PROTOCOL.equalsIgnoreCase(classpath.getProtocol())) {
					File scanDir;
					try {
						scanDir = new File(classpath.toURI());
					} catch (URISyntaxException e) {
						throw new ConfigException("The property autoReload=\"true\" of module \"" + module.getId() + "\", But classPaths property contained none welformat file URL", e);
					}
					scanDirs[i] = scanDir;
				}
				else {
					throw new ConfigException("The property autoReload=\"true\" of module \"" + module.getId() + "\", But classPaths property contained none file URL");
				}
			}
			
			scanner.setScanDirs(scanDirs);
	        scanner.setScanInterval(this.scanInterval);
	        scanner.setRecursive(true);
	        scanner.addListener(new ModificationListener());
	        scanner.setReportExistingFilesOnStartup(false);
		}
	}
	
	
	private class ModificationListener implements ModificationScanner.BulkListener {
		

		public ModificationListener() {
			super();
			changed = false;
		}

		
		public void filesChanged(List<String> filenames) {
			if(changed == false) {
				changed = true;
				log.info("Modification scanner find a changing in module id=\"" + module.getId() + "\". The module will be reloaded in next resource loading.");
			}
		}
		
	}

	
}

