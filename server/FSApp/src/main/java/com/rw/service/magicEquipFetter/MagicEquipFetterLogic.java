package com.rw.service.magicEquipFetter;

import io.netty.util.collection.IntObjectHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Hero;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.rwbase.dao.fetters.FettersBM;
import com.rwbase.dao.fetters.MagicEquipFetterDataHolder;
import com.rwbase.dao.fetters.FettersBM.SubConditionType;
import com.rwbase.dao.fetters.pojo.cfg.MagicEquipConditionCfg;
import com.rwbase.dao.fetters.pojo.cfg.dao.FetterMagicEquipCfgDao;
import com.rwbase.dao.fetters.pojo.cfg.dao.MagicEquipConditionKey;
import com.rwbase.dao.fetters.pojo.cfg.dao.MagicHeroModelKey;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.HeroFetterProto.HeroFetterType;
import com.rwproto.ItemBagProtos.EItemTypeDef;


/**
 * 检查羁绊逻辑,抽取出来，方便进行热更
 * @author Alex
 * 2016年12月22日 下午5:20:27
 */
public class MagicEquipFetterLogic {
	
	private static MagicEquipFetterLogic instance = new MagicEquipFetterLogic();
	
	
	public static MagicEquipFetterLogic getInstance(){
		return instance;
	}
	
	
	/**
	 * 检查神器数据，是否有羁绊
	 * 
	 * @param player
	 * @param holder 
	 * @param subCondition
	 */
	public void checkAndAddEquipFetter(Player player, MagicEquipFetterDataHolder holder) {

		List<Hero> list = player.getHeroMgr().getAllHeros(player, null);
		for (Hero hero : list) {

			checkOrAddTargetHeroEquipFetter(player, hero, false, holder);

		}
	}
	

	/**
	 * 检查角色法宝羁绊数据
	 * 
	 * @param player
	 * @param cfgList 检查的羁绊列表
	 * @param syn 是否需要同步到客户端
	 * @param holder 
	 * 
	 */
	public boolean checkAndAddMagicFetter(Player player, boolean syn, MagicEquipFetterDataHolder holder) {
		int playerModelId = player.getModelId();
		List<MagicEquipConditionCfg> cfgList = FetterMagicEquipCfgDao.getInstance().getCfgByType(FetterMagicEquipCfgDao.TYPE_MAGICWEAPON, playerModelId);
		// 获取玩家所有的法宝
		List<ItemData> list = ItemBagMgr.getInstance().getItemListByType(player.getUserId(), EItemTypeDef.Magic);
		if (list.isEmpty() && !player.isRobot()) {//这里过滤掉机器人的打印
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
			
			//避免角色等级不到就进阶，造成低阶羁绊无法激活问题，所以在这里要找到玩家同类型(以子类型进行判断)的最高阶法宝，将它的等级作为低阶法宝的
			//保底等级。
			
			//找到此法宝的配置
			MagicHeroModelKey key  = new MagicHeroModelKey(magicModelId, playerModelId);
			MagicEquipConditionCfg config = FetterMagicEquipCfgDao.getInstance().getByMagicHeroModelIDKey(key);
			if(config == null){
				continue;
			}
			
			//找到法宝子类型的配置列表
			List<MagicEquipConditionCfg> subType = FetterMagicEquipCfgDao.getInstance().getCfgListByMagicSubType(config.getSubType());
			for (MagicEquipConditionCfg subTypeCfg : subType) {
				//过滤出低阶法宝并且比较设置保底等级
				if(subTypeCfg.getConditionLevel() >= config.getConditionLevel()){
					continue;
				}
				int subModelID = subTypeCfg.getModelIDList().get(0);
				Integer curLv = magicMaxLevelMap.get(subModelID);
				if(curLv == null || curLv < magicLevel){
					magicMaxLevelMap.put(subTypeCfg.getModelIDList().get(0), magicLevel);
				}
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
				// 只要取等级最高的羁绊
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
//			StringBuilder sb = new StringBuilder("---------------------新增角色"+player.getUserName()+"的法宝羁绊：");
//			for (Integer id : updateList) {
//				sb.append(id).append(",");
//			}
//			System.out.println(sb.toString());
			FettersBM.sendFetterNotifyMsg(player, updateList, HeroFetterType.MagicFetter);
		}
		return changed;
	}

	
	
	/**
	 * 检查目标英雄神器羁绊
	 * 
	 * @param player
	 * @param hero
	 * @param syn
	 * @param holder 
	 */
	public void checkOrAddTargetHeroEquipFetter(Player player, Hero hero, boolean syn, MagicEquipFetterDataHolder holder) {
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

	
}
