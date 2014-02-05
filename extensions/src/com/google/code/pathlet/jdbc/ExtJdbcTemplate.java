package com.google.code.pathlet.jdbc;

import java.beans.PropertyDescriptor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.Assert;

import com.google.code.pathlet.jdbc.EntityInsertDef.Key;
import com.google.code.pathlet.util.ValueUtils;


public class ExtJdbcTemplate extends JdbcTemplate {
	
	public static boolean LOG_SQL = false;
	
	private static Log log = LogFactory.getLog(ExtJdbcTemplate.class);
	
	/** Convertor for convert bean property name to data column name */
	private final EntityPropertyConvertor propertyConvertor;
	
	private Map<String, Config> updateEntityCache = new HashMap<String, Config>();//Key is the ID of EntityDef
	
	private Map<String, Config> insertEntityCache = new HashMap<String, Config>();
	
	/** To indicate whether throw an EmptyResultDataAccessException encounting the  */
	private volatile boolean emptyDataException = false;
	
	public ExtJdbcTemplate() {
		super();
		this.propertyConvertor = new DefaultEntityPropertyConvertor();
	}

	public ExtJdbcTemplate(DataSource dataSource) {
		this(dataSource, new DefaultEntityPropertyConvertor(), true, false);
	}


	public ExtJdbcTemplate(DataSource dataSource, EntityPropertyConvertor propertyConvertor, boolean lazyInit, boolean emptyDataException) {
		super(dataSource, lazyInit);
		this.propertyConvertor = propertyConvertor;
		this.emptyDataException = emptyDataException;
	}

	public void registerInsertEntity(EntityInsertDef insertDef) {
		
		PropertyDescriptor[] propertyDescs = BeanUtils.getPropertyDescriptors(insertDef.getEntityClass());
		
		Set<String> excludePropertiesSet = new HashSet<String>();
		excludePropertiesSet.add("class"); //Java object "class" property must be exclude 
		if(insertDef.getExcludes() != null && insertDef.getExcludes().length > 0) {
			for(String prop : insertDef.getExcludes()) {
				excludePropertiesSet.add(prop);
			}
		}
		
		//Add the key properties into excludePropertiesSet
		Map<String, Key> generatedKeysMap = new HashMap<String, Key>();
		if(insertDef.getGeneratedKeys() != null && insertDef.getGeneratedKeys().length > 0) {
			for(Key generatedKey : insertDef.getGeneratedKeys()) {
				//Add the generatedKey as excluded field, if it has no value SQL to be designed.
				if( ValueUtils.isEmpty(generatedKey.getValueSql()) ) {
					excludePropertiesSet.add(generatedKey.getKey());
				}
				generatedKeysMap.put(generatedKey.getKey(), generatedKey);
			}
		}
		
		//Build the string for columns and columns' placeholders
		StringBuilder columnsBuf = new StringBuilder();
		StringBuilder placeholderBuf = new StringBuilder();
		
		ArrayList<String> argsNames = new ArrayList<String>(propertyDescs.length);
		
		//append keys 
		for(PropertyDescriptor propDesc : propertyDescs) {
			//Skip the java Object's property 'class', and designated excludes properties.
			if(excludePropertiesSet.contains(propDesc.getName()) == false) {
				Key generatedKey = generatedKeysMap.get(propDesc.getName());
				if(generatedKey != null) {
					columnsBuf.append(propertyConvertor.propertyToColumn(propDesc.getName())).append(",");
					placeholderBuf.append(generatedKey.getValueSql()).append(",");
				}
			}
		}
		
		for(PropertyDescriptor propDesc : propertyDescs) {
			//Skip the java Object's property 'class', and designated excludes properties.
			if(excludePropertiesSet.contains(propDesc.getName()) == false) {
				Key generatedKey = generatedKeysMap.get(propDesc.getName());
				if(generatedKey == null) {
					columnsBuf.append(propertyConvertor.propertyToColumn(propDesc.getName())).append(",");
					placeholderBuf.append("?,");
					argsNames.add(propDesc.getName());
				}
			}
		}
		columnsBuf.deleteCharAt(columnsBuf.length() - 1); //delete the last ',' from columns 
		placeholderBuf.deleteCharAt(placeholderBuf.length() - 1); //delete the last ',' from place holder
		
		//Create the insert SQL
		StringBuilder sqlBuf = new StringBuilder();
		sqlBuf.append("INSERT INTO ").append(insertDef.getTableName()).append(" (");
		sqlBuf.append(columnsBuf).append(") VALUES (").append(placeholderBuf).append(")");
		
		boolean holdGeneratedKey = false;
		if(insertDef.getGeneratedKeys() != null && insertDef.getGeneratedKeys().length > 0) {
			holdGeneratedKey = true;
		}
		
		Config config = new Config(sqlBuf.toString(), argsNames.toArray(new String[0]), 
				insertDef.getGeneratedKeys(), holdGeneratedKey);
		insertEntityCache.put(insertDef.getId(), config);

		
	}
	
