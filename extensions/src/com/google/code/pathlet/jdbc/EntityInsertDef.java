package com.google.code.pathlet.jdbc;

public class EntityInsertDef {
	
	private String id;
	
	private Class<?> entityClass;
	
	private String tableName;
	
	private Key[] generatedKeys;      //Key property names
	
	private String[] excludes;        //Excludes property names

	public EntityInsertDef(String id, Class<?> entityClass, String tableName, Key[] generatedKeys, String[] excludes) {
		this.id = id;
		this.entityClass = entityClass;
		this.tableName = tableName;
		this.generatedKeys = generatedKeys;
		this.excludes = excludes;
	}
	
	public EntityInsertDef(String id, Class<?> entityClass, String tableName, Key[] generatedKeys) {
		this(id, entityClass, tableName, generatedKeys, null);
	}
	
	public EntityInsertDef(String id, Class<?> entityClass, String tableName) {
		this(id, entityClass, tableName, null, null);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Key[] getGeneratedKeys() {
		return generatedKeys;
	}

	public void setGeneratedKeys(Key[] generatedKeys) {
		this.generatedKeys = generatedKeys;
	}

	public String[] getExcludes() {
		return excludes;
	}

	public void setExcludes(String[] excludes) {
		this.excludes = excludes;
	}
	
	
	public static class Key {
		private String key;
		private String valueSql;

		public Key(String key, String valueSql) {
			super();
			this.key = key;
			this.valueSql = valueSql;
		}

		public String getKey() {
			return key;
		}

		public String getValueSql() {
			return valueSql;
		}
	}
}