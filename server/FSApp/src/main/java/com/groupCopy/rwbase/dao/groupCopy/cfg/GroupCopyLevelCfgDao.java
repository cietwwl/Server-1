package com.groupCopy.rwbase.dao.groupCopy.cfg;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.battle.pojo.BattleCfgDAO;
import com.rwbase.dao.battle.pojo.cfg.CopyMonsterInfoCfg;

public class GroupCopyLevelCfgDao extends CfgCsvDao<GroupCopyLevelCfg> {
	public static GroupCopyLevelCfgDao getInstance() {
		return SpringContextUtil.getBean(GroupCopyLevelCfgDao.class);
	}
	
	@Override
	public Map<String, GroupCopyLevelCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("GroupCopy/GroupCopyLevelCfg.csv", GroupCopyLevelCfg.class);
		formatData(cfgCacheMap.values());
		return cfgCacheMap;
	}
	
	private void formatData(Collection<GroupCopyLevelCfg> values) {
		for (GroupCopyLevelCfg cfg : values) {
			cfg.formatData();
		}
	}
	
	public GroupCopyLevelCfg getConfig(String id){
		GroupCopyLevelCfg cfg = (GroupCopyLevelCfg)getCfgById(id);
		return cfg;
	}

	@Override
	public void CheckConfig() {
		Collection<GroupCopyLevelCfg> values = cfgCacheMap.values();
		boolean error = false;
		StringBuilder sb = new StringBuilder("帮派副本，找不到对应的怪物的关卡：");
		for (GroupCopyLevelCfg cfg : values) {
			List<CopyMonsterInfoCfg> list = BattleCfgDAO.getInstance().getCopyMonsterInfoByCopyID(cfg.getId());
			if(list.isEmpty()){
				error = true;
				sb.append("[").append(cfg.getId()).append("]");
			}
		}
		
		if(error){
			throw new ExceptionInInitializerError(sb.toString());
		}
		
	}
	
	
}