	public void registerUpdateEntity(EntityUpdateDef updateDef) {
	
		PropertyDescriptor[] propertyDescs = BeanUtils.getPropertyDescriptors(updateDef.getEntityClass());
		
		Set<String> excludePropertiesSet = new HashSet<String>();
		excludePropertiesSet.add("class"); //Java object "class" property must be exclude 
		if(updateDef.getExcludes() != null && updateDef.getExcludes().length > 0) {
			for(String prop : updateDef.getExcludes()) {
				excludePropertiesSet.add(prop);
			}
		}
		
		//Add the condition properties into excludePropertiesSet
		if(updateDef.getConditions() != null && updateDef.getConditions().length > 0) {
			for(String condition : updateDef.getConditions()) {
				excludePropertiesSet.add(condition);
			}
		}
		
		//update T_USER set PASSWORD=?,NAME=?,EMAIL=? where USERNAME=?
		
		//Build the string for columns and columns' placeholders
		StringBuilder sqlBuf = new StringBuilder();
		sqlBuf.append("UPDATE ").append(updateDef.getTableName()).append(" set ");
		
		ArrayList<String> argsNames = new ArrayList<String>(propertyDescs.length);
		for(PropertyDescriptor propDesc : propertyDescs) {
			if(excludePropertiesSet.contains(propDesc.getName()) == false) {	
				String propName = propDesc.getName();
				sqlBuf.append(propertyConvertor.propertyToColumn(propName)).append("=?,");
				argsNames.add(propName);
			}
		}
		sqlBuf.deleteCharAt(sqlBuf.length() - 1); //delete the last ',' from columns 
		
		sqlBuf.append(" WHERE ");
		
		//append key properties into where conditions
		boolean appendConditions = false;
		for(String condition : updateDef.getConditions()) {		
			String conditionColumn = propertyConvertor.propertyToColumn(condition);
			sqlBuf.append(conditionColumn).append("=? AND ");
			argsNames.add(condition);
			appendConditions = true;
		}
		sqlBuf.delete(sqlBuf.length() - 5, sqlBuf.length());
		
		if(updateDef.getAppendClause() != null) {
			for(String clause : updateDef.getAppendClause()) {
				if(appendConditions == false) {
					sqlBuf.append(clause);
					appendConditions = true;
				}
				else {
					sqlBuf.append(" AND ").append(clause);
				}
			}
		}
		
		Config config = new Config(sqlBuf.toString(), argsNames.toArray(new String[0]));
		updateEntityCache.put(updateDef.getId(), config);
	}
	
	
	
	/**
	 * Automatic build the insert SQL by entity properties and invoke 
	 * the JdbcTemplate update() method to execute insert.
	 * 
	 * @param tableName
	 * @param enetity
	 * @return the number of rows affected
	 */
	public int insertEntity(String id, Object entity) throws DataAccessException {
		return executeEntityUpdate(insertEntityCache.get(id), entity, null);
	}
	
	/**
	 * Automatic build the update SQL by entity properties and invoke 
	 * the JdbcTemplate update() method to execute update.<br/>
	 * 
	 * @param id
	 * @param entity
	 * @return the number of rows affected
	 */
	public int updateEntity(String id, Object entity) throws DataAccessException {
		return executeEntityUpdate(updateEntityCache.get(id), entity, null);
	}

