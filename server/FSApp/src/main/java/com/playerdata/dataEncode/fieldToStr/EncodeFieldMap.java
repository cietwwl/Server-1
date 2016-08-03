package com.playerdata.dataEncode.fieldToStr;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.dataEncode.ClassInfo4Encode;
import com.playerdata.dataEncode.DataEncodeHelper;
import com.playerdata.dataEncode.IFieldToStr;
import com.playerdata.dataSyn.json.FieldType;
import com.playerdata.dataSyn.json.FieldTypeHelper;

public class EncodeFieldMap implements IFieldToStr {

	private Field field;

	private FieldType valueGenericType;
	private FieldType keyGenericType;
	private ClassInfo4Encode genericClassInfo;

	public EncodeFieldMap(Field fieldP) {
		field = fieldP;
		Class<?> keyGenericClass = FieldTypeHelper.getGenericClass(fieldP);
		Class<?> valueGenericClass = FieldTypeHelper.getSecondGenericClass(fieldP);
		keyGenericType = FieldTypeHelper.getFieldType(keyGenericClass);
		valueGenericType = FieldTypeHelper.getFieldType(valueGenericClass);
		if(valueGenericType ==  FieldType.Class){
			genericClassInfo = new ClassInfo4Encode(valueGenericClass);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toStr(Object target) throws Exception {
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

		return valueMap.size()>0 ? DataEncodeHelper.mapToStr(valueMap):null;
	}
	

	
	
	@SuppressWarnings("rawtypes")
	private String getEntryStrValue(Object entryValue) throws Exception{
		String strValue = null;
		switch (valueGenericType) {
		case Class:
			strValue = genericClassInfo.toStr(entryValue);
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
	
	
}
