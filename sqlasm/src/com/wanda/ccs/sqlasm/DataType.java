package com.wanda.ccs.sqlasm;

public enum DataType {
    INTEGER,
    LONG,
    FLOAT,
    DOUBLE,
    SHORT,
    DATE,
    DATE_TIME,
    STRING,
    SQL,  //此特殊类型，变量作为字符串直接插入到语句中
    BOOLEAN
}

