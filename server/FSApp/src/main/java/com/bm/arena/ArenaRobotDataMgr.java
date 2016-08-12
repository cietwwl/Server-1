package com.bm.arena;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.rwbase.dao.arena.TableArenaRobotDataDAO;
import com.rwbase.dao.arena.pojo.ArenaRobotData;
import com.rwbase.dao.arena.pojo.HeroFettersRobotInfo;
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

		return RobotHelper.parseTaoist2Info(arenaRobotData.getTaoist());
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

		return RobotHelper.parseFixExpEquip2Info(heroModelId, fixEquip);
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

		return RobotHelper.parseFixNormEquip2Info(heroModelId, fixEquip);
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

		return RobotHelper.parseHeroFettersInfo(heroModelId, fetters);
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