	/**
	 * Automatic build the update SQL by entity properties and invoke 
	 * the JdbcTemplate update() method to execute update.<br/>
	 * 
	 * @param id
	 * @param entity
	 * @param appendClauseParam append parameters which defined in applenClause property of EntityUpdateDef.
	 * @return
	 * @throws DataAccessException
	 */
	public int updateEntity(String id, Object entity, Object[] appendClauseParam) throws DataAccessException {
		return executeEntityUpdate(updateEntityCache.get(id), entity, appendClauseParam);
	}
	
	
	/**
	 * 
	 * @param config
	 * @param entity
	 * @param retrieveKeys whether retrieve the auto-generated keys
	 * @return the number of rows affected
	 * @since 2012-7-23 5:29:31
	 */
	private int executeEntityUpdate(final Config config, Object entity, Object[] appendClauseParam){
		//Get arguments array
		BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(entity);
		String[] propNames = config.getaArgProperties();
		int lenArguments = (appendClauseParam == null) ? propNames.length : propNames.length + appendClauseParam.length;
		
		Object[] propValues = new Object[lenArguments];
		
		for(int i=0 ; i<propNames.length ; i++) {
			propValues[i] = bw.getPropertyValue(propNames[i]);	
		}
		
		//叠加appendClauseParam中的值
		int appendParamIdx = 0;
		for(int i=propNames.length ; i<lenArguments ; i++) {
			propValues[i] = appendClauseParam[appendParamIdx++];	
		}
		
		if(config.isHoldGeneratedKey()) {
			PreparedStatementCreator psc = new SimplePreparedStatementCreator(config.getSql(), config.getKeyColumns());
			
			KeyHolder keyHolder = new GeneratedKeyHolder();
			
			logUpdateSql(config.getSql(), propValues);
			int ret = update(psc, newArgPreparedStatementSetter(propValues), keyHolder);

			//Set key properties's value. The key properties will be set one by one.
			Map<String, Object> keys = keyHolder.getKeys();
			if(keys != null && keys.size() > 0) {
				Iterator<Map.Entry<String, Object>> it = keys.entrySet().iterator();
				
				int keyIndex = 0;
				while(it.hasNext()) {
					Map.Entry<String, Object> keyEntry = it.next();
					String keyPropertyName = config.getKeyProperties()[keyIndex];
					//System.out.println(keyEntry.getValue());
					bw.setPropertyValue(keyPropertyName, keyEntry.getValue());
					keyIndex ++;
				}
			}
			
			return ret;
		}

		
		else {
			//Execute the SQL
			logUpdateSql(config.getSql(), propValues);
			return update(config.getSql(), propValues);
		}
	}
	
	private void logUpdateSql(String sql, Object[] arguments) {
		if(LOG_SQL) {
			log.info("SQL[" + sql + "], arguments" + Arrays.toString(arguments));
		}
	}
	
	protected int update(final PreparedStatementCreator psc, final PreparedStatementSetter pss, final KeyHolder generatedKeyHolder)
			throws DataAccessException {
		Assert.notNull(generatedKeyHolder, "KeyHolder must not be null");
		logger.debug("Executing SQL update and returning generated keys");
		return execute(psc, new PreparedStatementCallback<Integer>() {
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException {
				ResultSet keysResultSet = null;
				
				try {
					if (pss != null) {
						pss.setValues(ps);
					}
					int rows = ps.executeUpdate();

					List<Map<String, Object>> generatedKeys = generatedKeyHolder.getKeyList();
					generatedKeys.clear();
					keysResultSet = ps.getGeneratedKeys();
					if (keysResultSet != null) {
						RowMapperResultSetExtractor<Map<String, Object>> rse = 
								new RowMapperResultSetExtractor<Map<String, Object>>(getColumnMapRowMapper(), 1);
						generatedKeys.addAll(rse.extractData(keysResultSet));
					}
					if (logger.isDebugEnabled()) {
						logger.debug("SQL update affected " + rows + " rows and returned " + generatedKeys.size() + " keys");
					}
					
					return rows;
				}
				finally {
					JdbcUtils.closeResultSet(keysResultSet);
					
					if (pss instanceof ParameterDisposer) {
						((ParameterDisposer) pss).cleanupParameters();
					}
				}
			}
		});
	}

