package com.wanda.ccs.sqlasm.expression;

import java.beans.PropertyDescriptor;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanda.ccs.sqlasm.CriteriaParseException;
import com.wanda.ccs.sqlasm.convert.BeanInfoWrapper;
import com.wanda.ccs.sqlasm.convert.Property;

public class JsonCriteriaHelper {
	
	/**
	 * 把json字符串格式为 {key1: value1, key2: value2 ...} 
	 * 转换为bean的属性进行自动赋值，赋值规则找出 key1同名的属性，把属性赋值为value1的转换值。
	 * 转换值的方式和Criterion的value转换类似。根据bean的属性类型自动进行转换。
	 * 如果找不到匹配类型就会抛出CriteriaParseException
	 * @param json
	 * @param beanClass
	 * @return bean object. The bean will be created new instance.
	 * @throws CriteriaParseException
	 */
	public static <T> T parseSimple(String json, Class<T> beanClass) throws CriteriaParseException {
		List<ExpressionCriterion> criteria = parseSimple(json);
		
		T bean = null;
		try {
			bean = beanClass.newInstance();
		} 
		catch (Exception e) {
			new CriteriaParseException(e);
		}
		
		BeanInfoWrapper wrapper = new BeanInfoWrapper(beanClass);
		
		for(ExpressionCriterion cri : criteria) {
			PropertyDescriptor propDesc = wrapper.getPropertyDesc(cri.getId());
			if(propDesc == null) {
				throw new CriteriaParseException("Failed to found the property name='" + cri.getId() + ", in class '" + beanClass + "'");
			}
			
			Property prop = new Property(beanClass, propDesc);
	
			Object propValue = convertValue(prop.getType(), cri);
			prop.setProperty(bean, propValue);
		}
		
		return bean;
	}
	
	private static Object convertValue(Class<?> propType, ExpressionCriterion cri) {
		Object value = null;
		if(Collection.class.isAssignableFrom(propType)) {
			value = ((ArrayExpCriterion)cri).getValue();
		}
		else if(CompositeValue.class.isAssignableFrom(propType)) {
			value =((CompositeExpCriterion)cri).getValue();
		}
		else if(String.class.isAssignableFrom(propType)) {
			value = ((SingleExpCriterion)cri).getValue();
		}
		else {
			try {
				if(cri.isEmpty() == false) {
					SingleExpCriterion singleCri = (SingleExpCriterion)cri;
					
					if(Date.class.isAssignableFrom(propType))	{
						//根据日期字符串的长度，来判断是日期类型，还是日期时间类型。
						if(singleCri.getValue().length() > 11) {
							value = DateUtils.parseMediumDateTime(singleCri.getValue());
						}
						else {
							value = DateUtils.parseMediumDate(singleCri.getValue());
						}
					}
					else if((propType == Integer.class) || (propType == Integer.TYPE)) {
						value = new Integer(singleCri.getValue());
					}
					else if((propType == Boolean.class) || (propType == Boolean.TYPE)) {
						value = new Boolean(singleCri.getValue());
					}
					else if((propType == Short.class) || (propType == Short.TYPE)) {
						value = new Short(singleCri.getValue());
					}
					else if((propType == Long.class) || (propType == Long.TYPE)) {
						value = new Long(singleCri.getValue());
					}
					else if((propType == Float.class) || (propType == Float.TYPE)) {
						value = new Float(singleCri.getValue());
					}
					else if((propType == Double.class) || (propType == Double.TYPE)) {
						value = new Double(singleCri.getValue());
					}
					else {
						throw new CriteriaParseException("Not support value parse for class: '" + propType + " for bean property");
					}
				}
			
			}
			catch(Exception e) {
				throw new CriteriaParseException("Faile to convert bean property value, propertyName=" + cri.getId() + ", propertyType=" + propType, e);
			}
		}

		return value;
	}
	

