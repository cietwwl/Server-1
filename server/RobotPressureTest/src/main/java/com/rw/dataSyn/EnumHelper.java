package com.rw.dataSyn;

import java.util.HashMap;
import java.util.Map;

public class EnumHelper {

	private static Map<Class<?>, Object[]> enumConstanMap = new HashMap<Class<?>, Object[]>();
	
	
	public static Object getByOrdinal(Class<?> enumClazz, int ordinal){
		if(!enumConstanMap.containsKey(enumClazz)){
			enumConstanMap.put(enumClazz, enumClazz.getEnumConstants());
		}
		
		Object[] enumConstants = enumConstanMap.get(enumClazz);
		return enumConstants[ordinal];
		
	}
	
}