	/**
	 * 
	 * @param startIndex zero base index number to indicate the start row number
	 * @param fetchSize max data row size to be fetched
	 * @param sql 
	 * @param args arguments to be replace in java
	 * @return
	 * @throws DataAccessException
	 * @since 2012-8-22
	 */
	public List<Map<String, Object>> query(int startIndex, int fetchSize, String sql, Object[] args) throws DataAccessException {
		PreparedStatementCreator psc = new SimplePreparedStatementCreator(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		return query(psc, newArgPreparedStatementSetter(args),
				new ScrollableRowMapperResultSetExtractor<Map<String, Object>>(startIndex, fetchSize, getColumnMapRowMapper()));
	}
	
	public <T> List<T> query(int startIndex, int fetchSize, String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
		PreparedStatementCreator psc = new SimplePreparedStatementCreator(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		return query(psc, newArgPreparedStatementSetter(args),
				new ScrollableRowMapperResultSetExtractor<T>(startIndex, fetchSize, rowMapper));
	}
	
	
	
	public <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper)
			throws DataAccessException {
		try {
			return super.queryForObject(sql, args, rowMapper);
		} 
		catch(EmptyResultDataAccessException ere) {
			if(emptyDataException) { throw ere; }
			else { return null; }
		}
	}
	
	public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args)
			throws DataAccessException {
		try {
			return super.queryForObject(sql, rowMapper, args);
		} 
		catch(EmptyResultDataAccessException ere) {
			if(emptyDataException) { throw ere; }
			else { return null; }
		}
		
	}
	
	public <T> T queryForObject(String sql, Object[] args, int[] argTypes, Class<T> requiredType)
		    throws DataAccessException  {
		try {
			return super.queryForObject(sql, args, argTypes, requiredType);
		} 
		catch(EmptyResultDataAccessException ere) {
			if(emptyDataException) { throw ere; }
			else { return null; }
		}
	}
	
	public <T> T queryForObject(String sql, Object[] args, Class<T> requiredType) throws DataAccessException {
		try {
			return super.queryForObject(sql, args, requiredType);
		} 
		catch(EmptyResultDataAccessException ere) {
			if(emptyDataException) { throw ere; }
			else { return null; }
		}
	}

	public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) throws DataAccessException  {
		try {
			return super.queryForObject(sql, requiredType, args);
		} 
		catch(EmptyResultDataAccessException ere) {
			if(emptyDataException) { throw ere; }
			else { return null; }
		}
	}

	public Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes) throws DataAccessException  {
		try {
			return super.queryForMap(sql, args, argTypes);
		}
		catch(EmptyResultDataAccessException ere) {
			if(emptyDataException) { throw ere; }
			else { return null; }
		}
	}
	
	public Map<String, Object> queryForMap(String sql, Object... args) throws DataAccessException  {
		try {
			return super.queryForMap(sql, args);
		}
		catch(EmptyResultDataAccessException ere) {
			if(emptyDataException) { throw ere; }
			else { return null; }
		}
	}
	
	
	private class Config {
		
		String sql;
		
		String[] argProperties;
		
		/** Bean property names for primary keys */
		String[] keyProperties = null; 
		
		/** Column names for primary keys */
		String[] keyColumns = null;
		
		boolean holdGeneratedKey;
		
		public Config(String sql, String[] argProperties) {
			this(sql, argProperties, null, false);
		}
		
		public Config(String sql, String[] argProperties, Key[] generatedKeys, boolean holdGeneratedKey) {
			this.sql = sql;
			this.argProperties = argProperties;
			this.holdGeneratedKey = holdGeneratedKey;
			
			//convert keyProperties into keyColumns by propertyConvertor
			if(generatedKeys != null) {
				int len = generatedKeys.length;
				this.keyColumns = new String[len];
				this.keyProperties = new String[len];
				for(int i=0 ; i<len ; i++) {
					this.keyColumns[i] = propertyConvertor.propertyToColumn(generatedKeys[i].getKey());
					this.keyProperties[i] = generatedKeys[i].getKey();
				}
			}
		}

		public String getSql() {
			return sql;
		}
		
		public String[] getaArgProperties() {
			return argProperties;
		}

		public String[] getKeyProperties() {
			return keyProperties;
		}

		public String[] getKeyColumns() {
			return keyColumns;
		}

		public boolean isHoldGeneratedKey() {
			return holdGeneratedKey;
		}

	}
	
}
