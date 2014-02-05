/**
 * Copyright ....
 */
package com.google.code.pathlet.jdbc;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;

/**
 * 
 * @author Charlie Zhang
 * 
 * @param <T>
 */
public class EntityRowMapper<T> implements RowMapper<T> {

	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	/** The class we are mapping to */
	private Class<T> mappedClass;

	/** Whether we're strictly validating */
	private boolean checkFullyPopulated = false;

	/** Whether we're defaulting primitives when mapping a null value */
	private boolean primitivesDefaultedForNullValue = false;

	/** Map of the fields we provide mapping for */
	private Map<String, PropertyDescriptor> mappedFields;

	/** Set of bean properties we provide mapping for */
	private Set<String> mappedProperties;

	/** Convertor for convert bean property name to data column name */
	private final EntityPropertyConvertor propertyConvertor;

	public EntityRowMapper(Class<T> mappedClass) {
		this(mappedClass, null);
	}
	
	/**
	 * Create a new BeanPropertyRowMapper, accepting unpopulated properties in
	 * the target bean.
	 * <p>
	 * Consider using the {@link #newInstance} factory method instead, which
	 * allows for specifying the mapped type once only.
	 * 
	 * @param mappedClass
	 *            the class that each row should be mapped to
	 */
	public EntityRowMapper(Class<T> mappedClass,
			String[] excludeProperties) {
		this.propertyConvertor = new DefaultEntityPropertyConvertor();
		initialize(mappedClass, excludeProperties);
	}

	/**
	 * Create a new BeanPropertyRowMapper.
	 * 
	 * @param mappedClass
	 *            the class that each row should be mapped to
	 * @param checkFullyPopulated
	 *            whether we're strictly validating that all bean properties
	 *            have been mapped from corresponding database fields
	 */
	public EntityRowMapper(EntityPropertyConvertor propertyConvertor,
			Class<T> mappedClass, String[] excludeProperties,
			boolean checkFullyPopulated) {
		this.propertyConvertor = propertyConvertor;
		this.checkFullyPopulated = checkFullyPopulated;
		initialize(mappedClass, excludeProperties);
	}

	/**
	 * Initialize the mapping metadata for the given class.
	 * 
	 * @param mappedClass
	 *            the mapped class.
	 */
	protected void initialize(Class<T> mappedClass, String[] excludeProperties) {
		this.mappedClass = mappedClass;
		this.mappedFields = new HashMap<String, PropertyDescriptor>();
		this.mappedProperties = new HashSet<String>();
		PropertyDescriptor[] pds = BeanUtils
				.getPropertyDescriptors(mappedClass);

		Set<String> excludePropertiesSet = new HashSet<String>();
		if (excludeProperties != null && excludeProperties.length > 0) {
			for (String prop : excludeProperties) {
				excludePropertiesSet.add(prop);
			}
		}

		for (PropertyDescriptor pd : pds) {
			if (pd.getWriteMethod() != null
					&& excludePropertiesSet.contains(pd.getName()) == false) {
				String underscoredName = propertyConvertor.propertyToColumn(pd
						.getName());
				this.mappedFields.put(underscoredName, pd);
				this.mappedProperties.add(pd.getName());
			}
		}
	}

	/**
	 * Set whether we're strictly validating that all bean properties have been
	 * mapped from corresponding database fields.
	 * <p>
	 * Default is <code>false</code>, accepting unpopulated properties in the
	 * target bean.
	 */
	public void setCheckFullyPopulated(boolean checkFullyPopulated) {
		this.checkFullyPopulated = checkFullyPopulated;
	}

	/**
	 * Return whether we're strictly validating that all bean properties have
	 * been mapped from corresponding database fields.
	 */
	public boolean isCheckFullyPopulated() {
		return this.checkFullyPopulated;
	}

	/**
	 * Set whether we're defaulting Java primitives in the case of mapping a
	 * null value from corresponding database fields.
	 * <p>
	 * Default is <code>false</code>, throwing an exception when nulls are
	 * mapped to Java primitives.
	 */
	public void setPrimitivesDefaultedForNullValue(
			boolean primitivesDefaultedForNullValue) {
		this.primitivesDefaultedForNullValue = primitivesDefaultedForNullValue;
	}

	/**
	 * Return whether we're defaulting Java primitives in the case of mapping a
	 * null value from corresponding database fields.
	 */
	public boolean isPrimitivesDefaultedForNullValue() {
		return primitivesDefaultedForNullValue;
	}

	/**
	 * Extract the values for all columns in the current row.
	 * <p>
	 * Utilizes public setters and result set metadata.
	 * 
	 * @see java.sql.ResultSetMetaData
	 */
	public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
		Assert.state(this.mappedClass != null, "Mapped class was not specified");
		T mappedObject = BeanUtils.instantiate(this.mappedClass);
		BeanWrapper bw = PropertyAccessorFactory
				.forBeanPropertyAccess(mappedObject);

		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Set<String> populatedProperties = (isCheckFullyPopulated() ? new HashSet<String>()
				: null);

		for (int index = 1; index <= columnCount; index++) {
			String column = JdbcUtils.lookupColumnName(rsmd, index);
			PropertyDescriptor pd = this.mappedFields.get(column.toUpperCase());
			if (pd != null) {
				try {
					Object value = getColumnValue(rs, index, pd);
					if (logger.isDebugEnabled() && rowNumber == 0) {
						logger.debug("Mapping column '" + column
								+ "' to property '" + pd.getName()
								+ "' of type " + pd.getPropertyType());
					}
					try {
						bw.setPropertyValue(pd.getName(), value);
					} catch (TypeMismatchException e) {
						if (value == null && primitivesDefaultedForNullValue) {
							logger.debug("Intercepted TypeMismatchException for row "
									+ rowNumber
									+ " and column '"
									+ column
									+ "' with value "
									+ value
									+ " when setting property '"
									+ pd.getName()
									+ "' of type "
									+ pd.getPropertyType()
									+ " on object: " + mappedObject);
						} else {
							throw e;
						}
					}
					if (populatedProperties != null) {
						populatedProperties.add(pd.getName());
					}
				} catch (NotWritablePropertyException ex) {
					throw new DataRetrievalFailureException(
							"Unable to map column " + column + " to property "
									+ pd.getName(), ex);
				}
			}
		}

		if (populatedProperties != null
				&& !populatedProperties.equals(this.mappedProperties)) {
			throw new InvalidDataAccessApiUsageException(
					"Given ResultSet does not contain all fields "
							+ "necessary to populate object of class ["
							+ this.mappedClass + "]: " + this.mappedProperties);
		}

		return mappedObject;
	}

	/**
	 * Retrieve a JDBC object value for the specified column.
	 * <p>
	 * The default implementation calls
	 * {@link JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)}.
	 * Subclasses may override this to check specific value types upfront, or to
	 * post-process values return from <code>getResultSetValue</code>.
	 * 
	 * @param rs
	 *            is the ResultSet holding the data
	 * @param index
	 *            is the column index
	 * @param pd
	 *            the bean property that each result object is expected to match
	 *            (or <code>null</code> if none specified)
	 * @return the Object value
	 * @throws SQLException
	 *             in case of extraction failure
	 * @see org.springframework.jdbc.support.JdbcUtils#getResultSetValue(java.sql.ResultSet,
	 *      int, Class)
	 */
	protected Object getColumnValue(ResultSet rs, int index,
			PropertyDescriptor pd) throws SQLException {
		return JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
	}

}
