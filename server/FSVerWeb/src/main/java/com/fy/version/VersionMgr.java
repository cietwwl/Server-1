package com.fy.version;

import org.apache.log4j.Logger;

public class VersionMgr {


	public final static Logger logger = Logger.getLogger("gsLogger");
	
	private VersionDao versionDao;
	
	
	public Version getUpdateVersion(Version clientVersion){
		
		//获取下一个完整包,有则直接升级到这个完整包
		Version targetVersion = versionDao.getNextCompVer(clientVersion);
		//没有说明已经是最高的完整包，继续获取看有没有patch
		if(targetVersion == null){
			targetVersion = versionDao.getNextPatch(clientVersion);
		}
		
		//没有全量更新和资源更新，则检查是否有代码更新
		if(targetVersion == null){
			targetVersion = versionDao.getNextCodePatch(clientVersion);
		}
		return targetVersion;
	}
	
	public Version getMaxVersion(Version clientVersion){
		Version maxCompVersion = versionDao.getMaxVersion(clientVersion);
		return  maxCompVersion;
	}


	public void setVersionDao(VersionDao versionDao) {
		this.versionDao = versionDao;
	}
	

	
	
	
	
}
