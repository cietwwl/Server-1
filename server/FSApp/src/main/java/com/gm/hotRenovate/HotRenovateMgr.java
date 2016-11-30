package com.gm.hotRenovate;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

public class HotRenovateMgr {
	
	private static HotRenovateMgr instance = new HotRenovateMgr();
	
	public static HotRenovateMgr getInstance(){
		return instance;
	}
	
	public void hotRenovate(){
		try {
			updateVMClass();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AttachNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	private void updateVMClass() throws IOException, AttachNotSupportedException {
		String localPath = System.getProperty("user.dir");
		String updateFilePath = localPath + "/update";
		String updateListPath = localPath + "/update/updatelist.txt";
		String libFilePath = localPath + "/lib/RainWings.HotFix-1.0.1.jar";
		File updateDir = new File(updateFilePath);
		File libFile = new File(libFilePath);
		if(updateDir.exists() && updateDir.isDirectory() && libFile.exists()){
			File updateList = new File(updateListPath);
			if(updateList.exists()){
				FileReader fr = new FileReader(updateList);
				int fileLen = (int)updateList.length();
				char[] chars = new char[fileLen];
				fr.read(chars);
				fr.close();
				String clazzNames = String.valueOf(chars);
				clazzNames = clazzNames.replaceAll("\n", "").replaceAll("\r", "").trim();
				
				String name = "";//ManagementFactory.getRuntimeMXBean().getName();
				VirtualMachine vm = VirtualMachine.attach(name.split("@")[0]);
				try{
					vm.loadAgent(libFilePath, localPath + "/update/@" + clazzNames);
					vm.detach();
				}catch(Throwable ex){
					System.out.println("vm loadAgent or detach fail....");
				}
				deleteAllFiles(updateDir);
			}
		}
	}
	
	/**
	 * 删除文件夹下所有文件
	 * @param file 需要是文件夹
	 */
	private void deleteAllFiles(File file){
		File[] subFiles = file.listFiles();
		if(null != subFiles){
			for(File subFile : subFiles){
				if(subFile.isDirectory()){
					deleteAllFiles(subFile);
				}
				subFile.delete();
			}
		}
	}
}
