package com.rwbase.common.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import com.common.RefParam;
import com.log.GameLog;
import com.rw.fsutil.common.EnumIndex;
import com.rw.fsutil.common.EnumName;

public class CfgCsvHelper {
	public static <T> Map<String, T> readCsv2Map(String configFileName, Class<T> clazzP){
		return readCsv2Map(configFileName,clazzP,null,null);
	}
	
	public static <T> Map<String, T> readCsv2Map(String configFileName, Class<T> clazzP,
			RefParam<Map<String, Field>> outFieldMap) {
		return readCsv2Map(configFileName,clazzP,null,outFieldMap);
	}
	
	public static <T> Map<String, T> readCsv2Map(String configFileName, Class<T> clazzP, RefParam<String[]> headers,
			RefParam<Map<String, Field>> outFieldMap) {
		HashMap<String, T> map = new HashMap<String, T>();
		try {
			Map<String, Field> fieldMap = getFieldMap(clazzP);
			if (outFieldMap != null) outFieldMap.value = fieldMap;

			String csvFn = CfgCsvHelper.class.getResource("/config/"+configFileName).getFile();
			File csvFile = new File(csvFn);
			
			InputStreamReader InputStreamReader = new InputStreamReader(new FileInputStream(csvFile), "UTF-8");			
			
			CSVParser parser = new CSVParser(InputStreamReader, CSVFormat.DEFAULT);
			boolean firstRecord = true;
			String[] fieldNameArray = null;
			for (CSVRecord csvRecord : parser) {
				
				if(firstRecord){
					firstRecord = false;
					fieldNameArray = getFieldNameArray(csvRecord);
					if (headers != null) headers.value = fieldNameArray;
				}else{
					T newInstance = clazzP.newInstance();
					T cfg = createFromCsv(fieldNameArray, csvRecord, newInstance,fieldMap);
					if (map.put(csvRecord.get(0), cfg)!=null){
						GameLog.error("配置错误", configFileName, "重复的关键字:"+csvRecord.get(0));
					}
					map.put(csvRecord.get(0), cfg);
				}
			}
			
			parser.close();
			InputStreamReader.close();
		} catch (Exception e) {
			throw(new RuntimeException("配置表错误：" + configFileName+e.getMessage()));
		}
		

		return map;
	}

	private static String[] getFieldNameArray(CSVRecord csv) {
		int size = csv.size();
		String[] fieldNameArray = new String[size];
		for (int i =0; i < fieldNameArray.length; i++) {
			fieldNameArray[i] = csv.get(i);
		}
		return fieldNameArray;
	}

	private static <T> T createFromCsv(String[] fieldNameArray, CSVRecord csv,T newInstance,
			Map<String, Field> fieldMap) throws Exception {
		try {
			// String[] fieldNameArray = getFieldNameArray(csv);
			for (int i = 0; i < fieldNameArray.length; i++) {
				String fieldName = fieldNameArray[i];
				Field field = fieldMap.get(fieldName);
				if(field!=null){					
					String strvalue = csv.get(i);
					
					if(field.getType() == String.class || StringUtils.isNotBlank(strvalue)){
						try {
							
							Object value = parsStr(field, strvalue);
							field.set(newInstance, value);
						} catch (Exception e) {
							parsStr(field, strvalue);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return newInstance;
	}

	public static Map<String, Field> getFieldMap(Class<?> clazzP) {
		Map<String, Field> fieldMap = new HashMap<String, Field>();
		Field[] fields = clazzP.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			fieldMap.put(field.getName(), field);
		}
		
		// HC @Modify 2015-12-05 如果不是Object为父才检测
		Class<?> superclass = clazzP.getSuperclass();
		if (superclass != Object.class) {
			Field[] superfields = superclass.getDeclaredFields();
			for (Field field : superfields) {
				field.setAccessible(true);
				fieldMap.put(field.getName(), field);
			}
		}
		return fieldMap;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object parsStr(Field field, String strvalue) {
		Object value = null;
		Class<?> fieldType = field.getType();

		if (fieldType.isEnum()){
			if (field.isAnnotationPresent(EnumName.class)) {
				value = Enum.<Enum>valueOf((Class<Enum>)fieldType, strvalue.trim());
			}else if (field.isAnnotationPresent(EnumIndex.class)) {
				int index = Integer.parseInt(strvalue);
				Object[] enumLst=fieldType.getEnumConstants();
				value = enumLst[index];
			}else{
				try {
					value = Enum.<Enum>valueOf((Class<Enum>)fieldType, strvalue.trim());
				} catch (Exception e) {
					int index = Integer.parseInt(strvalue);
					Object[] enumLst=fieldType.getEnumConstants();
					value = enumLst[index];
				}
			}
		}else if (fieldType == boolean.class || fieldType == Boolean.class){
			value = Boolean.parseBoolean(strvalue);
		}else if (fieldType == String.class) {
			value = strvalue;
		} else if (fieldType == int.class || fieldType == Integer.class) {
			value = Integer.parseInt(trim(strvalue));
		} else if (fieldType == long.class || fieldType == Long.class) {
			value = Long.parseLong(trim(strvalue));
		} else if (fieldType == float.class || fieldType == Float.class) {
			value = Float.parseFloat(trim(strvalue));
		} else if (fieldType == double.class || fieldType == Double.class) {
			value = Double.parseDouble(trim(strvalue));
		}

		return value;

	}
	private static String trim(String valueStr){
		// in case"1,000"
		return valueStr.replace(",", "");
	}

}
