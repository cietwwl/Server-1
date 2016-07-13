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
import com.rwbase.dao.arena.pojo.HeroFettersRobotInfo;
import com.rwbase.dao.fetters.pojo.SynConditionData;
import com.rwbase.dao.fetters.pojo.cfg.dao.FettersBaseCfgDAO;
import com.rwbase.dao.fetters.pojo.cfg.dao.FettersConditionCfgDAO;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersBaseTemplate;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersConditionTemplate;

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

	public void addArenaRobotData(ArenaRobotData data) {
		if (data == null) {
			return;
		}

		TableArenaRobotDataDAO.getDAO().update(data);
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

	/**
	 * 获取机器人对应的羁绊数据
	 * 
	 * @param userId
	 * @param heroModelId
	 * @return
	 */
	public Map<Integer, SynConditionData> getHeroFettersInfo(String userId, int heroModelId) {
		ArenaRobotData arenaRobotData = TableArenaRobotDataDAO.getDAO().get(userId);
		if (arenaRobotData == null) {
			return Collections.emptyMap();
		}

		HeroFettersRobotInfo[] fetters = arenaRobotData.getFetters();
		if (fetters == null || fetters.length <= 0) {
			return Collections.emptyMap();
		}

		FettersBaseCfgDAO cfgDAO = FettersBaseCfgDAO.getCfgDAO();
		FettersConditionCfgDAO conditionCfgDAO = FettersConditionCfgDAO.getCfgDAO();

		Map<Integer, SynConditionData> map = new HashMap<Integer, SynConditionData>();

		for (int i = 0, len = fetters.length; i < len; i++) {
			HeroFettersRobotInfo heroFettersRobotInfo = fetters[i];
			if (heroFettersRobotInfo == null) {
				continue;
			}

			String id = heroFettersRobotInfo.getId();
			int level = heroFettersRobotInfo.getLevel();

			int fettersId = Integer.parseInt(heroModelId + id);
			FettersBaseTemplate baseTmp = cfgDAO.getFettersBaseTemplateById(fettersId);
			if (baseTmp == null) {
				continue;
			}

			// 检查羁绊条件
			List<Integer> fettersConditionList = baseTmp.getFettersConditionList();
			if (fettersConditionList == null || fettersConditionList.isEmpty()) {
				continue;
			}

			SynConditionData synConditionData = new SynConditionData();
			List<Integer> l = new ArrayList<Integer>();

			for (int j = 0, size = fettersConditionList.size(); j < size; j++) {
				FettersConditionTemplate conditionTmp = conditionCfgDAO.getFettersConditionListByIdAndLevel(fettersConditionList.get(j), level);
				if (conditionTmp == null) {
					continue;
				}

				l.add(conditionTmp.getUniqueId());
			}

			synConditionData.setConditionList(l);

			map.put(fettersId, synConditionData);
		}

		return map;
	}

	/**
	 * 时装信息：按照顺序是--->套装，翅膀，宠物
	 * 
	 * @param userId
	 * @return
	 */
	public int[] getFashionIdArr(String userId) {
		ArenaRobotData arenaRobotData = TableArenaRobotDataDAO.getDAO().get(userId);
		if (arenaRobotData == null) {
			return null;
		}

		return arenaRobotData.getFashionId();
	}

	/**
	 * 获取额外的属性Id
	 * 
	 * @param userId
	 * @return
	 */
	public int getExtraAttrId(String userId) {
		ArenaRobotData arenaRobotData = TableArenaRobotDataDAO.getDAO().get(userId);
		if (arenaRobotData == null) {
			return -1;
		}

		return arenaRobotData.getExtraAttrId();
	}
}