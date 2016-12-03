package com.fy.version;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class VersionMgr {

	public final static Logger logger = Logger.getLogger("gsLogger");
	
	private VersionDao versionDao;
	
	public List<Version> getUpdateVersion(Version clientVersion){
		List<Version> result = new ArrayList<Version>();
		//获取下一个完整包,有则直接升级到这个完整包
		Version targetVersion = versionDao.getNextCompVer(clientVersion);
		if(null != targetVersion){
			result.add(targetVersion);
		}else{
			//没有完整包，说明已经是最高的完整包，继续获取看有没有patch
			result.addAll(versionDao.getLeftPatch(clientVersion));
			//检查是否有代码更新
			targetVersion = versionDao.getNextCodePatch(clientVersion);
			if(null != targetVersion){
				if(targetVersion.getPriority() == 1) {
					result.clear();
				}
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
