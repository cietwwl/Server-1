package com.playerdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.common.IHeroAction;
import com.playerdata.hero.core.FSHeroMgr;
import com.playerdata.readonly.PlayerIF;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.inlay.InlayItem;
import com.rwbase.dao.inlay.InlayItemHelper;
import com.rwbase.dao.inlay.InlayItemHolder;
import com.rwbase.dao.item.pojo.GemCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.role.InlayCfgDAO;
import com.rwbase.dao.role.pojo.InlayCfg;
import com.rwproto.ItemBagProtos.EItemTypeDef;

public class InlayMgr /* extends IDataMgr */{

	private static InlayMgr _instance = new InlayMgr();

	public static InlayMgr getInstance() {
		return _instance;
	}

	protected InlayMgr() {
	}

	private InlayItemHolder inlayItemHolder = InlayItemHolder.getInstance();

	// // private InlayData inlayData;
	// public boolean init(Hero pOwner) {
	// initPlayer(pOwner);
	//
	// inlayItemHolder = new InlayItemHolder(pOwner);
	//
	// return true;
	// }

	public boolean init(Hero pOwner) {
		return true;
	}

	public boolean save(String heroId) {
		// 原来从IDataMgr继承的方法
		/*
		 * 2016-08-19 备注：根据目前的业务逻辑，暂时好像不需要在save做任何东西；原来的方法体也是空实现； 现在的业务逻辑只有addItem和removeItem的业务，这两个逻辑都是直接操作MapItemStore，没有额外需要save的操作 后续可以考虑去掉此方法。
		 */
		return true;
	}

	public void regDataChangeCallback(IHeroAction callback) {
		inlayItemHolder.regDataChangeCallback(callback);
	}

	/**
	 * 
	 * 同步所有的镶嵌数据
	 * 
	 * @param player
	 * @param heroId
	 * @param version
	 */
	public void syncAllInlay(Player player, String heroId, int version) {
		inlayItemHolder.synAllData(player, heroId, version);
	}

	/**
	 * 
	 * 镶嵌宝石
	 * 
	 * @param player 目标角色
	 * @param heroId 目标英雄id
	 * @param itemData 装备
	 * @return
	 */
	public boolean InlayGem(Player player, String heroId, ItemData itemData) {
		int inlaySlot = getSolt(player, heroId);
		if (inlaySlot < 0) {
			return false;
		}
		InlayItem inlayItem = InlayItemHelper.toInlayItem(heroId, itemData, inlaySlot);

		ItemBagMgr.getInstance().useItemByCfgId(player, itemData.getModelId(), 1);
		boolean success = inlayItemHolder.addItem(player, heroId, inlayItem);

		// setAllAttMgr();
		//
		// syncAllBs();

		return success;
	}

	/**
	 * 
	 * 卸下宝石
	 * 
	 * @param player
	 * @param heroId
	 * @param modelId
	 * @return
	 */
	public boolean XieXia(Player player, String heroId, int modelId) {
		boolean stripSuccess = false;

		Hero m_pOwner = FSHeroMgr.getInstance().getHeroById(player, heroId);
		List<InlayItem> itemList = inlayItemHolder.getItemList(heroId);
		if (itemList.size() == 0) {
			return false;
		}
		TreeMap<Integer, Integer> slotMap = new TreeMap<Integer, Integer>();
		for (InlayItem inlayItem : itemList) {
			slotMap.put(inlayItem.getSlotId(), inlayItem.getModelId());
		}
		Map.Entry<Integer, Integer> entry = slotMap.lastEntry();
		if (!InlayItemHelper.isOpen(m_pOwner.getModeId(), entry.getKey(), m_pOwner.getLevel())) {
			modelId = entry.getValue();
		}

		InlayItem targetItem = inlayItemHolder.getItem(heroId, modelId);
		if (targetItem != null) {
			stripSuccess = inlayItemHolder.removeItem(player, targetItem);
			if (stripSuccess) {
				ItemBagMgr.getInstance().addItem(player, modelId, 1);
			}
		}
		//
		// if(stripSuccess)
		// {
		// syncAllBs();
		// setAllAttMgr();
		// }
		//
		return stripSuccess;
	}

	/**
	 * 
	 * 拿下所有宝石
	 * 
	 * @param player
	 * @param heroId
	 * @return
	 */
	public boolean XieXiaAll(Player player, String heroId) {
		List<InlayItem> itemList = inlayItemHolder.getItemList(heroId);
		if (itemList.size() <= 0) {
			return false;
		}
		boolean success = true;
		List<ItemInfo> list = new ArrayList<ItemInfo>(itemList.size());
		for (InlayItem inlayItem : itemList) {
			boolean stripSuccess = inlayItemHolder.removeItem(player, inlayItem);
			if (stripSuccess) {
				// player.getItemBagMgr().addItem(inlayItem.getModelId(), 1);
				list.add(new ItemInfo(inlayItem.getModelId(), 1));
			} else {
				success = false;
				break;
			}
		}
		if (list.size() > 0) {
			ItemBagMgr.getInstance().addItem(player, list);
		}
		return success;
	}

