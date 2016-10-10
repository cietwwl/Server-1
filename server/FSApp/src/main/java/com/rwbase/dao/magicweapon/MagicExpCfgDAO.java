package com.rwbase.dao.magicweapon;

import java.util.HashMap;
import java.util.Map;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.magicweapon.pojo.MagicExpCfg;

public class MagicExpCfgDAO extends CfgCsvDao<MagicExpCfg> {

	private HashMap<Integer, MagicExpCfg> magicCfgMap;
	private int maxMagicLevel;
	

	public int getMaxMagicLevel() {
		return maxMagicLevel;
	}

	public static MagicExpCfgDAO getInstance() {
		return SpringContextUtil.getBean(MagicExpCfgDAO.class);
	}
	
	public MagicExpCfg getMagicCfgByLevel(int level){
		return magicCfgMap.get(level);
	}

	@Override
	public Map<String, MagicExpCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("MagicWeapon/MagicExp.csv", MagicExpCfg.class);
		int count = cfgCacheMap.size();

		magicCfgMap = new HashMap<Integer, MagicExpCfg>(cfgCacheMap.size());
		int maxLevel = -1;
		for (int i = 1; i <= count; i++) {

			MagicExpCfg cfg = cfgCacheMap.get(String.valueOf(i));

			if (cfg == null) {

				GameLog.error("法宝", "配置错误", "MagicExp表缺少了等级：" + i);

				continue;

			}

			int level = cfg.getLevel();
			if (level > maxLevel) {
				maxLevel = level;
			}
			if (magicCfgMap.put(level, cfg) != null) {
				GameLog.error("法宝", "配置错误", "MagicExp表出现重复的记录");
				continue;
			}

		}
		maxMagicLevel = maxLevel;
		return cfgCacheMap;
	}

}
