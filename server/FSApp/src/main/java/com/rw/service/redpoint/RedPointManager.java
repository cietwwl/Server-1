package com.rw.service.redpoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.playerdata.EquipMgr;
import com.playerdata.FriendMgr;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.ItemBagMgr;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.playerdata.RedPointMgr;
import com.rwbase.dao.business.SevenDayGifInfo;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.gamble.pojo.TableGamble;
import com.rwbase.dao.item.ComposeCfgDAO;
import com.rwbase.dao.item.pojo.HeroEquipCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.openLevelLimit.pojo.CfgOpenLevelLimit;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.task.pojo.DailyActivityData;
import com.rwbase.dao.task.pojo.TaskItem;
import com.rwproto.GambleServiceProtos.EGambleType;
import com.rwproto.GambleServiceProtos.ELotteryType;
import com.rwproto.MsgDef;
import com.rwproto.RedPointProtos.DisplayRedPoint;
import com.rwproto.RedPointProtos.RedPoint;
import com.rwproto.RedPointProtos.RedPointPushMsg;

public class RedPointManager {

	public static RedPointManager instance = new RedPointManager();

	public static RedPointManager getRedPointManager() {
		return instance;
	}

	// 装备部位列表，游戏逻辑中暂时没有装备部位记录
	private LinkedList<Integer> equipList;

	private RedPointManager() {
		this.equipList = new LinkedList<Integer>();
		for (int i = 0; i < 6; i++) {
			this.equipList.add(i);
		}
	}

