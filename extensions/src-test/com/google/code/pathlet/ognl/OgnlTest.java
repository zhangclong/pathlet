package com.google.code.pathlet.ognl;

import java.sql.Timestamp;

import ognl.Ognl;
import ognl.OgnlContext;

import org.junit.Test;



public class OgnlTest {
	
	@Test
	public void testBasicExpression()  {
		
		SegmentVo vo = new SegmentVo();
		vo.setName("hahaha!");
		vo.setSegmentId(100L);
		vo.setCalCountTime(new Timestamp(System.currentTimeMillis()));
		
		String[] values = {"aaa", "bbb"};
		String a1 = "a1";
		
		try {
			OgnlContext context = new OgnlContext();
			context.put("values", vo);
			//context.setValues(value)
			context.put("a1", a1);

			
			Object value = Ognl.getValue("segmentId", context, vo);
			System.out.println("value=" + value);
			System.out.println("valueType=" + value.getClass().getCanonicalName());
			
			Object value2 =  Ognl.getValue("#fib =:[#this>10 ? 'large10' : 'less10'], #fib(11)", context, vo);
			System.out.println("value2=" + value2);
			System.out.println("value2Type=" + value2.getClass().getCanonicalName());
			
			System.out.println(Ognl.getValue("#root.name", context, vo));
			System.out.println(Ognl.getValue("#a1", context, vo));
			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
