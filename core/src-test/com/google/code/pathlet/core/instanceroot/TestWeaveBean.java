package com.google.code.pathlet.core.instanceroot;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.code.pathlet.core.InstanceSpace;
import com.google.code.pathlet.core.Path;
import com.google.code.pathlet.core.PathPattern;
import com.google.code.pathlet.core.PathletContainer;
import com.google.code.pathlet.core.instanceroot.user.UserService;

public class TestWeaveBean {
	
	private String stringProp;

	private boolean booleanProp;
	
	private int intProp;

	private long longProp;
	
	private float floatProp;
	
	private double doubleProp;
	
	private UserService refBean;
	
	private InstanceSpace requestSpace;
	
	private InstanceSpace containerSpace;
	
	private List<String> listProp;
	
	private Set<String> setProp;
	
	private Map<String, String> mapProp;
	
	private List refList;
	
	private Map<String, Object> refMap;
	
	private PathPattern matchPattern;
	
	private PathPattern matchPatternIncludes;
	
	private Path path;
	
	private PathletContainer container;
	
	
	/** 
	 * Flag indicate the the destroyMethd and initializeMethod has been invoked.
	 */
	private boolean destroied = false;
	private boolean initialized = false;
	public void init() {
		initialized = true;
	}
	public void close() {
		destroied = true;
	}
	public boolean isInitialized() {
		return initialized;
	}
	public boolean isDestroied() {
		return destroied;
	}
	public String getStringProp() {
		return stringProp;
	}
	public void setStringProp(String stringProp) {
		this.stringProp = stringProp;
	}
	public boolean isBooleanProp() {
		return booleanProp;
	}
	public void setBooleanProp(boolean booleanProp) {
		this.booleanProp = booleanProp;
	}
	public int getIntProp() {
		return intProp;
	}
	public void setIntProp(int intProp) {
		this.intProp = intProp;
	}
	public long getLongProp() {
		return longProp;
	}
	public void setLongProp(long longProp) {
		this.longProp = longProp;
	}
	public float getFloatProp() {
		return floatProp;
	}
	public void setFloatProp(float floatProp) {
		this.floatProp = floatProp;
	}
	public double getDoubleProp() {
		return doubleProp;
	}
	public void setDoubleProp(double doubleProp) {
		this.doubleProp = doubleProp;
	}
	public UserService getRefBean() {
		return refBean;
	}
	public void setRefBean(UserService refBean) {
		this.refBean = refBean;
	}
	public List<String> getListProp() {
		return listProp;
	}
	public void setListProp(List<String> listProp) {
		this.listProp = listProp;
	}
	public Set<String> getSetProp() {
		return setProp;
	}
	public void setSetProp(Set<String> setProp) {
		this.setProp = setProp;
	}
	public Map<String, String> getMapProp() {
		return mapProp;
	}
	public void setMapProp(Map<String, String> mapProp) {
		this.mapProp = mapProp;
	}
	public List getRefList() {
		return refList;
	}
	public void setRefList(List refList) {
		this.refList = refList;
	}
	public Map<String, Object> getRefMap() {
		return refMap;
	}
	public void setRefMap(Map<String, Object> refMap) {
		this.refMap = refMap;
	}
	public InstanceSpace getRequestSpace() {
		return requestSpace;
	}
	public void setRequestSpace(InstanceSpace requestSpace) {
		this.requestSpace = requestSpace;
	}
	public InstanceSpace getContainerSpace() {
		return containerSpace;
	}
	public void setContainerSpace(InstanceSpace containerSpace) {
		this.containerSpace = containerSpace;
	}
	public PathPattern getMatchPattern() {
		return matchPattern;
	}
	public void setMatchPattern(PathPattern matchPattern) {
		this.matchPattern = matchPattern;
	}
	public PathPattern getMatchPatternIncludes() {
		return matchPatternIncludes;
	}
	public void setMatchPatternIncludes(PathPattern matchPatternIncludes) {
		this.matchPatternIncludes = matchPatternIncludes;
	}
	public Path getPath() {
		return path;
	}
	public void setPath(Path path) {
		this.path = path;
	}
	public PathletContainer getContainer() {
		return container;
	}
	public void setContainer(PathletContainer container) {
		this.container = container;
	}
	

}
