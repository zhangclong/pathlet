package com.google.code.pathlet.web.widget;


public interface ResultRowMapper<F> {
	F convert(F row);
}