	/**
	 * 
	 * 一键镶嵌宝石
	 * 
	 * @param player
	 * @param heroId
	 * @return
	 */
	public boolean InlayAll(Player player, String heroId) {
		List<ItemData> allGemList = ItemBagMgr.getInstance().getItemListByType(player.getUserId(), EItemTypeDef.Gem);
		if (allGemList.isEmpty()) {
			player.NotifyCommonMsg("没有更多可佩戴的宝石");
			return false;
		}

		// 检查一下当前身上已经穿的宝石类型
		List<InlayItem> hasGemList = inlayItemHolder.getItemList(heroId);

		int hasGemTypeSize = hasGemList.size();
		List<Integer> hasTypeList = new ArrayList<Integer>(hasGemTypeSize);

		for (int i = 0; i < hasGemTypeSize; i++) {
			InlayItem inlayItem = hasGemList.get(i);
			if (inlayItem == null) {
				continue;
			}

			GemCfg gemCfg = ItemCfgHelper.getGemCfg(inlayItem.getModelId());
			if (gemCfg == null) {
				continue;
			}

			hasTypeList.add(gemCfg.getGemType());
		}

		// 检查一下当前身上可以开多少个宝石镶嵌孔
		Hero hero = player.getHeroMgr().getHeroById(player, heroId);
		int heroLevel = hero.getLevel();

		InlayCfg heroInlayCfg = InlayCfgDAO.getInstance().getConfig(String.valueOf(hero.getModeId()));

		List<Integer> inlayPriorityList = null;
		int maxGemInlayLevel = -1;
		int maxGemSize = 6;// 最大可以开放的宝石孔数量
		if (heroInlayCfg != null) {
			// 开放的宝石孔的等级
			List<Integer> openLevelList = heroInlayCfg.getOpenLevelList();

			int openGemSize = 0;
			for (int i = 0, size = openLevelList.size(); i < size; i++) {
				Integer openLevel = openLevelList.get(i);
				if (heroLevel < openLevel) {
					continue;
				}

				openGemSize++;
				if (openLevel > maxGemInlayLevel) {
					maxGemInlayLevel = openLevel;
				}
			}
			maxGemSize = openGemSize;

			// 获取镶嵌宝石的优先级
			inlayPriorityList = heroInlayCfg.getPriorityList();
		}

		// 判断当前镶嵌宝石的数量是否已经打到了宝石孔的上限
		int leftGemSize = maxGemSize - hasGemTypeSize;
		if (leftGemSize <= 0) {
			player.NotifyCommonMsg("没有更多位置可镶嵌");
			return false;
		}

		// 把当前背包里的宝石进行分类
		Map<Integer, ItemData> gemMap = new HashMap<Integer, ItemData>();
		for (int i = 0, size = allGemList.size(); i < size; i++) {
			ItemData gem = allGemList.get(i);
			if (gem == null) {
				continue;
			}

			int modelId = gem.getModelId();
			GemCfg gemCfg = ItemCfgHelper.getGemCfg(modelId);
			if (gemCfg == null) {
				continue;
			}

			int gemType = gemCfg.getGemType();
			if (hasTypeList.contains(gemType)) {
				continue;
			}

			int gemLevel = gemCfg.getGemLevel();
			int gemInlayLevel = gemCfg.getLevel();
			if (gemInlayLevel > heroLevel || gemInlayLevel > maxGemInlayLevel) {
				continue;
			}

			ItemData hasGem = gemMap.get(gemType);
			if (hasGem == null) {
				gemMap.put(gemType, gem);
			} else {
				int hasGemModelId = hasGem.getModelId();
				if (hasGemModelId == modelId) {
					continue;
				}

				GemCfg hasGemCfg = ItemCfgHelper.getGemCfg(hasGemModelId);
				int hasGemLevel = hasGemCfg.getGemLevel();
				if (hasGemLevel > gemLevel) {
					continue;
				}

				gemMap.put(gemType, gem);
			}
		}

		if (gemMap.isEmpty()) {
			player.NotifyCommonMsg("没有更多可佩戴的宝石");
			return false;
		}

		// 按照优先级镶嵌下宝石
		if (inlayPriorityList == null || inlayPriorityList.isEmpty()) {
			for (Entry<Integer, ItemData> e : gemMap.entrySet()) {
				InlayGem(player, heroId, e.getValue());
				if (--leftGemSize <= 0) {
					break;
				}
			}
		} else {
			// 先按照正确的顺序镶嵌宝石
			for (int i = 0, size = inlayPriorityList.size(); i < size; i++) {
				Integer key = inlayPriorityList.get(i);
				ItemData itemData = gemMap.get(key);
				if (itemData == null) {
					continue;
				}

				InlayGem(player, heroId, itemData);
				if (--leftGemSize <= 0) {
					break;
				}

				gemMap.remove(key);
			}

			// 如果还有些宝石，并且优先级列表中已经检测完了，只能把没有放在优先级列表中的宝石随机穿身上了
			if (leftGemSize > 0 && !gemMap.isEmpty()) {
				for (Entry<Integer, ItemData> e : gemMap.entrySet()) {
					InlayGem(player, heroId, e.getValue());
					if (--leftGemSize <= 0) {
						break;
					}
				}
			}
		}

		return true;
	}

