package com.rwbase.dao.openLevelLimit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.common.RefInt;
import com.common.RefParam;
import com.log.GameLog;
import com.playerdata.Player;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.shareCfg.ChineseStringHelper;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.copy.cfg.CopyCfg;
import com.rwbase.dao.copy.cfg.CopyCfgDAO;
import com.rwbase.dao.openLevelLimit.pojo.CfgOpenLevelLimit;
import com.rwproto.MsgDef.Command;

public class CfgOpenLevelLimitDAO extends CfgCsvDao<CfgOpenLevelLimit> {

	public static CfgOpenLevelLimitDAO getInstance() {
		return SpringContextUtil.getBean(CfgOpenLevelLimitDAO.class);
	}

	private Map<Command,List<CfgOpenLevelLimit>> cmdMapping;
	public Map<String, CfgOpenLevelLimit> initJsonCfg() {
		Map<String, CfgOpenLevelLimit> tmpMap = CfgCsvHelper.readCsv2Map("openLevelLimit/openLevelLimit.csv", CfgOpenLevelLimit.class);
		Set<Entry<String, CfgOpenLevelLimit>> entryLst = tmpMap.entrySet();
		Map<Command,List<CfgOpenLevelLimit>> mapping = new HashMap<Command, List<CfgOpenLevelLimit>>();
		for (Entry<String, CfgOpenLevelLimit> entry : entryLst) {
			CfgOpenLevelLimit cfg = entry.getValue();
			cfg.ExraLoad();
			Command serviceId = cfg.getServiceId();
			if (serviceId != null){
				List<CfgOpenLevelLimit> old = mapping.get(serviceId);
				if (old == null){
					old = new ArrayList<CfgOpenLevelLimit>();
					mapping.put(serviceId, old);
				}
				old.add(cfg);
			}
		}
		
		cmdMapping = mapping;
		cfgCacheMap = tmpMap;
		return cfgCacheMap;
	}
	
	public List<CfgOpenLevelLimit> getOpenCfg(Command cmd){
		return cmdMapping.get(cmd);
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
				level = cfg.getMinLevel();
				if (checkPointID > 0){
					CopyCfg copyCfg = CopyCfgDAO.getInstance().getCfg(checkPointID);
					if (copyCfg != null){
						RefInt chapter = new RefInt();
						RefInt order = new RefInt();
						GetCopyChapterAndOrder(checkPointID,chapter,order);
						String tipTemplate = helper.getLanguageString("FunctionOpenAtLevelAtCopy", "主角%s级并且通关“%s-%s %s”开启");
						outTip.value = String.format(tipTemplate, level,chapter.value,order.value,copyCfg.getName());
					}else{
						GameLog.error("功能开发", "", "配置错误：openLevelLimit配置了不存在的关卡ID:"+checkPointID);
						String tipTemplate = helper.getLanguageString("FunctionOpenAtLevel", "主角%s级开启");
						outTip.value = String.format(tipTemplate, level);
					}
				}else{
					String tipTemplate = helper.getLanguageString("FunctionOpenAtLevel", "主角%s级开启");
					outTip.value = String.format(tipTemplate, level);
				}
			}
		}

		return result;
	}

	private 	void GetCopyChapterAndOrder(int copyId,RefInt chapterNum,RefInt orderInChapter){
		orderInChapter.value = copyId % 100;
		chapterNum.value = (copyId / 100) % 100;
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