package com.playerdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.common.IHeroAction;
import com.log.GameLog;
import com.playerdata.hero.core.FSHeroMgr;
import com.playerdata.readonly.PlayerIF;
import com.rwbase.dao.inlay.InlayItem;
import com.rwbase.dao.inlay.InlayItemHelper;
import com.rwbase.dao.inlay.InlayItemHolder;
import com.rwbase.dao.item.pojo.GemCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.role.InlayCfgDAO;
import com.rwbase.dao.role.pojo.InlayCfg;

public class InlayMgr /*extends IDataMgr*/ {

	private static final InlayMgr _INSTANCE = new InlayMgr();
	
	public static InlayMgr getInstance() {
		return _INSTANCE;
	}
	
	protected InlayMgr() {}
	
	private InlayItemHolder inlayItemHolder = InlayItemHolder.getInstance();
	

//	// private InlayData inlayData;
//	public boolean init(Hero pOwner) {
//		initPlayer(pOwner);
//
//		inlayItemHolder = new InlayItemHolder(pOwner);
//
//		return true;
//	}
	
	public boolean init(Hero pOwner) {
		return true;
	}
	
	public boolean save(String heroId) {
		// 原来从IDataMgr继承的方法
		/* 2016-08-19 备注：根据目前的业务逻辑，暂时好像不需要在save做任何东西；原来的方法体也是空实现；
		 * 现在的业务逻辑只有addItem和removeItem的业务，这两个逻辑都是直接操作MapItemStore，没有额外需要save的操作
		 * 后续可以考虑去掉此方法。 
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

		player.getItemBagMgr().useItemByCfgId(itemData.getModelId(), 1);
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
		if(itemList.size() == 0){
			return false;
		}
		TreeMap<Integer, Integer> slotMap = new TreeMap<Integer, Integer>();
		for (InlayItem inlayItem : itemList) {
			slotMap.put(inlayItem.getSlotId(), inlayItem.getModelId());
		}
		Map.Entry<Integer, Integer> entry = slotMap.lastEntry();
		if(!InlayItemHelper.isOpen(m_pOwner.getModeId(), entry.getKey(), m_pOwner.getLevel())){
			modelId = entry.getValue();
		}
		
		InlayItem targetItem = inlayItemHolder.getItem(heroId, modelId);
		if (targetItem != null) {
			stripSuccess = inlayItemHolder.removeItem(player, targetItem);
			if (stripSuccess) {
				player.getItemBagMgr().addItem(modelId, 1);
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
		for (InlayItem inlayItem : itemList) {
			boolean stripSuccess = inlayItemHolder.removeItem(player, inlayItem);
			if (stripSuccess) {
				player.getItemBagMgr().addItem(inlayItem.getModelId(), 1);
			} else {
				success = false;
				break;
			}
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
		Hero ownerHero = player.getHeroMgr().getHeroById(player, heroId);
		
		boolean isT = false;

		Map<Integer, GemCfg> tempMap = new HashMap<Integer, GemCfg>();

		for (int i = 1; i < 100; i++) {
			List<ItemData> list = player.getItemBagMgr().getItemListByCfgId(800000 + i);
			if (list != null && list.size() > 0) {
				GemCfg gemCfg = ItemCfgHelper.getGemCfg(list.get(0).getModelId());
				if (gemCfg != null && ownerHero.getLevel() >= gemCfg.getLevel()) {
					tempMap.put(gemCfg.getGemType(), gemCfg);
				}

			}

		}

		List<InlayItem> itemList = inlayItemHolder.getItemList(ownerHero.getUUId());
		for (InlayItem inlayItem : itemList) {
			GemCfg gemCfg = ItemCfgHelper.getGemCfg(inlayItem.getModelId());
			if (gemCfg != null) {
				if (tempMap.containsKey(gemCfg.getGemType())) {
					tempMap.remove(gemCfg.getGemType());
				}
			}

		}

		InlayCfg heroInlayCfg = InlayCfgDAO.getInstance().getConfig(String.valueOf(ownerHero.getModeId()));
		ArrayList<Integer> priorList = new ArrayList<Integer>();
		if (heroInlayCfg != null) {
			String[] array = heroInlayCfg.getPrior().split(",");
			for (int i = 0; i < array.length; i++) {
				try {
					priorList.add(Integer.parseInt(array[i]));
				} catch (NumberFormatException e) {
					GameLog.error("InlayMgr", "#InlayAll()", "一键宝石转换优先列表异常：" + player.getUserId() + "," + ownerHero.getModeId(), e);
				}
			}
		}
		for (int typeId : priorList) {
			if (tempMap.containsKey(typeId)) {
				GemCfg gemCfg = tempMap.get(typeId);

				List<ItemData> list = player.getItemBagMgr().getItemListByCfgId(gemCfg.getId());

				if (InlayGem(player, ownerHero.getUUId(), list.get(0))) {
					isT = true;
				} else {
					if (!isT) {
						player.NotifyCommonMsg("没有更多位置可佩戴");
					}
					return isT;
				}
			}

		}

		if (!isT) {
			player.NotifyCommonMsg("没有更多可佩戴的宝石");
			return false;
		}

		//
		// this.setAllAttMgr();
		// this.syncAllBs();

		return isT;
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
			item.setId(InlayItemHelper.getItemId(heroId, templateId));
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
}