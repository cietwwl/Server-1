package com.common.refOpt;


public class AnotedClassMgr {

	private static AnotedClassMgr instance = new AnotedClassMgr();
	
	public static AnotedClassMgr getInstance(){
		return instance;
	}
	
	
	
	public void sumSynClass(){
		
//		Iterable<Class<?>> annotated = ClassIndex.getAnnotated(SynClass.class);
//		for (Class<?> klass : annotated) {
//		    System.out.println(klass.getName());
//		}
		
	}
	
	public static void main(String[] args) {
//		AnotedClassMgr.getInstance().sumSynClass();	
	}
	
}
