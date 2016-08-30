package com.rw.dataSyn.json.fieldToJson;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.rw.dataSyn.ClassInfo;
import com.rw.dataSyn.JsonUtil;
import com.rw.dataSyn.json.FieldType;
import com.rw.dataSyn.json.FieldTypeHelper;
import com.rw.dataSyn.json.IFieldToJson;

public class FieldMap implements IFieldToJson {

	private Field field;

	private FieldType valueGenericType;
	private FieldType keyGenericType;
	private ClassInfo genericClassInfo;

	public FieldMap(Field fieldP) {
		field = fieldP;
		Class<?> keyGenericClass = FieldTypeHelper.getGenericClass(fieldP);
		Class<?> valueGenericClass = FieldTypeHelper.getSecondGenericClass(fieldP);
		keyGenericType = FieldTypeHelper.getFieldType(keyGenericClass);
		valueGenericType = FieldTypeHelper.getFieldType(valueGenericClass);
		if(valueGenericType ==  FieldType.Class){
			genericClassInfo = new ClassInfo(valueGenericClass);
		}
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public String toJson(Object target) throws Exception {
		Object objectValue = field.get(target);
		if (objectValue == null) {
			return null;
		}

		Map<String, String> valueMap = new HashMap<String, String>();

		Map<Object, Object> objectMap = (Map<Object, Object>) objectValue;
		Set<Entry<Object, Object>> entrySet = objectMap.entrySet();
		for (Entry<Object, Object> entry : entrySet) {
			Object entryKey = entry.getKey();
			Object entryValue = entry.getValue();
			String strValue = getEntryStrValue(entryValue);
			String strKey = getEntryStrKey(entryKey);

			if(StringUtils.isNotBlank(strKey) && StringUtils.isNotBlank(strValue)){
				
				valueMap.put(strKey, strValue);
			}
		}

		return valueMap.size()>0 ? JsonUtil.writeValue(valueMap):null;
	}
	
	@SuppressWarnings("rawtypes")
	private String getEntryStrValue(Object entryValue) throws Exception{
		String strValue = null;
		switch (valueGenericType) {
		case Class:
			strValue = genericClassInfo.toJson(entryValue);
			break;
		case Enum:
			int enumInt = ((Enum) entryValue).ordinal();
			strValue = String.valueOf(enumInt);
			break;
		case Primitive:
			strValue = String.valueOf(entryValue);
			break;
		case String:
			strValue = (String) entryValue;
			break;
		case List:
			// do nothing 不支持
			break;
		case Map:
			// do nothing 不支持
			break;

		default:
			// do nothing 不支持
			break;
		}
		return strValue;
	}
	@SuppressWarnings("rawtypes")
	private String getEntryStrKey(Object entryKey) throws Exception{
		String strValue = null;
		switch (keyGenericType) {
		case Enum:
			int enumInt = ((Enum) entryKey).ordinal();
			strValue = String.valueOf(enumInt);
			break;
		case Primitive:
			strValue = String.valueOf(entryKey);
			break;
		case String:
			strValue = (String) entryKey;
			break;
		case List:
			// do nothing 不支持
			break;
		case Map:
			// do nothing 不支持
			break;
			
		default:
			// do nothing 不支持
			break;
		}
		return strValue;
	}

	@Override
	public String getLogInfo() {
		StringBuilder info = new StringBuilder();
		info.append("field Name:").append(field.getName());
		return info.toString();
	}

	@Override
	public void fromJson(Object target, String json) throws Exception {
		Map<Object,Object> resultData = new HashMap<Object, Object>();
		Map<String,String> tableData = JsonUtil.readToMap(json);

		for(String keyTmp : tableData.keySet()){			
			String jsonTmp = tableData.get(keyTmp);
			
			Object valueTmp = null;			
			
			switch (valueGenericType) {
				case Class:
					valueTmp = genericClassInfo.fromJson(jsonTmp);
					break;
				case Enum:
					valueTmp = FieldTypeHelper.toEnumValue(field.getType(), jsonTmp);
					break;
				case Primitive:
					valueTmp = FieldTypeHelper.ToPrimitiveValue(field.getType(), jsonTmp);
					break;
				case String:
					valueTmp = jsonTmp;
					break;
				case List:
					//do nothing 不支持
					break;
				case Map:
					//do nothing 不支持
					break;
				default:
					//do nothing 不支持
					break;
			}	
			
			if(valueTmp!=null){
				resultData.put(keyTmp, valueTmp);
			}
		}

		if(resultData.size()>0){
			field.set(target, resultData);
		}
		
	}
	
}
