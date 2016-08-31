package com.rwbase.dao.openLevelLimit;

import java.util.Map;

import com.common.RefParam;
import com.playerdata.Player;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.shareCfg.ChineseStringHelper;
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

	public String getNotOpenTip(eOpenLevelType type, Player player){
		RefParam<String> outtip = new RefParam<String>();
		isOpen(type,player,outtip);
		return outtip.value;
	}
	
	/**
	 * 某个功能是否开放
	 * @param type 检测开放的功能类型
	 * @param player 当前角色
	 * @return 是否开放
	 */
	public boolean isOpen(eOpenLevelType type, Player player){
		return isOpen(type,player,null);
	}
	
	public boolean isOpen(eOpenLevelType type, Player player, RefParam<String> outTip){
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

		if (!result && outTip != null){
			if (cfg == null){
				outTip.value = "功能未开放";
			}else{
				ChineseStringHelper helper = ChineseStringHelper.getInstance();
				int checkPointID = cfg.getCheckPointID();
				if (checkPointID > 0){
					String tipTemplate = helper.getLanguageString("FunctionOpenAtLevelAtCopy", "主角%s级并且通关%s开启");
					outTip.value = String.format(tipTemplate, level,checkPointID);
				}else{
					String tipTemplate = helper.getLanguageString("FunctionOpenAtLevel", "主角%s级开启");
					outTip.value = String.format(tipTemplate, level);
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