package com.wanda.ccs.sqlasm.expression;


public enum Operator {
	EQUAL("eq"),     //consale.BOOK_DATE_KEY {op} {@SQL@toInt(value)}
	NOT_EQUAL("ne"), //consale.BOOK_DATE_KEY {op} {@SQL@toString(value)}
	GREAT_THAN("gt"), 
	LESS_THAN("lt"), 
	GREAT_THAN_EQUAL("ge"), 
	LESS_THAN_EQUAL("le"), 
	LIKE("like"),    //consale.BOOK_DATE_KEY {op} {@SQL@toString(op, value)}
	BETWEEN("between"), 
	NOT_BETWEEN("nbetween"),
	INCLUDE("in"),   //consale.BOOK_DATE_KEY {op} {@SQL@toString(value)}
	NOT_INCLUDE("nin");
	
	private Operator(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public static Operator getInstance(String key) {
		for (Operator op : Operator.values()) {
			if (key.equals(op.key)) {
				return op;
			}
		}
		return null;
	}
	
	private String key;

}
