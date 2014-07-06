package com.google.code.pathlet.web.json;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


public class DateJsonSerializer extends JsonSerializer<Date> {
	

	private SimpleDateFormat sdf;


	public void serialize(Date value, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {

		String str = sdf.format(value);
		generator.writeString(str);
	}

	public DateJsonSerializer(String pattern) {
		sdf = new SimpleDateFormat(pattern);
	}
	
	public DateJsonSerializer() {  }
	
	public void setFormatPattern(String pattern) {
		sdf = new SimpleDateFormat(pattern);
	}
	
}
