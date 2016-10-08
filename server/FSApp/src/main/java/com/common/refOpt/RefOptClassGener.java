package com.common.refOpt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;


/**
 * 
 * @author allen
 *
 */
public class RefOptClassGener {
	
	private static RefOptClassGener instance = new RefOptClassGener();
	
	public static RefOptClassGener getInstance(){
		
		return instance;
	}
	
	private List<String> loadedClassList = new ArrayList<String>();
	
	private boolean open = false;
	
	public RefOptClassGener setOpen(boolean openP){
		this.open = openP;
		return this;
	}
	
	public boolean containsClass(Class<?> clazz){
		if(!open){
			return false;
		}
		String className = clazz.getName();
		return loadedClassList.contains(className);
	}
	
	public void preLoadClassList() throws Exception{
		if(!open){
			return;
		}
		List<String> classNameList = getClassNameList();
		RefOptTmpLoader tmpLoader = new RefOptTmpLoader(classNameList);

		
		Class<?> dataGenClass = tmpLoader.loadClass("com.common.refOpt.RefOptClassDataGen");
		Method genMethod = dataGenClass.getDeclaredMethod("gen", List.class);
		
		@SuppressWarnings("unchecked")
		Map<String,RefOptClassData> classDataMap = (Map<String, RefOptClassData>) genMethod.invoke(dataGenClass.newInstance(), classNameList);
		
		for (String className : classNameList) {
			try {
				genRefOptClass(className, classDataMap.get(className));
				loadedClassList.add(className);
			} catch (Exception e) {
				GameLog.error(LogModule.RefOpt, "RefOptClassGener[preLoadClassList]", "class 文件加载出错  className:"+className, e);
			}
		}
		
	}

	private List<String> getClassNameList() throws FileNotFoundException, IOException {
		String filePath = this.getClass().getResource("./classNames/SynClassList").getFile();
		FileReader csFileReader = new FileReader(filePath);

		BufferedReader bf = new BufferedReader(csFileReader);
		String readLine = bf.readLine();
		List<String> classNameList = new ArrayList<String>();
		while(readLine!=null){
			if(StringUtils.isNotBlank(readLine)){
				classNameList.add(readLine);
			}
			readLine = bf.readLine();	
		}
		bf.close();
		return classNameList;
	}
	
	
	private void genRefOptClass(String className, RefOptClassData classData) throws Exception{
	    
	    if(classData==null){
	    	return;
	    }
		
		
	    URL classResource = this.getClass().getClassLoader().getResource(className.replace(".", "/")+".class");
	    String filePath = classResource.getFile();	
	    
		ClassPool pool = ClassPool.getDefault();
		FileInputStream fileInputStream = new FileInputStream(new File(filePath));
		CtClass cls = pool.makeClass(fileInputStream);
		cls.addInterface(pool.get("com.common.refOpt.IRefOpt"));
		
		
		//根据域名取域值的方法
		addRefGetMethod(pool, cls, classData);			 
		cls.toClass();					
		fileInputStream.close();
		
		
	}


	private void addRefGetMethod(ClassPool pool, CtClass cls, RefOptClassData classData) throws NotFoundException, CannotCompileException {
		CtClass getReturnType = pool.get("java.lang.Object");
		CtClass[] getParameters = new CtClass[]{pool.get("java.lang.String")};		
		CtMethod getMethod = new CtMethod(getReturnType , "ref$Get", getParameters , cls);
		getMethod.setModifiers(Modifier.PUBLIC);
		String getBody = classData.getGetBody();
		getMethod.setBody(getBody);		
		cls.addMethod(getMethod);
	}


	public static void main(String[] args) {
		String filePath = RefOptClassGener.class.getResource("./classNames/SynClassList").getFile();	
		System.out.println(filePath);
	}
}