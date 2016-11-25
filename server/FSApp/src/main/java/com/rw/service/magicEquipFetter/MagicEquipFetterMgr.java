package com.rw.service.magicEquipFetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.common.Action;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Hero;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.rwbase.dao.fetters.FettersBM;
import com.rwbase.dao.fetters.FettersBM.SubConditionType;
import com.rwbase.dao.fetters.MagicEquipFetterDataHolder;
import com.rwbase.dao.fetters.pojo.cfg.MagicEquipConditionCfg;
import com.rwbase.dao.fetters.pojo.cfg.dao.FetterMagicEquipCfgDao;
import com.rwbase.dao.fetters.pojo.cfg.dao.MagicEquipConditionKey;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.HeroFetterProto.HeroFetterType;
import com.rwproto.ItemBagProtos.EItemTypeDef;

import io.netty.util.collection.IntObjectHashMap;

/**
 * 法宝神器羁绊管理类
 * 
 * @author Alex
 *
 * 2016年7月18日 下午5:52:02
 */
public class MagicEquipFetterMgr {

	private MagicEquipFetterDataHolder holder;

	private final List<Action> actionListener = new ArrayList<Action>();

	public void init(Player player) {
		holder = new MagicEquipFetterDataHolder(player.getUserId());
	}

	public void loginNotify(Player player) {
		holder.synAllData(player, 0);
	}

	/**
	 * 检查角色数据
	 * 
	 * @param player
	 */
	public void checkPlayerData(Player player) {
		checkAndAddMagicFetter(player, false);
		checkAndAddEquipFetter(player);
	}

	/**
	 * 检查神器数据，是否有羁绊
	 * 
	 * @param player
	 * @param subCondition
	 */
	private void checkAndAddEquipFetter(Player player) {

		// List<Hero> list = player.getHeroMgr().getAllHeros(null);
		List<Hero> list = player.getHeroMgr().getAllHeros(player, null);
		for (Hero hero : list) {

			checkOrAddTargetHeroEquipFetter(player, hero, false);

		}
	}

	/**
	 * 检查目标英雄神器羁绊
	 * 
	 * @param player
	 * @param hero
	 * @param syn
	 */
	private void checkOrAddTargetHeroEquipFetter(Player player, Hero hero, boolean syn) {
		List<FixNormEquipDataItem> itemList = hero.getFixNormEquipMgr().getFixNorEquipItemList(hero.getUUId());

		List<MagicEquipConditionCfg> temp = new ArrayList<MagicEquipConditionCfg>();
		for (FixNormEquipDataItem item : itemList) {

			List<MagicEquipConditionCfg> cfgList = FetterMagicEquipCfgDao.getInstance().getCfgListByModelID(Integer.parseInt(item.getCfgId()));
			if (cfgList.isEmpty()) {
				continue;
			}

			for (MagicEquipConditionCfg cfg : cfgList) {
				// 找出所有合符条件的配置
				boolean match = true;
				Map<Integer, Map<Integer, Integer>> conditionMap = cfg.getConditionMap();
				for (Iterator<Entry<Integer, Map<Integer, Integer>>> itr = conditionMap.entrySet().iterator(); itr.hasNext();) {
					Entry<Integer, Map<Integer, Integer>> entry = itr.next();

					match = checkFixEquipMatch(entry, itemList);
					if (!match) {
						break;
					}

				}
				if (match) {
					temp.add(cfg);
					// System.out.println(String.format("找到合适的神器羁绊，羁绊id：[%s],羁绊描述[%s],羁绊条件:[%s]",
					// cfg.getUniqueId(),cfg.getFettersAttrDesc(),cfg.getSubConditionValue()));
				}
			}
		}
		// if(temp.isEmpty()){ 去掉这个，因为有可能会降星到0
		// return;
		// }

		// 去掉所有类型相同的配置，只保留最高级的
		MagicEquipConditionCfg tempCfg = null;
		for (MagicEquipConditionCfg cfg : temp) {
			if (tempCfg == null) {
				tempCfg = cfg;
				continue;
			}
			if (cfg.getConditionLevel() > tempCfg.getConditionLevel()) {
				tempCfg = cfg;
			}
		}

		holder.checkFixEquipFetterRecord(tempCfg, hero.getModeId());

		if (syn) {
			holder.synAllData(player, 0);
		}
//		if (update && tempCfg != null) {
//			FettersBM.sendFetterNotifyMsg(player, Arrays.asList(tempCfg.getUniqueId()), HeroFetterType.FixEquipFetter);
//		}
	}

