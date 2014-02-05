package com.google.code.pathlet.jdbc;

public class EntityUpdateDef {
	
	private String id;
	
	private Class<?> entityClass;
	
	private String tableName;
	
	private String[] conditions;  //The property names which be used in SQL 'WHERE' condition  
	
	private String[] appendClause = null; //附加的Where语句

	private String[] excludes = null;    //Excludes property names
	
	public EntityUpdateDef(String id, Class<?> entityClass, String tableName, String[] conditions) {
		this.id = id;
		this.entityClass = entityClass;
		this.tableName = tableName;
		this.conditions = conditions;
	}

	public EntityUpdateDef(String id, Class<?> entityClass, String tableName, 
			String[] conditions, String[] excludes) {
		this.id = id;
		this.entityClass = entityClass;
		this.tableName = tableName;
		this.conditions = conditions;
		this.excludes = excludes;
	}
	
	public EntityUpdateDef(String id, Class<?> entityClass, String tableName, 
			String[] conditions, String[] excludes, String[] appendClause) {
		this.id = id;
		this.entityClass = entityClass;
		this.tableName = tableName;
		this.conditions = conditions;
		this.excludes = excludes;
		this.appendClause = appendClause;
	}

	public String getId() {
		return id;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}
	
	public String getTableName() {
		return tableName;
	}

	public String[] getConditions() {
		return conditions;
	}

	public String[] getExcludes() {
		return excludes;
	}

	public String[] getAppendClause() {
		return appendClause;
	}
	
}