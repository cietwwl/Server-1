package com;

import java.lang.reflect.Field;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.rwbase.common.attrdata.AttrData;

public class EncodeClassCreator {

	public static void main(String[] args) {
		Class<AttrData> clazz = AttrData.class;
		Field[] fields = clazz.getDeclaredFields();
		StringBuilder strBld = new StringBuilder();
		String clazzName = clazz.getSimpleName();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if(!field.isAnnotationPresent(IgnoreSynField.class))
			strBld.append(clazzName).append("-").append(field.getName()).append(";");
		}
		System.out.println(strBld);
	}
}
