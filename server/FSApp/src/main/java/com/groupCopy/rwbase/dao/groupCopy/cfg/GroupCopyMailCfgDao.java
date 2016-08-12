package com.groupCopy.rwbase.dao.groupCopy.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class GroupCopyMailCfgDao extends CfgCsvDao<GroupCopyMailCfg>{

	public static GroupCopyMailCfgDao getInstance(){
		return SpringContextUtil.getBean(GroupCopyMailCfgDao.class);
	}
	
	@Override
	protected Map<String, GroupCopyMailCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("GroupCopy/GroupCopyRewardMailCfg.csv", GroupCopyMailCfg.class);
		return cfgCacheMap;
	}
	
	public GroupCopyMailCfg getConfig(String key){
		return getCfgById(key);
	}

	//正常来说应该是只有一列数据
	public GroupCopyMailCfg getConfig(){
		return getAllCfg().get(0);
	}
}
