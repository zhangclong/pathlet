package com.wanda.ccs.sqlasm.expression;

import static com.wanda.ccs.sqlasm.expression.ExpressionCriterionType.ARRAY;
import static com.wanda.ccs.sqlasm.expression.ExpressionCriterionType.COMPOSITE;
import static com.wanda.ccs.sqlasm.expression.ExpressionCriterionType.SINGLE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wanda.ccs.sqlasm.ClauseResult;
import com.wanda.ccs.sqlasm.CriteriaParseException;
import com.wanda.ccs.sqlasm.Criterion;
import com.wanda.ccs.sqlasm.DataType;
import com.wanda.ccs.sqlasm.impl.BaseClause;



/**
 * 
 * 把Criterion中的value属性作为变量替换clause中的特殊标记。
 * 分句中可以放入类似这样的变量"{0}", "{1}"。其中的0,1表示变换的索引值。
 * 如果Criterion为单值变量则只有"{0}"起作用，如果Criterion是数组变量则括弧中的数字表示数组的索引。
 * 
 * 其中的变量值可以再指定值映射方式。如Criterion中可以返回的值有 "01"和"02"，但我们拼入的分句要替换为"is null"和"is not null"
 * 可调用addValueMapper(Integer, ValueMapper)或addValueMapper(Integer, final Map<Object, Object>)函数加入值映射.
 * 如： 
 * <pre>
 *  Clause clause = new ValueClause("where", "idColumn  {0}", DataType.STRING,  true);
 * Map<Object, Object> mapper = new HashMap<Object, Object>();
 * mapper.put("01", "is null");
 * mapper.put("02", "is not null");
 * clause.addValueMapper(0, mapper);
 *
 * 也可以这样写：
	clause.addValueMapper(0, new ValueMapper() {
				public Object convert(Object value) {
					if(value.equals("01") {//是
						return "is null";
					}
					else if(value.equals("02") {//否
						return "is not null";
					}
					return null;
				}
			}
		);
 * 
 * </pre>
 * 
 * @author Charlie Zhang
 *
 */
public class ValueClause extends BaseClause {
	
	private DataType valueType;
	
	private Map<Integer, ValueMapper> valueMappers;
	
	private boolean injectValue;
	
	public ValueClause() {
		this.valueMappers = new HashMap<Integer, ValueMapper>();
	}

	public ClauseResult toResult(Criterion crit) throws CriteriaParseException {

		if(crit.isEmpty() == false) {
			
			int parameterCount = 0;
			
			ExpressionCriterionType type = CriteriaUtils.getCriteriaType((ExpressionCriterion)crit);
			
			
			if(type == COMPOSITE ) {
				throw new CriteriaParseException("ValueClause do not support composite value! clause=" + getClause());
			}
			
			//create and add the parameters
			List<String> parameters;
			if(type == SINGLE) {
				SingleExpCriterion singleCrit = (SingleExpCriterion)crit;
				parameters = new ArrayList<String>(1);
				parameters.add(singleCrit.getValue());
			}
			else if(type == ARRAY) {
				parameters = ((ArrayExpCriterion)crit).getValue();
			}
			else {
				parameters = null;
				assert false; //Should never go here!
			}
	
			return compose(parameters);
		}
		else {
			throw new CriteriaParseException("Criterion could not be null!");
		}
		
	}

	
	private ClauseResult compose(List<String> parameters) {
		List<Object> retParameters = new ArrayList<Object>();
		List<DataType> retParameterTypes = new ArrayList<DataType>();
		
		if(parameters != null && parameters.size() > 0) {
			String clause = getClause();
			int len = clause.length();
			int beginIdx = -1;
			StringBuilder retBuf = new StringBuilder();
			StringBuilder paramBuf = new StringBuilder();
			for(int i=0 ; i<len ; i++) {
				char c = clause.charAt(i);
				boolean endFlag = false;
				if(c == '{') {
					beginIdx = i;
				} else if(beginIdx >=0 && c == '}') {
					String paramValue = paramBuf.toString(); // 获取{}中的参数值
					//get value 
					//Object value = null;
					
					whereParamSql(paramValue, parameters, retBuf, retParameters, retParameterTypes);
					
					beginIdx = -1;
					endFlag = true;
					paramBuf = new StringBuilder();
				}
				
				if(beginIdx < 0 && endFlag == false) {
					retBuf.append(c);
				} else if(beginIdx >= 0 && i > beginIdx) {
					paramBuf.append(c);
				}
			}
			return new ClauseResult(this, retBuf.toString(), retParameters, retParameterTypes);
		}
		
		return new ClauseResult(this, this.getClause(), null, null);
	}
	
