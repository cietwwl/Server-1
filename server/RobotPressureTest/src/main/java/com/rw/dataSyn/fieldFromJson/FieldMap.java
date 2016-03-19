package com.rw.dataSyn.fieldFromJson;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.dataSyn.ClassInfo;
import com.rw.dataSyn.EnumHelper;
import com.rw.dataSyn.FieldTypeHelper;
import com.rw.dataSyn.JsonUtil;


//只支持基本类型的list
public class FieldMap implements IFieldFromJson
{

	private Field field;

	private ClassInfo genericClass;
	
	private Type keyType;
	private Type valueType;

	public FieldMap (Field fieldP)
	{
		field = fieldP;
		Type genericType = field.getGenericType();
		if (genericType != null && (genericType instanceof ParameterizedType)) {
			ParameterizedType pt = (ParameterizedType) genericType;

			keyType = pt.getActualTypeArguments()[0];
			valueType = pt.getActualTypeArguments()[1];
		}
		
			
		if(!FieldTypeHelper.isPrimitive(valueType) 
				&&!FieldTypeHelper.isString(valueType)
				&&!FieldTypeHelper.isList(valueType)
				&&!FieldTypeHelper.isMap(valueType)
				&&!FieldTypeHelper.isEnum(valueType)){
			
			genericClass = new ClassInfo(fieldP.getType());
		}	


	}
	
	public void FromJson (Object target, String json){

        Map<Object,Object> resultData = new HashMap<Object,Object>();

		Map<String,String> jsonMap = JsonUtil.readToMap(json);
		
		Iterator<Entry<String, String>> enumerator = jsonMap.entrySet().iterator();
		


		while(enumerator.hasNext()){
			Entry<String, String> next = enumerator.next();
			String keyStr = next.getKey();
			String jsonTmp = next.getValue();

            Object valueTmp = null;
			Object keyTmp = null;
			
			if(FieldTypeHelper.isEnum(keyType)){
				Class<?> keyClazz = (Class<?>)keyType;
				keyTmp = EnumHelper.getByOrdinal(keyClazz, Integer.valueOf(keyStr));
			}else if(FieldTypeHelper.isString(keyType)){
				keyTmp = keyStr;
            }else{
            	keyTmp = FieldTypeHelper.ToPrimitiveValue( keyType, keyStr);
            }
			
			if(genericClass!=null){
            	valueTmp = genericClass.FromJson(jsonTmp);
            }else if(FieldTypeHelper.isEnum(valueType)){
				Class<?> valueClazz = (Class<?>)valueType;
				valueTmp = EnumHelper.getByOrdinal(valueClazz, Integer.valueOf(jsonTmp));
			}else if(FieldTypeHelper.isString(valueType)){
				valueTmp = jsonTmp;
            }else if(FieldTypeHelper.isPrimitive(valueType)){
            	valueTmp = FieldTypeHelper.ToPrimitiveValue( valueType, jsonTmp);
            }

			if(keyTmp!=null && valueTmp!=null){
				resultData.put(keyTmp, valueTmp);
			}

		}

		if(resultData.size()>0){
            try {
				field.set(target, resultData);
			} catch (Exception e) {
				throw(new RuntimeException("FieldMap[FromJson] error fieldName:"+field.getName()+" json:"+json, e));
			}
		}
	}
	

}
