package com.wanda.ccs.sqlasm.expression;

import java.util.List;

import com.wanda.ccs.sqlasm.Criterion;

/**
 * The value could be three types: <br/>
 *  1. Object(String, Integer, Long, Date ...): it is string in json.<br/>
 *  2. List&lt;String&gt;: it is array in json.<br/>
 *  3. ComositeValue: it is composite definition object in JSon.<br/> 
 *     A instance of composite definition JSon: <br/>
<pre>
{
	"selTarget" : true,
	"criteria" : [ {
		"inputId" : "username",
		"groupId": "member",
		"groupLabel" "会员基本",
		"label" : "用户名",
		"operator" : "eq",
		"value" : "",
		"valueLabel" : ""
	},
	{
		"inputId" : "email",
		"groupId": "member",
		"groupLabel" "会员基本",
		"label" : "电子邮件",
		"operator" : "eq",
		"value" : "",
		"valueLabel" : ""
	}, 
	{
		"inputId" : "gender",
		"groupId": "member",
		"groupLabel" "会员基本",
		"label" : "性别",
		"operator" : "in",
		"value" : [],
		"valueLabel" : []
	} 
	],
	"selections" : {
		"value" : [ "user8", "user9" ],
		"valueLabel" : [ "用户8", "用户9" ]
	}
}
</pre>
 */
public interface ExpressionCriterion<V> extends Criterion<V> {

	/**
	 * 传入对应的表达式操作符
	 * @return
	 */
	Operator getOp();
	
	/**
	 * 用于前端传入criterion的分组区别。
	 * @return
	 */
	String getGroupId();
	
	/**
	 * 用于前端传入名称,仅作记录和显示使用.
	 * @return
	 */
	String getLabel();
	
}