	/**
	 * 根据表达式来处理sql条件拼装
	 * 
	 * @param expression
	 * @param parameters
	 * @param retBuf
	 * @param retParameters
	 * @param retParameterTypes
	 * @return
	 */
	private void whereParamSql(String expression, List<String> parameters, StringBuilder retBuf, List<Object> retParameters, List<DataType> retParameterTypes) {
		Object value = null;
		
		if(regex(expression, "^[0-9]{1}$")) {		//匹配数字表达式
			parseRegex1(expression, parameters, retBuf, retParameters, retParameterTypes);
		} else if(regex(expression, "^\\*{1}$")) {	//匹配*表达式
			parseRegex2(parameters, retBuf, retParameters, retParameterTypes);
		} else if(regex(expression, "^\\*{1}\\|[a-z]{2,3}$")) {	//匹配*|[and,or]表达式
			parseRegex3(expression, parameters, retBuf, retParameters, retParameterTypes);
		} else {
			throw new CriteriaParseException("not find parse expression");
		}
	}
	
	/**
	 * 根据正则条件进行判断
	 * 
	 * @param expression
	 * @param regexStr
	 * @return
	 */
	private boolean regex(String expression, String regexStr) {
		Pattern pattern = Pattern.compile(regexStr);
		Matcher matcher = pattern.matcher(expression.trim());
		return matcher.find();
	}
	
	/**
	 * 解析表达式1
	 * 
	 * @param expression
	 * @param parameters
	 * @param isSql
	 * @return
	 */
	private void parseRegex1(String expression, List<String> parameters, StringBuilder retBuf, List<Object> retParameters, List<DataType> retParameterTypes) {
		Object value = null;
		
		int paramIdx = Integer.parseInt(expression);
		if(paramIdx >= parameters.size()) {
			throw new CriteriaParseException("Parameter index is out of bounds! clause=" + clause);
		}
		Object convertedParam = CriteriaUtils.convertParameter(null, valueType, parameters.get(paramIdx));
		ValueMapper mapper = valueMappers.get(paramIdx);
		if(mapper != null) {
			value = mapper.convert(convertedParam);
			if(value == null) {
				throw new CriteriaParseException("ValueClause could not get the value=" + parameters.get(paramIdx) + " from ValueMapper! clause=" + getClause());
			}
		} else {
			value = convertedParam;
		}
		
		//不是插入SQL方式
		if(injectValue == false) {
			retParameters.add(value);
			retParameterTypes.add(valueType);
			retBuf.append('?');
		} else {//是插入SQL方式
			retBuf.append(CriteriaUtils.parameterToSql(value, valueType));
		}
	}
	