	/**
	 * 
	 * 是否可以加入
	 * 
	 * @param player
	 * @param heroId
	 * @return
	 */
	public boolean CheckAddSize(Player player, String heroId) {
		int size = inlayItemHolder.getItemList(heroId).size();

		if (size >= 6) {
			return false;
		}

		return true;
	}

	/**
	 * 
	 * 是否可以加入
	 * 
	 * @param heroId
	 * @param itemId
	 * @return
	 */
	public boolean CheckAddType(String heroId, int itemId) {

		GemCfg gemCfg = ItemCfgHelper.getGemCfg(itemId);
		if (gemCfg == null) {
			return false;
		}

		boolean canAdd = true;
		List<InlayItem> itemList = inlayItemHolder.getItemList(heroId);
		for (InlayItem inlayItem : itemList) {
			GemCfg gemCfgTmp = ItemCfgHelper.getGemCfg(inlayItem.getModelId());
			if (gemCfgTmp != null && gemCfgTmp.getGemType() == gemCfg.getGemType()) {
				canAdd = false;
				break;
			}
		}
		return canAdd;

	}

	/**
	 * 
	 * 得到位置
	 * 
	 * @param player
	 * @param heroId
	 * @return
	 */
	private int getSolt(Player player, String heroId) {

		Hero hero = player.getHeroMgr().getHeroById(player, heroId);
		List<Integer> slotList = new ArrayList<Integer>();
		List<InlayItem> itemList = inlayItemHolder.getItemList(heroId);
		for (InlayItem inlayItem : itemList) {
			slotList.add(inlayItem.getSlotId());
		}

		int targetSlot = -1;

		for (int i = 0; i < 6; i++) {

			if (!slotList.contains(i) && InlayItemHelper.isOpen(hero.getModeId(), i, hero.getLevel())) {
				targetSlot = i;
				break;
			}

		}

		return targetSlot;

	}

	/**
	 * 
	 * 添加机器人的宝石
	 * 
	 * @param player
	 * @param heroId
	 * @param gemList
	 */
	public void addRobotGem(Player player, String heroId, List<Integer> gemList) {
		if (gemList == null || gemList.isEmpty()) {
			return;
		}

		for (int i = 0, size = gemList.size(); i < size; i++) {
			InlayItem item = new InlayItem();
			int templateId = gemList.get(i).intValue();
			item.setId(templateId);
			item.setModelId(templateId);
			item.setOwnerId(heroId);
			item.setSlotId(i);
			inlayItemHolder.addItem(player, heroId, item);
		}
	}

	/**
	 * 
	 * gm命令镶嵌宝石 改方法只能被gm调用
	 * 
	 * @param player
	 * @param heroId
	 * @param itemData
	 */
	public void gmInlayGem(Player player, String heroId, ItemData itemData) {
		int inlaySlot = getSolt(player, heroId);
		if (inlaySlot < 0) {
			return;
		}
		InlayItem inlayItem = InlayItemHelper.toInlayItem(heroId, itemData, inlaySlot);
		inlayItemHolder.addItem(player, heroId, inlayItem);

	}

	/**
	 * 
	 * 获取身上已经镶嵌的宝石模版Id列表
	 * 
	 * @param player
	 * @param heroId
	 * @return
	 */
	public List<String> getInlayGemList(PlayerIF player, String heroId) {
		List<InlayItem> itemList = inlayItemHolder.getItemList(heroId);

		int gemSize = itemList.size();
		List<String> gemList = new ArrayList<String>(gemSize);

		for (int i = 0; i < gemSize; i++) {
			InlayItem item = itemList.get(i);
			if (item == null) {
				continue;
			}

			gemList.add(String.valueOf(item.getModelId()));
		}

		return gemList;
	}
	
	/**
	 * 获取身上已经镶嵌的宝石列表,如果为null，表示当前位置没有宝石
	 * @param heroID
	 * @return
	 */
	public List<InlayItem> getInlayGenList(String heroID){
		return inlayItemHolder.getItemList(heroID);
	}
	
}