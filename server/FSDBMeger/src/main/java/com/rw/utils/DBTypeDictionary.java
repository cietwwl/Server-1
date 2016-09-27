package com.rw.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * DB类型字典
 * @author lida
 *
 */
public class DBTypeDictionary {
	public final static Map<String, Class<?>> MySQLTypeDictionary = new HashMap<String, Class<?>>();
	
	static{
		MySQLTypeDictionary.put("VARCHAR", String.class);
		MySQLTypeDictionary.put("CHAR", String.class);
		MySQLTypeDictionary.put("BLOB", byte[].class);
		MySQLTypeDictionary.put("TEXT", String.class);
		MySQLTypeDictionary.put("INT", Integer.class);
		MySQLTypeDictionary.put("INTEGER", Integer.class);
		MySQLTypeDictionary.put("TINYINT", Integer.class);
		MySQLTypeDictionary.put("SMALLINT", Integer.class);
		MySQLTypeDictionary.put("MEDIUMINT", Integer.class);
		MySQLTypeDictionary.put("BIT", Boolean.class);
		MySQLTypeDictionary.put("BIGINT", Long.class);
		MySQLTypeDictionary.put("FLOAT", Float.class);
		MySQLTypeDictionary.put("DOUBLE", Double.class);
		MySQLTypeDictionary.put("DECIMAL", BigDecimal.class);
		MySQLTypeDictionary.put("ID", Long.class);
		MySQLTypeDictionary.put("DATE", Date.class);
		MySQLTypeDictionary.put("TIME", Time.class);
		MySQLTypeDictionary.put("DATETIME", Timestamp.class);
		MySQLTypeDictionary.put("TIMESTAMP", Timestamp.class);
		MySQLTypeDictionary.put("YEAR", Date.class);
		MySQLTypeDictionary.put("MEDIUMTEXT", String.class);
		MySQLTypeDictionary.put("LONGTEXT", String.class);
	}
}
