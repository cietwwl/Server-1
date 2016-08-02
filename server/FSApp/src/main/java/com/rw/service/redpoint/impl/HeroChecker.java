package com.rw.service.redpoint.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.playerdata.EquipMgr;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.ItemBagMgr;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.item.ComposeCfgDAO;
import com.rwbase.dao.item.pojo.HeroEquipCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;

public class HeroChecker implements RedPointCollector {

	// 装备部位列表，游戏逻辑中暂时没有装备部位记录
	private LinkedList<Integer> equipList;

	{
		this.equipList = new LinkedList<Integer>();
		for (int i = 0; i < 6; i++) {
			this.equipList.add(i);
		}
	}

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map) {
		ArrayList<String> heroRedPointList = new ArrayList<String>();
//		List<String> heroIdList = player.getHeroMgr().getHeroIdList();
		List<String> heroIdList = player.getHeroMgr().getHeroIdList(player);
		ArrayList<String> heroEquipList = new ArrayList<String>();
		ArrayList<String> upgradeStarList = new ArrayList<String>();
		HeroMgr heroMgr = player.getHeroMgr();
		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		Map<String, RoleCfg> roleCfgCopys = RoleCfgDAO.getInstance().getAllRoleCfgCopy();
		for (String id : heroIdList) {
//			Hero hero = heroMgr.getHeroById(id);
			Hero hero = heroMgr.getHeroById(player, id);
			String templateId = hero.getTemplateId();
			// roleCfgCopys.remove(roleCfgCopys.get(String.valueOf(hero.getModelId())));
			removeByModelId(roleCfgCopys, hero.getModeId());
			EquipMgr equipMgr = hero.getEquipMgr();
			List<EquipItem> equipList = equipMgr.getEquipList(hero.getUUId());
			if (equipList.size() >= 6) {
				heroRedPointList.add(templateId);
			} else {
				// 英雄对应品质的装备配置id(对应modelId)
				List<Integer> equipCfgList = RoleQualityCfgDAO.getInstance().getEquipList(hero.getQualityId());
				int size = equipList.size();
				LinkedList<Integer> needEquipList;
				if (size == 0) {
					needEquipList = this.equipList;
				} else {
					needEquipList = new LinkedList<Integer>(this.equipList);
					for (int i = equipList.size(); --i >= 0;) {
						EquipItem item = equipList.get(i);
						Integer equipIndex = item.getEquipIndex();
						needEquipList.remove(equipIndex);
					}
				}
				// 有可穿装备
				for (int equipIndex : needEquipList) {
					int equipCfgId = equipCfgList.get(equipIndex);
					// 先检查等级
					HeroEquipCfg equipCfg = ItemCfgHelper.getHeroEquipCfg(equipCfgId);
					if (equipCfg == null) {
						continue;
					}
					if (equipCfg.getLevel() > hero.getLevel()) {
						continue;
					}
					ItemData itemData = itemBagMgr.getFirstItemByModelId(equipCfgId);
					// if (itemData == null) {
					// HashMap<Integer, Integer> composeItems =
					// ComposeCfgDAO.getInstance().getMate(equipCfgId);
					// if (composeItems == null) {
					// continue;
					// }
					// boolean canCompose = true;
					// for (Map.Entry<Integer, Integer> entry :
					// composeItems.entrySet()) {
					// if (itemBagMgr.getItemCountByModelId(entry.getKey()) <
					// entry.getValue()) {
					// canCompose = false;
					// break;
					// }
					// }
					// if (!canCompose) {
					// continue;
					// }
					// }
					if (itemData != null) {
						heroEquipList.add(templateId);
						break;
					}
				}
			}

			RoleCfg heroCfg = RoleCfgDAO.getInstance().getConfig(hero.getTemplateId());
			int risingNumber = heroCfg.getRisingNumber();
			if (risingNumber <= 0) {
				continue;
			}
			// 修改红点升星最高级的判断条件
			String nextRoleId = heroCfg.getNextRoleId();
			if (nextRoleId == null || nextRoleId.isEmpty()) {
				continue;
			}
			int soulStoneCount = itemBagMgr.getItemCountByModelId(heroCfg.getSoulStoneId());
			if (soulStoneCount >= risingNumber) {
				upgradeStarList.add(templateId);
			}
		}
		// if (!heroRedPointList.isEmpty()) {
		// map.put(RedPointType.ROLE_WINDOW_ADVANCED, heroRedPointList);
		// }
		// 穿装红点用进阶代替
		if (!heroEquipList.isEmpty()) {
			map.put(RedPointType.ROLE_WINDOW_ADVANCED, heroEquipList);
		}
		if (!upgradeStarList.isEmpty()) {
			map.put(RedPointType.ROLE_WINDOW_UPGRADE_STAR, upgradeStarList);
		}

		ArrayList<String> summonHeroList = new ArrayList<String>();
		// 检查可召唤佣兵
		for (RoleCfg roleCfg : roleCfgCopys.values()) {
			int soulStoneId = roleCfg.getSoulStoneId();
			int summonNumber = roleCfg.getSummonNumber();
			if (itemBagMgr.getItemCountByModelId(soulStoneId) >= summonNumber) {
				summonHeroList.add(roleCfg.getRoleId());
			}
		}

		if (!summonHeroList.isEmpty()) {
			map.put(RedPointType.HERO_LIST_WINDOW_ROLE_SUMMON, summonHeroList);
		}
	}

	private void removeByModelId(Map<String, RoleCfg> roleCfgCopys, int modelId) {
		for (Iterator<RoleCfg> it = roleCfgCopys.values().iterator(); it.hasNext();) {
			RoleCfg cfg = it.next();
			if (cfg.getModelId() == modelId) {
				it.remove();
			}
		}
	}
}
