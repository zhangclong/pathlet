package com.wanda.ccs.sqlasm.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.wanda.ccs.sqlasm.ClauseResult;
import com.wanda.ccs.sqlasm.CriteriaParseException;
import com.wanda.ccs.sqlasm.CriteriaParser;
import com.wanda.ccs.sqlasm.CriteriaResult;
import com.wanda.ccs.sqlasm.Criterion;
import com.wanda.ccs.sqlasm.DataType;
import com.wanda.ccs.sqlasm.impl.BaseClause;

import static com.wanda.ccs.sqlasm.expression.ExpressionCriterionType.*;


/**
 * 
 * 根据传入的Criterion的数据，条件性的组装子句。
 * 根据toResult()函数中传入的Criterion值不同进行拼装。Criterion中的op和value属性会参与拼装。
 * 
 * 如果Criterion为null, 或者Criterion的子属性value为null则不进行拼装toResult()返回null.
 * 
 * @author Charlie Zhang
 *
 */
public class ExpressionClause extends BaseClause {
	
	private Operator operator = null;
	
	private DataType valueType;
	
	private CriteriaParser compositeParser;

	/*
	public ExpressionClause(String paragraphId, String clause, DataType valueType, CriteriaParser compositeParser, Operator op, boolean distinct) {
		super(paragraphId, clause, distinct);
		this.valueType = valueType;
		this.compositeParser = compositeParser;
		this.operator = op;
	}
	
	public ExpressionClause(String paragraphId, String clause, DataType valueType, Operator op, boolean distinct) {
		super(paragraphId, clause, distinct);
		this.valueType = valueType;
		this.compositeParser = null;
		this.operator = op;
	}
	
	public ExpressionClause(String paragraphId, String clause, DataType valueType, CriteriaParser compositeParser, boolean distinct) {
		super(paragraphId, clause, distinct);
		this.valueType = valueType;
		this.compositeParser = compositeParser;
	}
	
	public ExpressionClause(String paragraphId, String clause, DataType valueType, boolean distinct) {
		super(paragraphId, clause, distinct);
		this.valueType = valueType;
		this.compositeParser = null;
	}
	*/
	
	public ClauseResult toResult(Criterion crit) throws CriteriaParseException {
		
		ExpressionCriterion expcrit = (ExpressionCriterion)crit;
		ExpressionCriterionType type = getCriteriaType(expcrit);
		
		Operator op = (this.operator != null) ? this.operator : expcrit.getOp();
			
		validate(type, op, expcrit);
		
		StringBuilder composed = new StringBuilder();
		List<Object> parameters = new ArrayList<Object>();
		List<DataType> parameterTypes = new ArrayList<DataType>();
		
		if(crit.isEmpty() == false) {
		
			//如果是组合查询，存储组合查询的解析结果
			CriteriaResult compositeResult = null;
			
			int parameterCount = 0;
			
			//create and add the parameters
			if( type == COMPOSITE && ((CompositeValue)expcrit.getValue()).isSelTarget() == false) {

				if(compositeParser == null) {
					throw new CriteriaParseException("Failed to find the composite definition! Please check the clause: inputId="
							+ crit.getId());
				}
				compositeResult = compositeParser.parse(((CompositeValue)expcrit.getValue()).getCriteria());
				if(compositeResult.getParameters() != null) {
					parameters.addAll(compositeResult.getParameters());
					parameterTypes.addAll(compositeResult.getParameterTypes());
					parameterCount = compositeResult.getParameters().size();
				}
			}
			else {
				if( type == SINGLE) {
					Object param = CriteriaUtils.convertParameter(op, valueType, ((String)expcrit.getValue()));
					if(param != null) {
						parameters.add(param);
						parameterTypes.add(valueType);
						parameterCount = 1;
					}
				}
				else if( type == ARRAY) {
					List<String> values = (List<String>)expcrit.getValue();
					for(String value : values) {
						Object param = CriteriaUtils.convertParameter(op, valueType, value);
						if(param != null) {
							parameters.add(param);
							parameterTypes.add(valueType);
							parameterCount ++;
						}
					}
				}
				else if(type == COMPOSITE) {
					List<String> values = ((CompositeValue)expcrit.getValue()).getSelections().getValue();
					for(String value : values) {
						Object param = CriteriaUtils.convertParameter(op, valueType, value);
						if(param != null) {
							parameters.add(param);
							parameterTypes.add(valueType);
							parameterCount ++;
						}
					}
				}
				else {
					assert false; //Should never go here!
				}
			}
			
			if(parameterCount > 0) {
				if(compositeResult != null && compositeResult.isEmpty() == false) {
					compose(composed, getClause(), op, compositeResult.getComposedText(), parameterCount);
				}
				else {
					compose(composed, getClause(), op, null, parameterCount);
				}
				
				return new ClauseResult(this, composed.toString(), parameters, parameterTypes);
			}
		}
		
		return null;
	}
	

