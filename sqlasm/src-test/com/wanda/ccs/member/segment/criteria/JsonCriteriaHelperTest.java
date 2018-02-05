package com.wanda.ccs.member.segment.criteria;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;


import com.wanda.ccs.sqlasm.expression.ArrayExpCriterion;
import com.wanda.ccs.sqlasm.expression.CompositeExpCriterion;
import com.wanda.ccs.sqlasm.expression.CompositeValue;
import com.wanda.ccs.sqlasm.expression.ExpressionCriterion;
import com.wanda.ccs.sqlasm.expression.JsonCriteriaHelper;
import com.wanda.ccs.sqlasm.expression.Operator;
import com.wanda.ccs.sqlasm.expression.SingleExpCriterion;
import com.wanda.ccs.sqlasm.util.ClassPathResource;
import com.wanda.ccs.sqlasm.util.ClassUtils;
import com.wanda.ccs.sqlasm.util.IOUtils;

public class JsonCriteriaHelperTest {

	@Test
	public void testParse() throws Exception {
		ClassLoader cl = this.getClass().getClassLoader();
		String packagePath = ClassUtils.getPackageName(this.getClass()).replace('.', '/');
		File jsonFile = (new ClassPathResource(packagePath + "/JsonCriteriaHelperTest.js", cl)).getFile();
		
		String jsonContent = IOUtils.toString(new FileInputStream(jsonFile), "UTF-8");
		List<ExpressionCriterion> criteria = JsonCriteriaHelper.parse(jsonContent);
		
		assertEquals(criteria.size(), 4);
		
		CompositeExpCriterion userc = (CompositeExpCriterion)criteria.get(0);
		assertEquals(userc.getId(), "user");
		assertEquals(userc.getLabel(), "选择用户");
		assertEquals(userc.getOp(), Operator.INCLUDE);
		CompositeValue cv = userc.getValue();
		assertEquals(cv.getCriteria().size(), 3);
		ExpressionCriterion usernameCriterion = cv.getCriteria().get(0);
		assertEquals(usernameCriterion.getId(), "username");
		assertEquals(usernameCriterion.getLabel(), "用户名");
		assertEquals(usernameCriterion.getOp(), Operator.EQUAL);
		assertEquals((String)usernameCriterion.getValue(), "");
		
		ArrayExpCriterion arrayc = (ArrayExpCriterion)criteria.get(1);
		assertEquals(arrayc.getId(), "noDisturb");
		assertEquals(arrayc.getLabel(), "是否希望被联络");
		assertEquals(arrayc.getOp(), Operator.INCLUDE);
		assertEquals(((List<String>)arrayc.getValue()).get(0), "1");
		assertEquals(((List<String>)arrayc.getValue()).get(1), "2");

		SingleExpCriterion singlec = (SingleExpCriterion)criteria.get(2);
		assertEquals(singlec.getId(), "conAmount");
		assertEquals(singlec.getLabel(), "卖品消费金额");
		assertEquals(singlec.getOp(), Operator.EQUAL);
		assertEquals((String)singlec.getValue(), "100");
		
	}
	
	@Test
	public void testParseSimple() throws Exception {
		ClassLoader cl = this.getClass().getClassLoader();
		String packagePath = ClassUtils.getPackageName(this.getClass()).replace('.', '/');
		File jsonFile = (new ClassPathResource(packagePath + "/JsonCriteriaHelperTest-simple.js", cl)).getFile();
		String jsonContent = IOUtils.toString(new FileInputStream(jsonFile), "UTF-8");
		
		Map<String, Operator> queryOperatorMap = new LinkedHashMap<String, Operator>();

		
		List<ExpressionCriterion> criteria = JsonCriteriaHelper.parseSimple(jsonContent);
		
		assertEquals(criteria.size(), 4);
		
		ExpressionCriterion c1= criteria.get(0);
		assertEquals(c1.getId(), "code");
		assertEquals(c1.getValue(), "001");
		
		ExpressionCriterion c2= criteria.get(1);
		assertEquals(c2.getId(), "name");
		assertEquals(c2.getValue(), "张三");
		
		ExpressionCriterion c3= criteria.get(2);
		assertEquals(c3.getId(), "updateTimeFrom");
		assertEquals(c3.getValue(), "2013-07-02 00:00:00");
		
		ExpressionCriterion c4= criteria.get(3);
		assertEquals(c4.getId(), "updateTimeTo");
		assertEquals(c4.getValue(), "2013-07-06 00:00:00");
	}

}
