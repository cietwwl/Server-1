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

import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.spi.VersionValue;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fy.SpringContextUtil;
import com.fy.address.AddressInfo;
import com.fy.lua.LuaDao;
import com.fy.utils.FileUtils;
import com.sun.tools.javac.resources.version;

public class VersionDao {



	private Map<String, VersionChannel> channelVersionMap = new HashMap<String, VersionChannel>();
	
	private Map<String, VersionFileInfo> versionFileMap = new HashMap<String, VersionFileInfo>();
	
	private String verDirPath = "";
	
	
	
	public static VersionDao getInstance(){
		return SpringContextUtil.getBean("versionDao");
	}
	
	public void init(){
		load();
	}
	
	public void load(){
		File verDir = new File(verDirPath);
		List<File> fileList = new ArrayList<File>();
		FileUtils.sumFiles(verDir, fileList, ".txt");
		
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

	private boolean isModified(List<File> fileList) {
		boolean reault = false;
		if(fileList.size() < versionFileMap.size()){
			return true;
		}
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
	
	//剩余的补丁包
	public List<Version> getLeftPatch(Version clientVersion){
		return channelVersionMap.get(clientVersion.getChannel()).getLeftPatch(clientVersion);
	}
	
	//下一个代码补丁包
	public Version getNextCodePatch(Version clientVersion){
		return channelVersionMap.get(clientVersion.getChannel()).getNextCodePatch(clientVersion);
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
	

	//name=chanel_v.*.*.*_patch(0 完整包, >1 patch)
	private Version fromFile(File file) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String loginServerDomain = AddressInfo.getInstance().getLoginServerDomain();
		String cdnDomain = AddressInfo.getInstance().getCdnDomain();
		String cdnBackUpDomain = AddressInfo.getInstance().getCdnBackUpDomain();
		String logServerAddress = AddressInfo.getInstance().getLogServerAddress();
		Version version = null;
		try {
			version = Version.fromFile(file);
			if (StringUtils.isBlank(version.getLoginServerDomain()) || StringUtils.isBlank(version.getCdnDomain()) || StringUtils.isBlank(version.getCdnBackUpDomain()) || StringUtils.isBlank(version.getLogServerAddress())) {
				version.setLoginServerDomain(loginServerDomain);
				version.setCdnDomain(cdnDomain);
				version.setCdnBackUpDomain(cdnBackUpDomain);
				version.setLogServerAddress(logServerAddress);
			}
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