	/**
	 * Parse the json format like this: {key1:value1, key2:value2} 
	 * It will skip the empty value.
	 * @param json
	 * @return
	 */
	public static List<ExpressionCriterion> parseSimple(String json) {
		JsonNode rootNode = createJsonRootNode(json);
		List<ExpressionCriterion> criteria = new ArrayList<ExpressionCriterion>();
		
		if(rootNode.size() > 0) {
			Iterator<Map.Entry<String, JsonNode>> it = rootNode.fields();
			while(it.hasNext()) {
				
				Map.Entry<String, JsonNode> entry = it.next();

				String inputId = entry.getKey();
				JsonNode valueNode = entry.getValue();
				
				if(valueNode != null && valueNode.isNull() == false) {
					ExpressionCriterion ec = parseCriterionValue(inputId, null, null, null, valueNode);
					if(ec.isEmpty() == false) {
						criteria.add(ec);
					}
				}
			}
		}
		
		return criteria;
	}
	
	public static List<ExpressionCriterion> parse(String json) throws CriteriaParseException {
		return parseCriteria(createJsonRootNode(json));
	}
	
	private static JsonNode createJsonRootNode(String json) throws CriteriaParseException {
		try {
			JsonFactory jsonFactory = new JsonFactory(); // or, for data binding, org.codehaus.jackson.mapper.MappingJsonFactory
			JsonParser jp = jsonFactory.createJsonParser(json);
			jp.enable(Feature.ALLOW_COMMENTS);
			jp.enable(Feature.ALLOW_UNQUOTED_FIELD_NAMES);
			jp.enable(Feature.ALLOW_SINGLE_QUOTES);
			
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readTree(jp);
		} 
		catch (Exception e) {
			throw new CriteriaParseException("Failed to parse json content! ", e);
		}
	}
	
	private static List<ExpressionCriterion> parseCriteria(JsonNode node)  throws CriteriaParseException {
		List<ExpressionCriterion> criteria = new ArrayList<ExpressionCriterion>();
		int size = node.size();
		for(int i=0 ; i<size ; i++) {
			JsonNode criterionNode = node.get(i);
			ExpressionCriterion c = parseCriterion(criterionNode);
			if(c != null) {
				criteria.add(c);
			}
		}
		return criteria;
	}

	private static ExpressionCriterion parseCriterion(JsonNode node) throws CriteriaParseException {
		String inputId = node.get("inputId").asText().trim();
		String label = node.path("label").asText().trim();
		String groupId = null, groupLabel = null;
		if(node.get("groupId") != null) {
			groupId = node.get("groupId").asText().trim();
		}
		
		Operator op = Operator.getInstance(node.get("operator").asText().trim());
		JsonNode valueNode = node.get("value");
		if(valueNode != null && valueNode.isNull() == false) {
			return parseCriterionValue(inputId, label, groupId, op, valueNode);
		}
		else {
			return null;
		}
	}
	
	private static ExpressionCriterion parseCriterionValue(String inputId, String label, String groupId, Operator op, JsonNode valueNode) 
			throws CriteriaParseException {
		if(valueNode.isTextual()) {
			String value = valueNode.asText();
			return new SingleExpCriterion(inputId, label, groupId, op, value);
		}
		else if(valueNode.isArray()) {
			int size = valueNode.size();
			List<String> value = new ArrayList<String>(size);
			for(int i=0 ; i<size ; i++) {
				value.add(valueNode.get(i).asText());
			}
			return new ArrayExpCriterion(inputId, label, groupId, op, value);
		}
		else if(valueNode.isObject()) {
			boolean selTarget = valueNode.get("selTarget").asBoolean();
			List<ExpressionCriterion> criteria =  parseCriteria(valueNode.get("criteria"));
			JsonNode selValueNode = valueNode.get("selections").get("value");
			JsonNode selValueLableNode = valueNode.get("selections").get("valueLabel");

			List<String> selValue = new ArrayList<String>();
			if(selValueNode != null) {
				int size = selValueNode.size();
				for(int i=0 ; i<size ; i++) {
					selValue.add(selValueNode.get(i).asText());
				}
			}
			
			List<String> selValueLable = new ArrayList<String>();
			if(selValueLableNode != null) {
				int labelSize = selValueLableNode.size();
				for(int i=0 ; i<labelSize ; i++) {
					selValueLable.add(selValueLableNode.get(i).asText());
				}
			}

			return new CompositeExpCriterion(inputId, label, groupId, op, 
					new CompositeValue(selTarget, criteria, selValue, selValueLable));
		}
		else {
			throw new CriteriaParseException("Error json node format, node:" + valueNode);
		}
	}
	
}
