package com.fy.db;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Transient;

public class ClassInfo {

	private Class<?> clazz;

	private Map<String, Field> filedNameMap = new HashMap<String, Field>();


	public Object newInstance() throws Exception {
		return clazz.newInstance();
	}

	public ClassInfo(Class<?> clazzP) {
		try {
			init(clazzP);
		} catch (Exception e) {
			throw (new RuntimeException("初始化ClassInfo失败 clazzP:" + clazzP.toString(), e));
		}
	}

	private void init(Class<?> clazzP) throws IntrospectionException, Exception {
		this.clazz = clazzP;

		Field[] fields = clazzP.getDeclaredFields();
		for (Field field : fields) {
			if (!field.isAnnotationPresent(Transient.class)) {
				field.setAccessible(true);
				String fieldName = field.getName();
				filedNameMap.put(fieldName, field);

			}
		}
	}


	public Collection<Field> getFields() {
		return filedNameMap.values();
	}

	public Field getField(String name) {
		return filedNameMap.get(name);
	}


}
