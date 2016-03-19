package com.fy.version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.hibernate.engine.spi.VersionValue;

import com.sun.tools.javac.resources.version;

public class VersionDao {



	private Map<String, VersionChannel> channelVersionMap = new HashMap<String, VersionChannel>();
	
	private Map<String, VersionFileInfo> versionFileMap = new HashMap<String, VersionFileInfo>();
	
	private String verDirPath = "";
	
	private List<String> lastFilePathList = new ArrayList<String>();
	
	private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
	
	
	public void init(){
		
		load();
		
		service.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				try {
					load();
				} catch (Throwable e) {
					e.printStackTrace();
				}
				
			}
		}, 0, 10, TimeUnit.SECONDS);
	}
	
	public void load(){
		File verDir = new File(verDirPath);
		List<File> fileList = new ArrayList<File>();
		sumFiles(verDir, fileList);
		
		if(isModified(fileList)){
			
			Map<String, VersionChannel> channelVersionMapTmp = new HashMap<String, VersionChannel>();
			
			Map<String, List<Version>> channelVerListMap = new HashMap<String, List<Version>>();
			List<String> channelList = new ArrayList<String>();
			List<Version> allVerList = getAllVer(fileList);
			for (Version version : allVerList) {
				String channelTmp = version.getChannel();
				if(!channelList.contains(channelTmp)){
					channelList.add(channelTmp);
					channelVerListMap.put(channelTmp, new ArrayList<Version>());
				}
				channelVerListMap.get(channelTmp).add(version);
			}
			for (String channel : channelList) {
				channelVersionMapTmp.put(channel, new VersionChannel(channelVerListMap.get(channel)));
				System.err.println("channel:"+channel);
				System.out.println("channelVerListMap list size:" + channelVerListMap.get(channel).size());
			}
			
			channelVersionMap = channelVersionMapTmp;
			storeFilePath(fileList);
		}
		for (File file : fileList) {
			if (!versionFileMap.containsKey(file.getAbsolutePath())) {
				VersionFileInfo versionFileInfo = new VersionFileInfo();
				versionFileInfo.setFileModifyTime(file.lastModified());
				versionFileInfo.setVersionFile(file);
				versionFileMap.put(file.getAbsolutePath(), versionFileInfo);
			}
		}
	}
	

	private void storeFilePath(List<File> fileList) {
		List<String> filePathListTmp = getFilePathList(fileList);
		lastFilePathList = filePathListTmp;
	}

	private List<String> getFilePathList(List<File> fileList) {
		List<String> filePathListTmp = new ArrayList<String>();
		for (File file : fileList) {
			filePathListTmp.add(file.getAbsolutePath());
		}
		return filePathListTmp;
	}

	private boolean isModified(List<File> fileList) {
		//List<String> current = getFilePathList(fileList);
		//boolean result = !(lastFilePathList.containsAll(current) && current.containsAll(lastFilePathList));
		boolean reault = false;
		for (File file : fileList) {
			VersionFileInfo versionFileInfo = versionFileMap.get(file.getAbsolutePath());
			if(versionFileInfo != null){
				if(versionFileInfo.getFileModifyTime() != file.lastModified()){
					reault = true;
					versionFileInfo.setFileModifyTime(file.lastModified());
				} 
			}else{
				reault = true;
			}
		}
		return reault;
	}

	//下一个升级全量包
	public Version getNextCompVer(Version clientVersion){
		System.out.println("channelVersionMap.size()"+channelVersionMap.size());
		System.out.println("clientVersion.getChannel()"+clientVersion.getChannel());
		for (Iterator<Entry<String, VersionChannel>> iterator = channelVersionMap
				.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, VersionChannel> next = iterator.next();
			System.out.println("key:" + next.getKey());

		}
		return channelVersionMap.get(clientVersion.getChannel()).getNextCompVer(clientVersion);
	}
	
	public Version getMaxVersion(Version clientVersion){
		return  channelVersionMap.get(clientVersion.getChannel()).getMaxVersion();
	}
	
	//下一个补丁包
	public Version getNextPatch(Version clientVersion){
		return channelVersionMap.get(clientVersion.getChannel()).getNextPatch(clientVersion);
	}
	

	private List<Version> getAllVer(List<File> fileList) {
		List<Version> allVerList = new ArrayList<Version>();
		for (File file : fileList) {
			try {
				Version fromFile = fromFile(file);
				if(fromFile!=null){
					allVerList.add(fromFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return allVerList;
	}
	
	private void sumFiles(File file, List<File> fileList){
		
		if(file.isFile()){
			fileList.add(file);
		}else if(file.isDirectory()){
			File[] fileArray = file.listFiles();
			for (File fileTmp : fileArray) {
				//筛选指定格式的版本文件（指定格式为txt）
				if (fileTmp.getName().indexOf(".txt") == -1 && fileTmp.isFile()) {
					continue;
				}
				sumFiles(fileTmp, fileList);

			}
		}
	}
	

	//name=chanel_v.*.*.*_patch(0 完整包, >1 patch)
	private Version fromFile(File file) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		Version version = null;
		try {
			version = Version.fromFile(file);
		}catch (Exception e){
			e.printStackTrace();
			throw(new RuntimeException("版本配置有错，请检查."));
		}finally{
			reader.close();
		}
		return version;
		
	}
	
	

	
	
	public void setVerDirPath(String verDirPath) {
		this.verDirPath = verDirPath;
	}

	public static void main(String[] args) {
		VersionDao vdao = new VersionDao();
		vdao.init();
	}
}
