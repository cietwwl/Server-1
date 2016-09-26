package com.common.refOpt;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

public class RefOptClassGener {
	
	private static RefOptClassGener instance = new RefOptClassGener();
	
	public static RefOptClassGener getInstance(){
		return instance;
	}
	
	
	public IRefOpt genRefOptClass(String className, RefOptTmpLoader refOptTmpLoader) throws Exception{
		
		String filePath = refOptTmpLoader.getResource(className.replace(".", "/")+".class").getFile();	
	    InputStream input = new FileInputStream(new File(filePath));
	    byte[] classBytes = new byte[input.available()];
	    input.read(classBytes);
	    input.close();
	    Class<?> source = refOptTmpLoader.redefineClass(className, classBytes);		
		
		
		ClassPool pool = ClassPool.getDefault();
		FileInputStream fileInputStream = new FileInputStream(new File(filePath));
		CtClass cls = pool.makeClass(fileInputStream);
		cls.addInterface(pool.get("classLoader.refOpt.IRefOpt"));
		
		//����ֶ����ֵ����ֵ�ӳ����ڻ�ȡ�ֶ�ֵ��ʱ���switch������֡�
		CtField enoField = new CtField(pool.getCtClass("java.util.Map"),"ref$FNmap", cls);  
	    enoField.setModifiers(Modifier.PRIVATE);  
	    cls.addField(enoField); 
		
		//��� ref$Init ����
		addRef$InitMethod(source, cls);
		
		
		//��� refGet ����
		addRefGetMethod(source, pool, cls);		
		 
		cls.toClass();	
				
		Object newCopyer = Class.forName(className).newInstance();
		Field nameField = newCopyer.getClass().getDeclaredField("name");
		nameField.setAccessible(true);
		nameField.set(newCopyer, "testName");
		fileInputStream.close();
		return (IRefOpt) newCopyer;
		
	}


	private void addRefGetMethod(Class<?> source, ClassPool pool, CtClass cls) throws NotFoundException, CannotCompileException {
		CtClass getReturnType = pool.get("java.lang.Object");
		CtClass[] getParameters = new CtClass[]{pool.get("java.lang.String")};		
		CtMethod getMethod = new CtMethod(getReturnType , "ref$Get", getParameters , cls);
		getMethod.setModifiers(Modifier.PUBLIC);
		String getBody = getrefGetBody(source);
		getMethod.setBody(getBody);		
		cls.addMethod(getMethod);
	}
	
	
	private String getRefInitBody(Class<?> source) {
		StringBuilder initMethod = new StringBuilder("{ref$FNmap = new java.util.HashMap();");
		
		int fnIndex = 0;
		Field[] sourceFields = source.getDeclaredFields();
		for (Field field : sourceFields) {
			String nameTmp = field.getName();
			initMethod.append("ref$FNmap.put(\""+nameTmp+"\", new Integer("+fnIndex+"));");
		}
		initMethod.append("}");
		
		return initMethod.toString();
	}


	private void addRef$InitMethod(Class<?> source, CtClass cls) throws CannotCompileException {
		CtClass returnType = CtClass.voidType;
		CtClass[] parameters = new CtClass[]{};		
		CtMethod initMethod = new CtMethod(returnType , "ref$Init", parameters , cls);
		initMethod.setModifiers(Modifier.PUBLIC);
		String initBody = getRefInitBody(source);
		initMethod.setBody(initBody);		
		cls.addMethod(initMethod);
	}
	private String getrefGetBody(Class<?> source) {
		StringBuilder getMethod = new StringBuilder("{java.lang.Integer indexInt = (java.lang.Integer) ref$FNmap.get($1);int index = indexInt.intValue();switch (index) {");
		
		int fnIndex = 0;
		Field[] sourceFields = source.getDeclaredFields();
		for (Field field : sourceFields) {
			String nameTmp = field.getName();
			if(!"ref$FNmap".equals(nameTmp)){
				getMethod.append("case "+fnIndex+":return classLoader.refOpt.Raw2ObjHelper.from(this."+nameTmp+");");
			}
			fnIndex++;
		}
		getMethod.append("}return null;}");
		
		return getMethod.toString();
	}


	public static void main(String[] args) {
		String filePath = RefOptClassGener.class.getClassLoader().getResource("classLoader/refOpt/example/BeanA.class").getFile();	
		System.out.println(filePath);
	}
}