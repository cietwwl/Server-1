package com.rw.db.dao.annotation;

import java.lang.reflect.Field;

import org.codehaus.jackson.type.JavaType;

public class FieldEntry {
	
	public final Field field;
	public final String columnName;  //对应数据库中的列名
	public final boolean saveAsJson; // 是否以saveAsJson的方式保存，如果存在combineSave,则此标志失效
	public final JavaType collectionType; // 如果是指定的集合类型，会存储对应JavaType(暂时只支持3种)
	public final boolean isPrimaryKey;	//是否主键
	
	public FieldEntry(Field field, String columnName,boolean saveAsJson, JavaType type,boolean isId) {
		this.field = field;
		this.saveAsJson = saveAsJson;
		this.collectionType = type;
		this.isPrimaryKey = isId;
		this.columnName = columnName;
	}

}
