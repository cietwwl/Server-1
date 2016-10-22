package com.rwbase.dao.magicweapon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.common.RefInt;
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
	
	public List<MagicExpCfg> getInheritList(int level, int toLevel){
		List<MagicExpCfg> result = new ArrayList<MagicExpCfg>();
		for (int i = 0; i < toLevel; i++) {
			MagicExpCfg magicExpCfg = magicCfgMap.get(i);
			if(magicExpCfg != null){
				result.add(magicExpCfg);
			}
		}
		return result;
	}
	
	public MagicExpCfg getInheritCfg(int level, int inheritExp){
		MagicExpCfg magicExpCfg = magicCfgMap.get(level);
		RefInt consumeExp = new RefInt();
		RefInt resourceExp = new RefInt(inheritExp);
		consumeExp.value += magicExpCfg.getExp();
		while (checkEnoughInheritExp(consumeExp, resourceExp)) {
			level++;
			if (magicCfgMap.containsKey(level)) {
				magicExpCfg = magicCfgMap.get(level);
			} else {
				break;
			}
			int exp = magicExpCfg.getExp();
			consumeExp.value+=exp;
			
		}
		return magicExpCfg;
	}
	
	private boolean checkEnoughInheritExp(RefInt consumeExp, RefInt sourceExp){
		return consumeExp.value <= sourceExp.value;
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
			String goodsId = cfg.getGoodsId();
			String[] value = goodsId.split(";");
			HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
			for (String str : value) {
				String[] split = str.split("_");
				if(split.length==2){
					int goodId = Integer.parseInt(split[0]);
					int goodCount = Integer.parseInt(split[1]);
					map.put(goodId, goodCount);
				}
			}
			cfg.setConsumeMap(map);

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