	public void checkRedPointVersion(Player player, int version) {
		RedPointMgr mgr = player.getRedPointMgr();
		Map<RedPointType, List<String>> oldMap = mgr.getMap();
		Map<RedPointType, List<String>> currentMap = getRedPointMap(player);
		boolean changed = false;
		if (oldMap == null) {
			changed = true;
		} else if (oldMap.size() != currentMap.size()) {
			changed = true;
		} else {
			for (Map.Entry<RedPointType, List<String>> entry : oldMap.entrySet()) {
				List<String> oldList = entry.getValue();
				List<String> list = currentMap.get(entry.getKey());
				if (list == null) {
					changed = true;
					break;
				}
				int oldSize = oldList.size();
				int newSize = list.size();
				if (oldSize != newSize) {
					changed = true;
					break;
				}
				for (int i = oldSize; --i >= 0;) {
					if (!list.contains(oldList.get(i))) {
						changed = true;
						break;
					}
				}
			}
		}
		if (changed) {
			mgr.setMap(currentMap);
			mgr.setVersion(mgr.getVersion() + 1);
		}
		int curVersion = mgr.getVersion();
		if (version != curVersion) {
			ArrayList<RedPoint> redPointList = new ArrayList<RedPoint>();
			for (Map.Entry<RedPointType, List<String>> entry : currentMap.entrySet()) {
				RedPoint.Builder builder = RedPoint.newBuilder();
				builder.setType(entry.getKey().ordinal());
				builder.addAllFunctionIdList(entry.getValue());
				redPointList.add(builder.build());
			}
			DisplayRedPoint.Builder drBuilder = DisplayRedPoint.newBuilder();
			drBuilder.addAllRedPoints(redPointList);
			drBuilder.setVersion(curVersion);
			RedPointPushMsg.Builder builder = RedPointPushMsg.newBuilder();
			builder.setAllRedPoints(drBuilder);
			player.SendMsg(MsgDef.Command.MSG_RED_POINT, builder.build().toByteString());
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

	public Map<RedPointType, List<String>> getRedPointMap(Player player) {
		EnumMap<RedPointType, List<String>> map = new EnumMap<RedPointType, List<String>>(RedPointType.class);
		// 检查武将红点，后面再抽出来
		ArrayList<String> heroRedPointList = new ArrayList<String>();
		List<String> heroIdList = player.getHeroMgr().getHeroIdList();
		int level = player.getLevel();
		ArrayList<String> heroEquipList = new ArrayList<String>();
		ArrayList<String> upgradeStarList = new ArrayList<String>();
		HeroMgr heroMgr = player.getHeroMgr();
		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		Map<String, RoleCfg> roleCfgCopys = RoleCfgDAO.getInstance().getAllRoleCfgCopy();
		for (String id : heroIdList) {
			Hero hero = heroMgr.getHeroById(id);
			String templateId = hero.getTemplateId();
			// roleCfgCopys.remove(roleCfgCopys.get(String.valueOf(hero.getModelId())));
			removeByModelId(roleCfgCopys, hero.getModelId());
			EquipMgr equipMgr = hero.getEquipMgr();
			List<EquipItem> equipList = equipMgr.getEquipList();
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
					if (itemData == null) {
						HashMap<Integer, Integer> composeItems = ComposeCfgDAO.getInstance().getMate(equipCfgId);
						if (composeItems == null) {
							continue;
						}
						boolean canCompose = true;
						for (Map.Entry<Integer, Integer> entry : composeItems.entrySet()) {
							if (itemBagMgr.getItemCountByModelId(entry.getKey()) < entry.getValue()) {
								canCompose = false;
								break;
							}
						}
						if (!canCompose) {
							continue;
						}
					}

					heroEquipList.add(templateId);
					break;
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
		if (!heroRedPointList.isEmpty()) {
			map.put(RedPointType.ROLE_WINDOW_ADVANCED, heroRedPointList);
		}
		if (!heroEquipList.isEmpty()) {
			map.put(RedPointType.ROLE_WINDOW_DRESS_EQUIP, heroEquipList);
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

		// 检查钓鱼台
		TableGamble gambleItemVo = player.getGambleMgr().getGambleItem();
		if (gambleItemVo.isCanFree(EGambleType.PRIMARY, ELotteryType.ONE)) {
			map.put(RedPointType.FISHING_WINDOW_LOW_LEVEL, Collections.EMPTY_LIST);
		}
		if (gambleItemVo.isCanFree(EGambleType.MIDDLE, ELotteryType.ONE)) {
			map.put(RedPointType.FISHING_WINDOW_MIDDLE_LEVEL, Collections.EMPTY_LIST);
		}
		// 检查等级开放
		CfgOpenLevelLimit taskOpenLevel = (CfgOpenLevelLimit) CfgOpenLevelLimitDAO.getInstance().getCfgById(String.valueOf(eOpenLevelType.TASK.getOrder()));
		if (taskOpenLevel == null || level >= taskOpenLevel.getMinLevel()) {
			// 检查任务
			List<TaskItem> taskEnumeration = player.getTaskMgr().getTaskEnumeration();
			boolean hasDrawState = false;
			for (TaskItem taskItem : taskEnumeration) {
				if (taskItem.getDrawState() == 1) {
					hasDrawState = true;
					break;
				}
			}
			if (hasDrawState) {
				map.put(RedPointType.HOME_WINDOW_TASK, Collections.EMPTY_LIST);
			}
		}
		CfgOpenLevelLimit dailyOpenLevel = (CfgOpenLevelLimit) CfgOpenLevelLimitDAO.getInstance().getCfgById(String.valueOf(eOpenLevelType.DAILY.getOrder()));
		if (dailyOpenLevel == null || level >= dailyOpenLevel.getMinLevel()) {
			// //日常可领取
			boolean dailyCompleted = false;
			List<DailyActivityData> dailyList = player.getDailyActivityMgr().getAllTask();
			for (DailyActivityData data : dailyList) {
				if (data.getCanGetReward() == 1) {
					dailyCompleted = true;
				}
			}
			if (dailyCompleted) {
				map.put(RedPointType.HOME_WINDOW_DAILY, Collections.EMPTY_LIST);
			}
		}
		if (!player.getSignMgr().isSignToday()) {
			map.put(RedPointType.HOME_WINDOW_SIGN_IN, Collections.EMPTY_LIST);
		}
		if (player.getEmailMgr().hasEmail()) {
			map.put(RedPointType.HOME_WINWOW_MAIL, Collections.EMPTY_LIST);
		}
		// 检查七日礼
		SevenDayGifInfo sevenDayGif = player.getDailyGifMgr().getTable();
		if (sevenDayGif.getCounts().size() < sevenDayGif.getCount()) {
			map.put(RedPointType.HOME_WINDOW_SEVER_GIFT, Collections.EMPTY_LIST);
		}

		FriendMgr friendMgr = player.getFriendMgr();
		if (friendMgr.hasReceivePower()) {
			map.put(RedPointType.FRIEND_WINDOW_RECEIVE_POWER, Collections.EMPTY_LIST);
		}
		if (friendMgr.hasRequest()) {
			map.put(RedPointType.FRIEND_WINDOW_ADD_FRIEND, Collections.EMPTY_LIST);
		}

		// 检查开服活动
		List<String> fresherActivity = player.getFresherActivityMgr().getFresherActivityList();
		if (fresherActivity != null && !fresherActivity.isEmpty()) {
			map.put(RedPointType.HOME_WINDOW_OPEN_GIGT, fresherActivity);
		}
		return map;
	}

}
