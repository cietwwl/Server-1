package com.common.beanCopy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;

import org.apache.commons.lang3.StringUtils;

class CopyClassGener {
	
	private static CopyClassGener instance = new CopyClassGener();
	
	public static CopyClassGener getInstance(){
		return instance;
	}
	
	
	public ICopy genCopyer(Class<?> source, Class<?> target) throws Exception{
		String className = "com.fy.common.beanCopy."+source.getSimpleName()+"2"+target.getSimpleName();
		
		ClassPool pool = ClassPool.getDefault();
		CtClass cls = pool.makeClass(className);
		cls.addInterface(pool.get("com.common.beanCopy.ICopy"));
		
		CtClass returnType = CtClass.voidType;
		CtClass[] parameters = new CtClass[]{ pool.get("java.lang.Object"), pool.get("java.lang.Object") };
		
		CtMethod copyMethod = new CtMethod(returnType , "copy", parameters , cls);
		copyMethod.setModifiers(Modifier.PUBLIC);
		String copyBody = getCopyBody(source, target);
		copyMethod.setBody(copyBody);		
		cls.addMethod(copyMethod);
		
		cls.toClass();	
				
		Object newCopyer = Class.forName(className).newInstance();
		return (ICopy) newCopyer;
		
	}
	
	
	
	private String getCopyBody(Class<?> source, Class<?> target) {
		
		List<Field> sourceFields = getFieldHasMethod(source,"get");
		List<Field> targetFields = getFieldHasMethod(target,"set");
		
		List<String> copyNameList = new ArrayList<String>(); 
		List<String> sNameList = new ArrayList<String>(); 
		for (Field field : sourceFields) {
			sNameList.add(field.getName());
		}
		for (Field field : targetFields) {
			String name = field.getName();
			if(sNameList.contains(name)){
				copyNameList.add(name);
			}
		}		
		
		String sourceClassName = source.getName();
		String targetClassName = target.getName();
		StringBuilder body = new StringBuilder();
		body.append("{");
		final String teamplate = "(({targetClassName})$2).set{fieldName}((({sourceClassName})$1).get{fieldName}());";
		for (String nameTmp : copyNameList) {			
			String copyCode = teamplate.replace("{fieldName}", firstUp(nameTmp))
								.replace("{targetClassName}", targetClassName)
								.replace("{sourceClassName}", sourceClassName);
			body.append(copyCode);
		}
		body.append("}");
		return body.toString();
	}
	
	private String firstUp(String target){
		StringBuilder sb = new StringBuilder(target);
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return sb.toString();
	}
	
	public List<Field> getFieldHasMethod(Class<?> source, String startWith){
		
		Method[] methods = source.getMethods();
		
		List<String> fNameListFromMethod = new ArrayList<String>();
		for (Method method : methods) {
			String methodName = method.getName();
			if(methodName.startsWith(startWith)){
				String fieldName = getFieldName(methodName, startWith);
				fNameListFromMethod.add(fieldName);
			}
		}
		
		List<Field>  targetList = new ArrayList<Field>();
		Field[] sourceFields = source.getDeclaredFields();
		for (Field field : sourceFields) {
			String nameTmp = field.getName();
			if(fNameListFromMethod.contains(nameTmp.toLowerCase())){
				targetList.add(field);
			}			
		}
		return targetList;
	}



	private String getFieldName(String methodName, String startWith) {
		return StringUtils.substringAfter(methodName, startWith).toLowerCase();
	}



}