	/**
	 * 检查神器条件是否满足条件产生羁绊
	 * 
	 * @param entry
	 * @param itemList
	 * @return
	 */
	private boolean checkFixEquipMatch(Entry<Integer, Map<Integer, Integer>> map, List<FixNormEquipDataItem> itemList) {

		FixNormEquipDataItem item = null;
		for (FixNormEquipDataItem data : itemList) {
			if (Integer.parseInt(data.getCfgId()) == map.getKey()) {
				item = data;
				break;
			}
		}

		if (item == null) {
			return false;
		}

		Map<Integer, Integer> condition = map.getValue();

		boolean match = true;
		for (Iterator<Entry<Integer, Integer>> itr = condition.entrySet().iterator(); itr.hasNext();) {
			Entry<Integer, Integer> entry = itr.next();
			Integer key = entry.getKey();
			int value = entry.getValue();
			SubConditionType type = FettersBM.SubConditionType.getEnum(key);
			if (type == null) {
				GameLog.error(LogModule.COMMON, "MagicEquipFetterMgr", "检查神器羁绊条件产生异常，神器 ID:" + item.getCfgId() + ",条件类型：" + key, null);
				continue;
			}

			switch (type) {
			case QUALITY:
				if (item.getQuality() < value) {
					match = false;
				}
				break;
			case STAR:
				if (item.getStar() < value) {
					match = false;
				}
				break;
			case LEVEL:
				if (item.getLevel() < value) {
					match = false;
				}

				break;

			default:
				break;
			}

			if (!match) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 检查角色法宝羁绊数据
	 * 
	 * @param player
	 * @param cfgList 检查的羁绊列表
	 * @param syn 是否需要同步到客户端
	 * 
	 */
	private boolean checkAndAddMagicFetter(Player player, boolean syn) {
		int playerModelId = player.getModelId();
		List<MagicEquipConditionCfg> cfgList = FetterMagicEquipCfgDao.getInstance().getCfgByType(FetterMagicEquipCfgDao.TYPE_MAGICWEAPON, playerModelId);
		// 获取玩家所有的法宝
		List<ItemData> list = ItemBagMgr.getInstance().getItemListByType(player.getUserId(), EItemTypeDef.Magic);
		if (list.isEmpty() && !player.isRobot()) {
			GameLog.error(LogModule.COMMON, "MagicEquipFetterMgr[checkAndAddMagicFetter]", String.format("检查角色[%s]法宝羁绊数据，发现角色没有法宝道具！", player.getUserName()), null);
			return false;
		}
		// modify by Jamaz @2016-10-13
		// 玩家各modelId 最高级的法宝，用于检测是否羁绊条件
		int dataSize = list.size();
		IntObjectHashMap<Integer> magicMaxLevelMap = new IntObjectHashMap<Integer>(dataSize << 1);
		for (int i = dataSize; --i >= 0;) {
			ItemData magic = list.get(i);
			int magicModelId = magic.getModelId();
			Integer maxLevel = magicMaxLevelMap.get(magicModelId);
			int magicLevel = magic.getMagicLevel();
			if (maxLevel == null || maxLevel < magicLevel) {
				magicMaxLevelMap.put(magicModelId, magicLevel);
			}
		}
		// 根据玩家当前拥有的法宝，找出符合资格的羁绊
		HashMap<MagicEquipConditionKey, MagicEquipConditionCfg> temp = new HashMap<MagicEquipConditionKey, MagicEquipConditionCfg>();
		for (MagicEquipConditionCfg cfg : cfgList) {
			// 判断一下羁绊是不是合适主角英雄，因为英雄会转职
			MagicEquipConditionKey key = cfg.getCompositeKey();
			int conditionLevel = cfg.getConditionLevel();
			MagicEquipConditionCfg oldCfg = temp.get(key);
			if (oldCfg != null) {
				// TODO 这里要考虑是大于，还是大于等于
				if (oldCfg.getConditionLevel() >= conditionLevel) {
					continue;
				}
			}
			// 找出所有合符条件的配置
			boolean match = true;
			Map<Integer, Map<Integer, Integer>> conditionMap = cfg.getConditionMap();
			for (Iterator<Entry<Integer, Map<Integer, Integer>>> itr = conditionMap.entrySet().iterator(); itr.hasNext();) {
				Entry<Integer, Map<Integer, Integer>> entry = itr.next();
				Map<Integer, Integer> value = entry.getValue();
				Integer level = value.get(FettersBM.SubConditionType.LEVEL.type);
				if (level != null) {
					Integer maxLevel = magicMaxLevelMap.get(entry.getKey());
					if (maxLevel == null || maxLevel < level) {
						match = false;
						break;
					}
				}
			}
			if (match) {
				temp.put(key, cfg);
			}
		}

		List<Integer> updateList = new ArrayList<Integer>();
		boolean changed = holder.compareMagicFetterRcord(temp, playerModelId, updateList);
		if (syn) {
			// TODO 这里为什么不用dataVersio而用0
			holder.synAllData(player, 0);
		}
		if (changed && updateList.size() > 0) {
			FettersBM.sendFetterNotifyMsg(player, updateList, HeroFetterType.MagicFetter);
		}
		return changed;
	}

	/**
	 * 检查法宝条件
	 * 
	 * @param entry
	 * @param list
	 * @return
	 */
	public boolean checkItemMatch(Entry<Integer, Map<Integer, Integer>> entry, List<ItemData> list) {
		ItemData item = null;
		for (ItemData itemData : list) {
			if (itemData.getModelId() == entry.getKey()) {
				if (item == null) {
					item = itemData;
				} else if (item.getMagicLevel() <= itemData.getMagicLevel()) {// 找出最高等级
					item = itemData;
				}
			}
		}
		if (item == null) {
			return false;
		}
		// 检查条件是否匹配
		Map<Integer, Integer> value = entry.getValue();
		Integer level = value.get(FettersBM.SubConditionType.LEVEL.type);
		if (level != null && level > item.getMagicLevel()) {
			return false;
		}
		return true;
	}

	/**
	 * 法宝强化进阶通知
	 * 
	 * @param player
	 * @param itemData
	 */
	public void notifyMagicChange(Player player) {
		if (checkAndAddMagicFetter(player, true)) {
			notifyListenerAction();
		}
	}

	/**
	 * 英雄变动通知
	 * 
	 * @param player
	 * @param hero
	 */
	public void notifyHeroChange(Player player, Hero hero) {
		if (hero.isMainRole()) {
			checkAndAddMagicFetter(player, false);
		}
		checkOrAddTargetHeroEquipFetter(player, hero, false);
		holder.synAllData(player, 0);
		notifyListenerAction();
	}

	/**
	 * 获取英雄的神器羁绊列表
	 * 
	 * @param modelId
	 */
	public List<Integer> getHeroFixEqiupFetter(int modelId) {
		return holder.getFixEquipFetterByModelID(modelId);
	}

	/**
	 * 获取法宝的羁绊列表
	 */
	public List<Integer> getMagicFetter() {
		return holder.getMagicFetters();
	}

	public void reChangeCallBack(Action action) {
		actionListener.add(action);
	}

	private void notifyListenerAction() {
		for (Action action : actionListener) {
			action.doAction();
		}
	}
}
