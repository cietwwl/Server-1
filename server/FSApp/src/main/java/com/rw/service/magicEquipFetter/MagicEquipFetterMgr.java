package com.rw.service.magicEquipFetter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.common.Action;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.rwbase.dao.fetters.FettersBM;
import com.rwbase.dao.fetters.FettersBM.SubConditionType;
import com.rwbase.dao.fetters.MagicEquipFetterDataHolder;
import com.rwbase.dao.fetters.pojo.cfg.MagicEquipConditionCfg;
import com.rwbase.dao.fetters.pojo.cfg.dao.FetterMagicEquipCfgDao;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.ItemBagProtos.EItemTypeDef;

/**
 * 法宝神器羁绊管理类
 * @author Alex
 *
 * 2016年7月18日 下午5:52:02
 */
public class MagicEquipFetterMgr {

	
	
	private MagicEquipFetterDataHolder holder;
	

	private final List<Action> actionListener = new ArrayList<Action>();

	
	public void init(Player player){
		holder = new MagicEquipFetterDataHolder(player.getUserId());
	}



	public void loginNotify(Player player){
		//检查一下旧数据,如果已经开启了的羁绊而数据库里又没有的，要添加
		checkPlayerData(player);
		holder.synAllData(player, 0);
	}
	
	/**
	 * 检查角色数据
	 * @param player
	 */
	private void checkPlayerData(Player player) {
		checkAndAddMagicFetter(player, false);
		checkAndAddEquipFetter(player);
	}



	/**
	 * 检查神器数据，是否有羁绊
	 * @param player
	 * @param subCondition
	 */
	private void checkAndAddEquipFetter(Player player) {
		
		List<Hero> list = player.getHeroMgr().getAllHeros(null);
		for (Hero hero : list) {
			
			checkOrAddTargetHeroEquipFetter(player, hero, false);
			
		}
	}

	/**
	 * 检查目标英雄神器羁绊
	 * @param player
	 * @param hero
	 * @param syn
	 */
	private void checkOrAddTargetHeroEquipFetter(Player player, Hero hero, boolean syn){
		List<FixNormEquipDataItem> itemList = hero.getFixNormEquipMgr().getFixNorEquipItemList(hero.getUUId());
		
		List<MagicEquipConditionCfg> temp = new ArrayList<MagicEquipConditionCfg>();
		for (FixNormEquipDataItem item : itemList) {
			
			List<MagicEquipConditionCfg> cfgList = FetterMagicEquipCfgDao.getInstance().getCfgListByModelID(Integer.parseInt(item.getCfgId()));
			if(cfgList.isEmpty()){
				continue;
			}
			
			for (MagicEquipConditionCfg cfg : cfgList) {
				//找出所有合符条件的配置
				boolean match = true;
				Map<Integer, Map<Integer, Integer>> conditionMap = cfg.getConditionMap();
				for (Iterator<Entry<Integer, Map<Integer, Integer>>> itr = conditionMap.entrySet().iterator(); itr.hasNext();) {
					Entry<Integer, Map<Integer, Integer>> entry =  itr.next();
					
					match = checkFixEquipMatch(entry, itemList);
					if(!match){
						break;
					}
					
				}
				if(match){
					temp.add(cfg);
//					System.out.println(String.format("找到合适的神器羁绊，羁绊id：[%s],羁绊描述[%s],羁绊条件:[%s]", cfg.getUniqueId(),cfg.getFettersAttrDesc(),cfg.getSubConditionValue()));
				}
			}
		}
		if(temp.isEmpty()){
			return;
		}
		
		//去掉所有类型相同的配置，只保留最高级的
		Set<MagicEquipConditionCfg> tempSet = new HashSet<MagicEquipConditionCfg>();
		tempSet.addAll(temp);
		List<MagicEquipConditionCfg> remove = new ArrayList<MagicEquipConditionCfg>();
		for (MagicEquipConditionCfg cfg : temp) {
			for (MagicEquipConditionCfg targetCfg : tempSet) {
				if(cfg.getType() == targetCfg.getType() && 
						cfg.getSubType() == targetCfg.getSubType() && 
								cfg.getUniqueId() != targetCfg.getUniqueId() && 
										cfg.getConditionLevel() <= targetCfg.getConditionLevel()){
					remove.add(cfg);
				}
			}
		}
		
		tempSet.removeAll(remove);
		
		holder.checkFixEquipFetterRecord(tempSet, hero.getModelId());

		if(syn){
			holder.synAllData(player, 0);
		}
	}

	
	/**
	 * 检查神器条件是否满足条件产生羁绊
	 * @param entry
	 *@param itemList
	 * @return
	 */
	private boolean checkFixEquipMatch(Entry<Integer, Map<Integer, Integer>> map,
			List<FixNormEquipDataItem> itemList) {
		
		
		FixNormEquipDataItem item = null;
		for (FixNormEquipDataItem data : itemList) {
			if(Integer.parseInt(data.getCfgId()) == map.getKey()){
				item = data;
				break;
			}
		}
		
		
		if(item == null){
			return false;
		}
		
		Map<Integer, Integer> condition = map.getValue();
		
		boolean match = true;
		for (Iterator<Entry<Integer, Integer>> itr = condition.entrySet().iterator(); itr.hasNext();) {
			Entry<Integer, Integer> entry =  itr.next();
			Integer key = entry.getKey();
			int value = entry.getValue();
			SubConditionType type = FettersBM.SubConditionType.getEnum(key);
			if(type == null){
				GameLog.error(LogModule.COMMON, "MagicEquipFetterMgr", "检查神器羁绊条件产生异常，神器 ID:" + item.getCfgId()
						+ ",条件类型：" + key, null);
				continue;
			}
			
			switch (type) {
			case QUALITY:
				if(item.getQuality() < value){
					match = false;
				}
				break;
			case STAR:
				if(item.getStar() < value){
					match = false;
				}
				break;
			case LEVEL:
				if(item.getLevel() < value){
					match = false;
				}
					
				break;

			default:
				break;
			}
			
			if(!match){
				return false;
			}
		}
		
		return true;
	}




