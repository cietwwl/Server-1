package com.common.refOpt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.log.GameLog;
import com.log.LogModule;



public class RefOptTmpLoader extends ClassLoader {
	
	private List<String> classList = new ArrayList<String>();
	
	private String baseDir;
	
	public RefOptTmpLoader(List<String> classListP){
		super(null);	
		this.classList = classListP;
		baseDir = this.getClass().getClassLoader().getResource("").getPath();
	}
	
	public void init(List<String> classList){
//		
//		this.classList = classList;
//		for (String className : classList) {
//			try {
//				derectLoad(className);
//			} catch (Exception e) {
//				GameLog.error(LogModule.RefOpt, "RefOptClassGener[genRefOptClass]", "class 文件加载失败在 className:"+className, e);
//			}
//		}
	}

	private Class<?> directLoad(String className) throws IOException {
		Class<?> cls = null;
		String filePath = baseDir + className.replace(".", "/")+".class";
		File file = new File(filePath);
		if(file.exists()){
			InputStream input = new FileInputStream(file);
			byte[] classBytes = new byte[input.available()];
			input.read(classBytes);
			input.close();
			cls = redefineClass(className, classBytes);
		}else{
			
			GameLog.error(LogModule.RefOpt, "RefOptClassGener[genRefOptClass]", "class 文件不存在 className:"+className, null);
		}
		return cls;
	}
	
	public Class<?> redefineClass(String className, byte[] code){
		return defineClass(className, code, 0, code.length);
	}
	
	@Override
	protected Class<?> loadClass(String name,boolean resolve) throws ClassNotFoundException {
		Class<?> cls = findLoadedClass(name);
		
		if(cls==null && !name.equals("com.common.refOpt.RefOptClassData")){
			try {
				cls = directLoad(name);
			} catch (IOException e) {
				GameLog.error(LogModule.RefOpt, "RefOptClassGener[loadClass]", "class 文件不存在 className:"+name, e);
			}
		}
		
		if(cls==null){
			if(name.contains("ApplyInfo")){
				System.out.println("tt");
			}
			cls = 	getSystemClassLoader().loadClass(name);
		}
		if(cls == null){
			throw new ClassNotFoundException(name);
		}
		if(resolve){
			resolveClass(cls);
		}
		return cls;
	}
	
	


	
	
}
