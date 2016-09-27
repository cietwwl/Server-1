package com.common.refOpt;



public class RefOptTmpLoader extends ClassLoader {
	
	
	public Class<?> redefineClass(String className, byte[] code){
		
		return defineClass(className, code, 0, code.length);
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		
		return super.loadClass(name);
	}
	
	


	
	
}
