/*
 * .
 * ProjectName:JCCP
 * File name:  DateJsonSrzer.java     
 * Author: Administrator  
 * Project:JCCP    
 * Version: v 1.0      
 * Date: 2011 2011-2-12 上午09:14:32 
 * Description:     
 * Function List:  
 * 
 */ 

package com.google.code.pathlet.web.json;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


public class DateJsonSerializer extends JsonSerializer<Date> {
	
	/** 变量描述：简单的日期格式化器. */
	private SimpleDateFormat sdf;

	/**
	  * 实现说明：将日期类型，转换为指定格式的json串. <BR/>
	  * @param value
	  * @param generator
	  * @param provider
	  * @throws IOException
	  * @throws JsonProcessingException
	  * @see com.google.code.pathlet.web.json.ibm.base.json.DateJsonSerializer.util.common.DateJsonSrzer
	  * @since 
	  */
	public void serialize(Date value, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
		//格式化日期
		String str = sdf.format(value);
		
		//生成json
		generator.writeString(str);
	}

	/**
	  * 构造说明：根据日期模式，构造简单的日志格式化器. <BR/>
	  * @param pattern
	  * @see com.google.code.pathlet.web.json.ibm.base.json.DateJsonSerializer.util.common.DateJsonSrzer.java
	  * @since 
	  */
	public DateJsonSerializer(String pattern) {
		sdf = new SimpleDateFormat(pattern);
	}
	
	public DateJsonSerializer() {  }
	
	public void setFormatPattern(String pattern) {
		sdf = new SimpleDateFormat(pattern);
	}
	
}
