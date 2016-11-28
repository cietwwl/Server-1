package com.fy.version;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class VersionMgr {


	public final static Logger logger = Logger.getLogger("gsLogger");
	
	private VersionDao versionDao;
	
	
	public Version getUpdateVersion(Version clientVersion){
		
		//获取下一个完整包,有则直接升级到这个完整包
		Version targetVersion = versionDao.getNextCompVer(clientVersion);
		
		//没有说明已经是最高的完整包,继续获取优先级高的补丁包
		if(targetVersion == null){
			targetVersion = versionDao.getPriorityPatch(clientVersion);
		}
		
		//没有说明已经是最高的完整包，继续获取看有没有patch
		if(targetVersion == null){
			targetVersion = versionDao.getNextPatch(clientVersion);
		}
		
		//没有全量更新和资源更新，则检查是否有代码更新
		if(targetVersion == null){
			targetVersion = versionDao.getNextCodePatch(clientVersion);
		}
		if(targetVersion != null){
			targetVersion.setPackageName(clientVersion.getPackageName());
		}
		return targetVersion;
	}
	
	public List<Version> getUpdateVersion2(Version clientVersion){
		List<Version> result = new ArrayList<Version>();
		//获取下一个完整包,有则直接升级到这个完整包
		Version targetVersion = versionDao.getNextCompVer(clientVersion);
		if(null != targetVersion){
			result.add(targetVersion);
		}
		//没有说明已经是最高的完整包，继续获取看有没有patch
		if(result.isEmpty()){
			result.addAll(versionDao.getLeftPatch(clientVersion));
		}
		//没有全量更新和资源更新，则检查是否有代码更新
		if(result.isEmpty()){
			targetVersion = versionDao.getNextCodePatch(clientVersion);
			if(null != targetVersion){
				result.add(targetVersion);
			}
		}
		for(Version tVer : result){
			tVer.setPackageName(clientVersion.getPackageName());
		}
		return result;
	}
	
	public Version getMaxVersion(Version clientVersion){
		Version maxCompVersion = versionDao.getMaxVersion(clientVersion);
		if(maxCompVersion != null){
			maxCompVersion.setPackageName(clientVersion.getPackageName());
		}
		return  maxCompVersion;
	}


	public void setVersionDao(VersionDao versionDao) {
		this.versionDao = versionDao;
	}
	

	
	
	
	
}
