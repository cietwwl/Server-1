package com.rwbase.dao.openLevelLimit;

import java.util.Map;

import com.playerdata.Player;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.openLevelLimit.pojo.CfgOpenLevelLimit;

public class CfgOpenLevelLimitDAO extends CfgCsvDao<CfgOpenLevelLimit> {

	public static CfgOpenLevelLimitDAO getInstance() {
		return SpringContextUtil.getBean(CfgOpenLevelLimitDAO.class);
	}

	public Map<String, CfgOpenLevelLimit> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("openLevelLimit/openLevelLimit.csv", CfgOpenLevelLimit.class);
		return cfgCacheMap;
	}

	/**
	 * 某个功能是否开放
	 * @param type 检测开放的功能类型
	 * @param player 当前角色
	 * @return 是否开放
	 */
	public boolean isOpen(eOpenLevelType type, Player player){
		boolean result = false;
		int level = player.getLevel();
		CfgOpenLevelLimit cfg = getCfgById(type.getOrderString());
		if (cfg != null) {
			if (level >= cfg.getMinLevel() && level <= cfg.getMaxLevel()) {
				int checkPointID = cfg.getCheckPointID();
				if (checkPointID > 0){
					result = player.getCopyRecordMgr().isCopyLevelPassed(checkPointID);
				}else{
					result = true;
				}
			}
		}
		return result;
	}

	/**
	 * 判断是否开放等级
	 * 
	 * @param type 检测开放的功能类型
	 * @param player 当前角色
	 * @return 返回开启需要的等级
	 */
	public int checkIsOpen(eOpenLevelType type, Player player) {
		int level = player.getLevel();
		CfgOpenLevelLimit cfg = getCfgById(type.getOrderString());
		if (cfg == null) {
			return -1;
		}

		int minLevel = cfg.getMinLevel();
		if (level >= minLevel) {
			return -1;
		}
		
		int checkPointID = cfg.getCheckPointID();
		if (checkPointID > 0 && !player.getCopyRecordMgr().isCopyLevelPassed(checkPointID)){
			 return -1;
		}

		return minLevel;
	}
}