	private void validate(ExpressionCriterionType type, Operator op, ExpressionCriterion crit) {
		if(op == null) {
			throw new CriteriaParseException("The operator has not define in Clause:[" + this.toString() + "] !");
		}
		if(op == Operator.LIKE && this.valueType != DataType.STRING) {
			throw new CriteriaParseException("The 'like' operator could not support the type other than STRING! Clause:[" + this.toString() + "] !");
		}
		
		if(type == COMPOSITE && (!(op == Operator.EQUAL || op == Operator.INCLUDE || op == Operator.NOT_INCLUDE)) ) {
			throw new CriteriaParseException("The operator of Clause:[" + this.toString() + "] must be EQUAL, INCLUDE or NOT_INCLUDE!");
		}
		
		if((op == Operator.INCLUDE || op == Operator.NOT_INCLUDE) && 
				(type != COMPOSITE && type != ARRAY)) {
			throw new CriteriaParseException("The value of Clause:[" + this.toString() + "] must be array values!");
		}
		
		if((op == Operator.BETWEEN || op == Operator.NOT_BETWEEN) &&
			(type != ARRAY || (type == ARRAY && ((List<String>)crit.getValue()).size() != 2)  )) {
				throw new CriteriaParseException("For operator is " + op + ", the value of Clause:[" + this.toString() + "] must contains 2 values! " );
		}
	}


	private ExpressionCriterionType getCriteriaType(ExpressionCriterion crit) {
		if(crit instanceof SingleExpCriterion) {
			return ExpressionCriterionType.SINGLE;
		}
		else if(crit instanceof ArrayExpCriterion) {
			return ExpressionCriterionType.ARRAY;
		}
		else if(crit instanceof CompositeExpCriterion) {
			return ExpressionCriterionType.COMPOSITE;
		}
		else {
			throw new CriteriaParseException("Could not support the ExpressionCriterion type: " + crit.getClass().getCanonicalName());
		}
	}
	
	
	private void compose(StringBuilder buf, String left, Operator op, String subQuerySql, int paramCount) {
        switch(op) {
            case EQUAL:
            	buf.append(left);
                buf.append("=");
                break;
            case NOT_EQUAL:
            	buf.append(left);
                buf.append("!=");
                break;
            case GREAT_THAN:
            	buf.append(left);
                buf.append(">");
                break;
            case LESS_THAN:
            	buf.append(left);
                buf.append("<");
                break;
            case GREAT_THAN_EQUAL:
            	buf.append(left);
                buf.append(">=");
                break;
            case LESS_THAN_EQUAL:
            	buf.append(left);
                buf.append("<=");
                break;
            case LIKE:
                buf.append(left);
                buf.append(" LIKE ");
                break;
            case BETWEEN:
            	buf.append(left).append(" between ");
            	break;
            case NOT_BETWEEN:
            	buf.append(left).append(" not between ");
            	break;
            case INCLUDE:
            	buf.append(left);
	            buf.append(" in ");
                break;
            case NOT_INCLUDE:
                buf.append(left);
                buf.append(" not in ");
            default:
                assert false; //it will never go here
                break;
        }
        
		if(subQuerySql != null) {
			buf.append("(").append(subQuerySql).append(")");
		}
		else {
			if(op == Operator.INCLUDE || op == Operator.NOT_INCLUDE) {
				buf.append("(");
                for(int i=0 ; i<paramCount ; i++) {
                	if(i == paramCount-1) {
                		buf.append("?");
                	}
            		else {
            			buf.append("?,");
            		}
                }
                buf.append(")");
			}
			else if(op == Operator.BETWEEN || op == Operator.NOT_BETWEEN) {
				buf.append("? and ?");
			}
			else {
				buf.append("?");
			}
		}

	}
	
	
	////////////////////////////////////////////////////////
	//下面为DSL模式函数定义，这些函数的返回值都为this
	////////////////////////////////////////////////////////
	public ExpressionClause from(String criterionId) {
		super.from(criterionId);
		return this;
	}
	
	public ExpressionClause output(String clause, DataType valueType) {
		this.clause = clause;
		this.valueType = valueType;
		return this;
	}

	public ExpressionClause output(String clause, DataType valueType, CriteriaParser compositeParser) {
		this.clause = clause;
		this.valueType = valueType;
		this.compositeParser = compositeParser;
		return this;
	}
	
	public ExpressionClause output(String clause, DataType valueType, Operator operator) {
		this.clause = clause;
		this.valueType = valueType;
		this.operator = operator;
		return this;
	}

	public ExpressionClause output(String clause, DataType valueType, CriteriaParser compositeParser, Operator operator) {
		this.clause = clause;
		this.valueType = valueType;
		this.compositeParser = compositeParser;
		this.operator = operator;
		return this;
	}
	
	public ExpressionClause in(String paragraphId,
			boolean distinct) {
		super.in(paragraphId, distinct);
		return this;
	}

	public ExpressionClause in(String paragraphId) {
		super.in(paragraphId);
		return this;
	}

	
}
