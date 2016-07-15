package com.bm.arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.fixEquip.cfg.RoleFixEquipCfg;
import com.playerdata.fixEquip.cfg.RoleFixEquipCfgDAO;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfg;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfgHelper;
import com.rwbase.dao.arena.TableArenaRobotDataDAO;
import com.rwbase.dao.arena.pojo.ArenaRobotData;
import com.rwbase.dao.fetters.pojo.SynConditionData;

/*
 * @author HC
 * @date 2016年7月14日 下午8:38:27
 * @Description 
 */
public class ArenaRobotDataMgr {
	private static ArenaRobotDataMgr mgr = new ArenaRobotDataMgr();

	public static ArenaRobotDataMgr getMgr() {
		return mgr;
	}

	ArenaRobotDataMgr() {
	}

	/**
	 * 获取对应机器人的道术等级列表
	 * 
	 * @param userId
	 * @return
	 */
	public Map<Integer, Integer> getRobotTaoistMap(String userId) {
		ArenaRobotData arenaRobotData = TableArenaRobotDataDAO.getDAO().get(userId);
		if (arenaRobotData == null) {
			return null;
		}

		Map<Integer, Integer> taoistLevelMap = new HashMap<Integer, Integer>();

		int[] taoist = arenaRobotData.getTaoist();
		int len = taoist.length;
		for (int i = 0; i < len; i++) {
			int level = taoist[i];
			if (level <= 0) {
				continue;
			}

			int tag = i + 1;
			List<TaoistMagicCfg> list = TaoistMagicCfgHelper.getInstance().getTaoistCfgListByTag(tag);
			if (list == null || list.isEmpty()) {
				continue;
			}

			for (int j = 0, size = list.size(); j < size; j++) {
				taoistLevelMap.put(list.get(j).getKey(), level);
			}
		}

		return taoistLevelMap;
	}

	/**
	 * 获取经验类神器
	 * 
	 * @param userId
	 * @param heroModelId
	 * @return
	 */
	public List<FixExpEquipDataItem> getFixExpEquipList(String userId, int heroModelId) {
		ArenaRobotData arenaRobotData = TableArenaRobotDataDAO.getDAO().get(userId);
		if (arenaRobotData == null) {
			return Collections.emptyList();
		}

		int[] fixEquip = arenaRobotData.getFixEquip();
		if (fixEquip.length != 3) {
			return Collections.emptyList();
		}

		RoleFixEquipCfg cfg = RoleFixEquipCfgDAO.getInstance().getConfig(String.valueOf(heroModelId));
		if (cfg == null) {
			return Collections.emptyList();
		}

		List<String> expCfgList = cfg.getExpCfgIdList();
		int size = expCfgList.size();

		List<FixExpEquipDataItem> fixExpEquipList = new ArrayList<FixExpEquipDataItem>(size);

		for (int i = 0; i < size; i++) {
			FixExpEquipDataItem expEquipDataItem = new FixExpEquipDataItem();
			expEquipDataItem.setCfgId(expCfgList.get(i));
			expEquipDataItem.setLevel(fixEquip[0]);
			expEquipDataItem.setQuality(fixEquip[1]);
			expEquipDataItem.setStar(fixEquip[2]);
			fixExpEquipList.add(expEquipDataItem);
		}

		return fixExpEquipList;
	}

	/**
	 * 获取普通类神器
	 * 
	 * @param userId
	 * @param heroModelId
	 * @return
	 */
	public List<FixNormEquipDataItem> getFixNormEquipList(String userId, int heroModelId) {
		ArenaRobotData arenaRobotData = TableArenaRobotDataDAO.getDAO().get(userId);
		if (arenaRobotData == null) {
			return Collections.emptyList();
		}

		int[] fixEquip = arenaRobotData.getFixEquip();
		if (fixEquip.length != 3) {
			return Collections.emptyList();
		}

		RoleFixEquipCfg cfg = RoleFixEquipCfgDAO.getInstance().getConfig(String.valueOf(heroModelId));
		if (cfg == null) {
			return Collections.emptyList();
		}

		List<String> normCfgList = cfg.getNormCfgIdList();
		int size = normCfgList.size();

		List<FixNormEquipDataItem> fixNormEquipList = new ArrayList<FixNormEquipDataItem>(size);

		for (int i = 0; i < size; i++) {
			FixNormEquipDataItem normEquipDataItem = new FixNormEquipDataItem();
			normEquipDataItem.setCfgId(normCfgList.get(i));
			normEquipDataItem.setLevel(fixEquip[0]);
			normEquipDataItem.setQuality(fixEquip[1]);
			normEquipDataItem.setStar(fixEquip[2]);
			fixNormEquipList.add(normEquipDataItem);
		}

		return fixNormEquipList;
	}

	public Map<Integer, SynConditionData> getHeroFettersInfo() {
		return null;
	}
}