	/**
	 * 如果表达式参数为{*}，那么将所有参数直接输出。例如1,2,3,4
	 * 
	 * @param parameters
	 * @return
	 */
	private void parseRegex2(List<String> parameters, StringBuilder retBuf, List<Object> retParameters, List<DataType> retParameterTypes) {
		for(int i=0,len=parameters.size();i<len;i++) {			
			
			//不是插入SQL方式
			if(injectValue == false) {
				Object value = null;
				if(valueMappers != null && valueMappers.size() > 0) {	// 如果配置了mapper转换类
					for(Map.Entry<Integer, ValueMapper> entry : valueMappers.entrySet()) {
						value = entry.getValue().convert(parameters.get(i));
					}
				} else {
					value = parameters.get(i);
				}
				retParameters.add(value);
				retParameterTypes.add(valueType);
				retBuf.append('?').append(",");
			} else {//是插入SQL方式
				if(valueMappers != null && valueMappers.size() > 0) {	// 如果配置了mapper转换类
					for(Map.Entry<Integer, ValueMapper> entry : valueMappers.entrySet()) {
						retBuf.append(CriteriaUtils.parameterToSql(entry.getValue().convert(parameters.get(i)), valueType)).append(",");
					}
				} else {
					retBuf.append(CriteriaUtils.parameterToSql(parameters.get(i), valueType)).append(",");
				}				
			}
		}
		
		if(retBuf.length() > 0) {
			retBuf.deleteCharAt(retBuf.length() - 1);
		}
	}
	
	/**
	 * 解析表达式3
	 * 
	 * @param parameters
	 * @param isSql
	 * @return
	 */
	private void parseRegex3(String expression, List<String> parameters, StringBuilder retBuf, List<Object> retParameters, List<DataType> retParameterTypes) {
		String[] exp = expression.split("\\|");
		
		for(int i=0,len=parameters.size();i<len;i++) {
			
			//不是插入SQL方式
			if(injectValue == false) {
				Object value = null;
				if(valueMappers != null && valueMappers.size() > 0) {	// 如果配置了mapper转换类
					for(Map.Entry<Integer, ValueMapper> entry : valueMappers.entrySet()) {
						value = entry.getValue().convert(parameters.get(i));
					}
				} else {
					value = parameters.get(i);
				}
				retParameters.add(value);
				retParameterTypes.add(valueType);
				retBuf.append('?').append(" "+exp[1]+" ");
			} else {//是插入SQL方式
				if(valueMappers != null && valueMappers.size() > 0) {	// 如果配置了mapper转换类
					for(Map.Entry<Integer, ValueMapper> entry : valueMappers.entrySet()) {
						retBuf.append(CriteriaUtils.parameterToSql(entry.getValue().convert(parameters.get(i)), valueType)).append(" "+exp[1]+" ");
					}
				} else {
					retBuf.append(CriteriaUtils.parameterToSql(parameters.get(i), valueType)).append(" "+exp[1]+" ");
				}
			}
		}
		
		if(retBuf.length() > 0) {
			retBuf.delete(retBuf.length() - (exp[1].length()+2), retBuf.length());
		}
		
	}
	
	////////////////////////////////////////////////////////
	//下面为DSL模式函数定义，这些函数的返回值都为this
	////////////////////////////////////////////////////////
	public ValueClause from(String criterionId) {
		super.from(criterionId);
		return this;
	}
	
	public ValueClause output(String clause, DataType valueType, boolean injectValue) {
		this.clause = clause;
		this.valueType = valueType;
		this.injectValue = injectValue;
		return this;
	}

	public ValueClause in(String paragraphId, boolean distinct) {
		super.in(paragraphId, distinct);
		return this;
	}

	public ValueClause in(String paragraphId) {
		super.in(paragraphId);
		return this;
	}
	
	public static interface ValueMapper {
		Object convert(Object value);
		//Object[] keys();
	}
	
	public ValueClause addValueMapper(Integer valueIndex, ValueMapper mapper) {
		valueMappers.put(valueIndex, mapper);
		return this;
	}
	
	public ValueClause addValueMapper(Integer valueIndex, final Map<Object, Object> mapper) {
		valueMappers.put(valueIndex, new ValueMapper() {
				public Object convert(Object value) {
					return mapper.get(value);
				}

				/*public Object[] keys() {
					return mapper.keySet().toArray();
				}*/
			}
		);
		
		return this;
	}
	
}
