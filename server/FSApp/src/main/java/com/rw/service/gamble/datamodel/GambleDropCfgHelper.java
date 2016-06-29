package com.rw.service.gamble.datamodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.common.RefInt;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.service.gamble.GambleLogicHelper;
import com.rwbase.common.config.CfgCsvHelper;
/*
<bean class="com.rw.service.gamble.datamodel.GambleDropCfgHelper"  init-method="init" />
*/

public class GambleDropCfgHelper extends CfgCsvDao<GambleDropCfg> {
	public static GambleDropCfgHelper getInstance() {
		return SpringContextUtil.getBean(GambleDropCfgHelper.class);
	}

	private Map<Integer,GambleDropGroup> dropGroupMappings;
	
	@Override
	public Map<String, GambleDropCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("gamble/GambleDropCfg.csv",GambleDropCfg.class);
		Collection<GambleDropCfg> vals = cfgCacheMap.values();
		HashMap<Integer,LinkedList<GambleDropCfg>> tmp = new HashMap<Integer, LinkedList<GambleDropCfg>>(vals.size());
		for (GambleDropCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
			if (cfg.getWeight() < 0){
				throw new RuntimeException("权重不能小于零"+"key="+cfg.getKey()+",权重:"+cfg.getWeight());
			}
			LinkedList<GambleDropCfg> old = tmp.get(cfg.getItemGroup());
			if (old == null){
				old = new LinkedList<GambleDropCfg>();
				tmp.put(cfg.getItemGroup(), old);
			}
			old.add(cfg);
		}
		dropGroupMappings = new HashMap<Integer, GambleDropGroup>(tmp.size());
		Set<Entry<Integer, LinkedList<GambleDropCfg>>> tmpCol = tmp.entrySet();
		for (Entry<Integer, LinkedList<GambleDropCfg>> entry : tmpCol) {
			dropGroupMappings.put(entry.getKey(), GambleDropGroup.Create(entry.getValue()));
		}
		return cfgCacheMap;
	}
	
	@Override
	public void CheckConfig() {
		// 跨表检查物品/英雄是否存在
		Collection<GambleDropCfg> vals = cfgCacheMap.values();
		for (GambleDropCfg cfg : vals) {
			if (!DropMissingCfgHelper.getInstance().isDropMissingId(cfg.getItemID())) {
				if (!GambleLogicHelper.isValidHeroOrItemId(cfg.getItemID())) {
					throw new RuntimeException("无效物品/英雄ID:" + cfg.getItemID());
				}
			}
		}
	}

	public String getRandomDrop(Random r,int groupKey,RefInt slotCount){
		RefInt weight = null;
		return getRandomDrop(r,groupKey,slotCount,weight);
	}
	
	public String getRandomDrop(Random r,int groupKey,RefInt slotCount,RefInt weight){
		GambleDropGroup group = dropGroupMappings.get(groupKey);
		if (group == null){
			return null;
		}
		return group.getRandomGroup(r, slotCount,weight);
	}
	
	public GambleDropGroup getGroup(int groupKey){
		return dropGroupMappings.get(groupKey);
	}
	
	public boolean checkInGroup(int groupKey,String itemModelId){
		if (StringUtils.isBlank(itemModelId))
			return false;
		GambleDropGroup group = dropGroupMappings.get(groupKey);
		if (group == null){
			return false;
		}
		return group.checkInGroup(itemModelId);
	}

	/**
	 * 连续生成N个热点
	 * 避免重复，如果热电组人数不够才允许重复
	 * @param r
	 * @param hotPlanId
	 * @param hotCount
	 * @param guanrateeHero
	 * @return
	 */
	public List<Pair<String, Integer>> getHotRandomDrop(Random r, int hotPlanId, int hotCount,String guanrateeHero) {
		GambleDropGroup group = dropGroupMappings.get(hotPlanId);
		if (group == null){
			return null;
		}
		return group.getHotRandomGroup(r, hotCount,guanrateeHero);
	}

	public int getDropGroupSize(int groupKey) {
		GambleDropGroup group = dropGroupMappings.get(groupKey);
		if (group == null){
			return 0;
		}
		return group.size();
	}
}