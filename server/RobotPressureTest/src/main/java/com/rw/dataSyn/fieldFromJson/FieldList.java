package com.rw.dataSyn.fieldFromJson;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.type.JavaType;

import com.rw.dataSyn.ClassInfo;
import com.rw.dataSyn.EnumHelper;
import com.rw.dataSyn.FieldTypeHelper;
import com.rw.dataSyn.JsonUtil;


//只支持基本类型的list
public class FieldList implements IFieldFromJson
{

	private Field field;

	private ClassInfo genericClass;
	
	private Type genericType;

	public FieldList (Field fieldP)
	{
		field = fieldP;
		Type genericTypeTmp = field.getGenericType();
		if (genericTypeTmp != null && (genericTypeTmp instanceof ParameterizedType)) {
			ParameterizedType pt = (ParameterizedType) genericTypeTmp;

			genericType = pt.getActualTypeArguments()[0];
		}
		
		
		
		if(!FieldTypeHelper.isPrimitive(genericType) 
				&&!FieldTypeHelper.isString(genericType)
				&&!FieldTypeHelper.isList(genericType)
				&&!FieldTypeHelper.isMap(genericType)
				&&!FieldTypeHelper.isEnum(genericType)){
			
			genericClass = new ClassInfo((Class<?>)genericType);
		}	


	}

    public void FromJson(Object target, String json) {
    	try {
    		List<Object> objectList = new ArrayList<Object>();
    		
    		List<String> listData = JsonUtil.readList(json, String.class);
    		
    		for (String jsonTmp : listData) {
    			
    			Object valueTmp = null;
    			if(genericClass!=null){
    				valueTmp = genericClass.FromJson(jsonTmp);
    			}else if(FieldTypeHelper.isPrimitive(genericType)){
    				valueTmp = FieldTypeHelper.ToPrimitiveValue(genericType, jsonTmp);
    			}else if(FieldTypeHelper.isString(genericType)){
    				valueTmp = jsonTmp;
    			}else if(FieldTypeHelper.isEnum(genericType)){
    				Class<?> valueClazz = (Class<?>)genericType;
    				valueTmp = EnumHelper.getByOrdinal(valueClazz, Integer.valueOf(jsonTmp));
    			}
    			if(valueTmp!=null){
    				objectList.add(valueTmp);
    			}
    		}
    		
    		
    		if(objectList.size()>0){
    			field.set(target, objectList);
    		}
			
		} catch (Exception e) {
			throw(new RuntimeException("FieldList[FromJson] error fieldName:"+field.getName()+" json:"+json, e));
		}
	
	}

	
	

}
