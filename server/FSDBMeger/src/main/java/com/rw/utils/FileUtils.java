package com.rw.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileUtils {
	public static boolean saveFile(String path, String content) {
		try {
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			FileWriter fw = new FileWriter(file.getAbsolutePath(), false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}	
	}
	
	public static void checkAndCreateFolder(String path){
		File file = new File(path);
		if (file.exists()) {
			clearFolder(path);
		} else {
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) {
				String absolutePath = parentFile.getAbsolutePath();
				checkAndCreateFolder(absolutePath);
			}else{
				file.mkdir();
			}
		}
	}
	
	public static void clearFolder(String path){
		File file = new File(path);
		if(!file.exists()){
			return;
		}
		File[] listFiles = file.listFiles();
		for (File file2 : listFiles) {
			file2.deleteOnExit();
		}
	}
}
