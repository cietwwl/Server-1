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

import com.fy.SpringContextUtil;
import com.fy.address.ChannelAddressInfo;
import com.fy.address.ChannelAddressInfoDao;
import com.fy.utils.FileUtils;

public class VersionDao {

	private Map<String, VersionChannel> channelVersionMap = new HashMap<String, VersionChannel>();
	
	private Map<String, VersionFileInfo> versionFileMap = new HashMap<String, VersionFileInfo>();
	
	private Map<String, ChannelAddressInfo> addressInfoMap = new HashMap<String, ChannelAddressInfo>();
	
	private Map<String, Map<String, ChannelAddressInfo>> packageNameAddressInfoMap = new HashMap<String, Map<String, ChannelAddressInfo>>();
	
	public static String DEFAULT_PACKAGENAME = "DEAFULT";
	
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
		
		// 加载地址信息
		List<File> pathFileList = new ArrayList<File>();
		FileUtils.sumFiles(verDir, pathFileList, ".path");
		try {
			if (isModified(pathFileList)) {

				for (File file : pathFileList) {

					Map<String, ChannelAddressInfo> map = ChannelAddressInfoDao.fromFile(file);
					ChannelAddressInfo channelAddressInfo = map.get(DEFAULT_PACKAGENAME);
					packageNameAddressInfoMap.put(channelAddressInfo.getChannel(), map);
				}
			}
		} catch (Exception ex) {

		}
		
		
		
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
		Version version = null;
		try {
			version = Version.fromFile(file);

//			String channel = version.getChannel();
//			String packageName = version.getPackageName();
//			ChannelAddressInfo channelAddressInfo = addressInfoMap.get(channel);
//			
//			Map<String, ChannelAddressInfo> packageNameMap = packageNameAddressInfoMap.get(channel);
//			if(packageNameMap == null){
//				packageNameMap = new HashMap<String, ChannelAddressInfo>();
//				packageNameAddressInfoMap.put(packageName, packageNameMap);
//			}
//			
//			Map<String, ChannelAddressInfo> map = packageNameAddressInfoMap.get(packageName);
//			
//			int main = version.getMain();
//			ChannelAddressInfo channelAddressInfo = null;
//			if(map.containsKey(main)){
//				channelAddressInfo = map.get(main);
//			}else{
//				channelAddressInfo = map.get(DefaultVersion);
//			}
//			
//			if (channelAddressInfo != null) {
//				version.setLoginServerDomain(channelAddressInfo.getLoginServerDomain());
//				version.setCdnDomain(channelAddressInfo.getCdnDomain());
//				version.setCdnBackUpDomain(channelAddressInfo.getCdnBackUpDomain());
//				version.setLogServerAddress(channelAddressInfo.getLogServerAddress());
//				version.setCheckServerURL(channelAddressInfo.getCheckServerURL());
//				version.setCheckServerPayURL(channelAddressInfo.getCheckServerPayURL());
//				version.setBackUrl(channelAddressInfo.getBackUrl());
//			}else{
//				throw (new RuntimeException("版本配置有错，请检查."));
//			}
		} catch (Exception e) {
			e.printStackTrace();
			throw (new RuntimeException("版本配置有错，请检查."));
		} finally {
			reader.close();
		}
		return version;
		
	}
	
	public void setVerDirPath(String verDirPath) {
		this.verDirPath = verDirPath;
	}
	
	public ChannelAddressInfo getChannelAddressInfo(String channel){
		Map<String, ChannelAddressInfo> map = packageNameAddressInfoMap.get(channel);
		return map.get(DEFAULT_PACKAGENAME);
	}
	
	public ChannelAddressInfo getChannelAddressInfoByPackageName(String channel, String packageName){
		Map<String, ChannelAddressInfo> map = packageNameAddressInfoMap.get(channel);
		ChannelAddressInfo channelAddressInfo;
		if(map.containsKey(packageName)){
			channelAddressInfo = map.get(packageName);
		}else{
			channelAddressInfo = map.get(DEFAULT_PACKAGENAME);
		}
		return channelAddressInfo;
	}

	public static void main(String[] args) {
		VersionDao vdao = new VersionDao();
		vdao.init();
	}
}