	/**
	 * 检查角色法宝羁绊数据
	 * @param player
	 * @param cfgList 检查的羁绊列表
	 * @param syn 是否需要同步到客户端
	 */
	private void checkAndAddMagicFetter(Player player, boolean syn) {
		List<MagicEquipConditionCfg> cfgList = FetterMagicEquipCfgDao.getInstance().getCfgByType(FetterMagicEquipCfgDao.TYPE_MAGICWEAPON);
		
		List<ItemData> list = player.getItemBagMgr().getItemListByType(EItemTypeDef.Magic);
		if(list.isEmpty()){
			GameLog.error(LogModule.COMMON, "MagicEquipFetterMgr[checkAndAddMagicFetter]", String.format("检查角色[%s]法宝羁绊数据，发现角色没有法宝道具！", player.getUserName()), null);
			return;
		}
		Set<MagicEquipConditionCfg> temp = new HashSet<MagicEquipConditionCfg>();
		int modelId = player.getModelId();
		
		for (MagicEquipConditionCfg cfg : cfgList) {
			//判断一下羁绊是不是合适主角英雄，因为英雄会转职
			if(Integer.parseInt(cfg.getHeroModelID()) != modelId){
				continue;
			}
			
			
			//找出所有合符条件的配置
			boolean match = true;
			Map<Integer, Map<Integer, Integer>> conditionMap = cfg.getConditionMap();
			for (Iterator<Entry<Integer, Map<Integer, Integer>>> itr = conditionMap.entrySet().iterator(); itr.hasNext();) {
				Entry<Integer, Map<Integer, Integer>> entry =  itr.next();
				
				match = checkItemMatch(entry, list);
				if(!match){
					break;
				}
				
			}
			if(match){
				temp.add(cfg);
//				System.out.println(String.format("找到合适的法宝羁绊，羁绊id：[%s],羁绊描述[%s],羁绊条件:[%s]", cfg.getUniqueId(),cfg.getFettersAttrDesc(),cfg.getSubConditionValue()));
			}
		}
		
//		if(temp.isEmpty()){ 角色可能会用降星把所有的法宝羁绊去掉，所以这里不能直接的return
//			return;
//		}
		if(!temp.isEmpty()){
			
			//去掉所有法宝类型重复的配置，使用等级最高的
			List<MagicEquipConditionCfg> remove = new ArrayList<MagicEquipConditionCfg>();
			List<MagicEquipConditionCfg> p = new ArrayList<MagicEquipConditionCfg>();
			p.addAll(temp);
			for (MagicEquipConditionCfg conditionCfg : p) {
				for (MagicEquipConditionCfg sub : temp) {
					if(sub.getType() == conditionCfg.getType() && 
							sub.getSubType() == conditionCfg.getSubType() && 
							sub.getUniqueId() != conditionCfg.getUniqueId() && 
							conditionCfg.getConditionLevel() <= sub.getConditionLevel()){
						remove.add(conditionCfg);
						break;
					}
				}
			}
			
			temp.removeAll(remove);
		}
		holder.compareMagicFetterRcord(temp, modelId);


		if(syn){
			holder.synAllData(player, 0);
		}
		
	}
	
	
	
	/**
	 * 检查法宝条件
	 * @param entry
	 * @param list
	 * @return
	 */
	public boolean checkItemMatch(Entry<Integer, Map<Integer, Integer>> entry, List<ItemData> list){
		ItemData item = null;
		for (ItemData itemData : list) {
			if(itemData.getModelId() == entry.getKey()){
				if(item == null){
					item = itemData;
				}else if(item.getMagicLevel() <= itemData.getMagicLevel()){//找出最高等级
					item = itemData;
				}
			}
		}
		if(item == null){
			return false;
		}
		//检查条件是否匹配
		Map<Integer, Integer> value = entry.getValue();
		Integer level = value.get(FettersBM.SubConditionType.LEVEL.type);
		if(level!= null && level > item.getMagicLevel()){
			return false;
		}
		return true;
	}




	/**
	 * 法宝强化进阶通知
	 * @param player
	 * @param itemData
	 */
	public void notifyMagicChange(Player player) {
		
		checkAndAddMagicFetter(player, true);
		notifyListenerAction();
	}




	/**
	 * 英雄变动通知
	 * @param player
	 * @param hero
	 */
	public void notifyHeroChange(Player player, Hero hero) {
		if(hero.isMainRole()){
			checkAndAddMagicFetter(player, false);
		}
		checkOrAddTargetHeroEquipFetter(player, hero, false);
		holder.synAllData(player, 0);
		notifyListenerAction();
	}






	/**
	 * 获取英雄的神器羁绊列表
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
	
	private void notifyListenerAction(){
		for (Action action : actionListener) {
			action.doAction();
		}
	}
}
