package com.rw.fsutil.util;

import java.io.File;
import java.net.URL;


public class UrlTool {

	private static UrlTool urlTool ;
	
	private URL rootUrl ;
	private String rootPath ;
	private String tmpFilePath ;
	
	public UrlTool(){
		this.rootUrl = ClassLoader.getSystemResource("");
		
		this.rootPath = rootUrl.getFile();//.replace("%2010", "");
		this.tmpFilePath = rootPath + "/tmp" ;
	}
//	
//	public URL getRootUrl() {
//		return rootUrl;
//	}
//
//	public String getRootPath() {
//		return rootPath;
//	}
	
	
	public String getClassPath() {
		return rootPath;
	}

	public String getTmpFilePath() {
		return tmpFilePath;
	}
	
//	public String getFilePath(String path,boolean isAutoCreate){
//		String curPath = rootPath + "/" + path ;
//		File file = new File(curPath);
//		if(!file.exists()){
//			file.mkdirs() ;
//		}
//		return curPath ;
//	}
	
	
//	public String getUserFilePath(String path){
//		String userDir = System.getProperty("user.dir");
//		String curPath = userDir + "/" + path ;
//		File file = new File(curPath);
//		if(!file.exists()){
//			//file.mkdirs() ;
//			return null;
//		}
//		return curPath ;
//	}
	
	
	public String getResFilePath(String path){
		String userDir = System.getProperty("user.dir");
		String curPath = userDir + "/resource/" + path ;
		File file = new File(curPath);
		if(!file.exists()){
			file.mkdirs() ;
			return null;
		}
		return curPath ;
	}


	public static UrlTool getInstance(){
		if(urlTool == null){
			urlTool = new UrlTool() ;
		}
		return urlTool ;
	}
}
