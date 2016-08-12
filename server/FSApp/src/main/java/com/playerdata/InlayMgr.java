package com.playerdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.common.Action;
import com.log.GameLog;
import com.rwbase.dao.inlay.InlayItem;
import com.rwbase.dao.inlay.InlayItemHelper;
import com.rwbase.dao.inlay.InlayItemHolder;
import com.rwbase.dao.item.pojo.GemCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.role.InlayCfgDAO;
import com.rwbase.dao.role.pojo.InlayCfg;

public class InlayMgr extends IDataMgr {

	private InlayItemHolder inlayItemHolder;

	// private InlayData inlayData;
	public boolean init(Hero pOwner) {
		initPlayer(pOwner);

		inlayItemHolder = new InlayItemHolder(pOwner);

		return true;
	}

	public void regChangeCallBack(Action callBack) {
		inlayItemHolder.regChangeCallBack(callBack);
	}

	public void syncAllInlay(int version) {
		inlayItemHolder.synAllData(m_pPlayer, version);

	}

	// public AttrData getTotalInlayAttrData() {
	// return inlayItemHolder.toAttrData();
	// }

	// public AttrData getTotalInlayPercentAttrData() {
	// return inlayItemHolder.toPercentAttrData();
	// }

	/**
	 * 镶嵌宝石
	 * 
	 * @param equipSolt 装备
	 * @param gem 宝石数据
	 */
	public boolean InlayGem(ItemData itemData) {
		int inlaySlot = getSolt();
		if (inlaySlot < 0) {
			return false;
		}
		String ownerId = m_pOwner.getUUId();
		InlayItem inlayItem = InlayItemHelper.toInlayItem(ownerId, itemData, inlaySlot);

		m_pPlayer.getItemBagMgr().useItemByCfgId(itemData.getModelId(), 1);
		boolean success = inlayItemHolder.addItem(m_pPlayer, inlayItem);

		// setAllAttMgr();
		//
		// syncAllBs();

		return success;
	}

	/**
	 * 拿下镶嵌宝石
	 * 
	 * 
	 */
	public boolean XieXia(int modelId) {
		boolean stripSuccess = false;

		List<InlayItem> itemList = inlayItemHolder.getItemList();
		if(itemList.size() == 0){
			return false;
		}
		TreeMap<Integer, Integer> slotMap = new TreeMap<Integer, Integer>();
		for (InlayItem inlayItem : itemList) {
			slotMap.put(inlayItem.getSlotId(), inlayItem.getModelId());
		}
		Map.Entry<Integer, Integer> entry = slotMap.lastEntry();
		if(!InlayItemHelper.isOpen(m_pOwner.getModelId(), entry.getKey(), m_pOwner.getLevel())){
			modelId = entry.getValue();
		}
		
		String ownerId = m_pOwner.getUUId();
		InlayItem targetItem = inlayItemHolder.getItem(ownerId, modelId);
		if (targetItem != null) {
			stripSuccess = inlayItemHolder.removeItem(m_pPlayer, targetItem);
			if (stripSuccess) {
				m_pPlayer.getItemBagMgr().addItem(modelId, 1);
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
	 * 拿下所有镶嵌宝石
	 * 
	 * 
	 */
	public boolean XieXiaAll() {
		List<InlayItem> itemList = inlayItemHolder.getItemList();
		if (itemList.size() <= 0)
			return false;
		boolean success = true;
		for (InlayItem inlayItem : itemList) {
			boolean stripSuccess = inlayItemHolder.removeItem(m_pPlayer, inlayItem);
			if (stripSuccess) {
				m_pPlayer.getItemBagMgr().addItem(inlayItem.getModelId(), 1);
			} else {
				success = false;
				break;
			}
		}
		return success;

	}

	/**
	 * 一键镶嵌宝石
	 */
	public boolean InlayAll() {
		boolean isT = false;

		Map<Integer, GemCfg> tempMap = new HashMap<Integer, GemCfg>();

		for (int i = 1; i < 100; i++) {
			List<ItemData> list = m_pPlayer.getItemBagMgr().getItemListByCfgId(800000 + i);
			if (list != null && list.size() > 0) {
				GemCfg gemCfg = ItemCfgHelper.getGemCfg(list.get(0).getModelId());
				if (gemCfg != null && m_pOwner.getLevel() >= gemCfg.getLevel()) {
					tempMap.put(gemCfg.getGemType(), gemCfg);
				}

			}

		}

		List<InlayItem> itemList = inlayItemHolder.getItemList();
		for (InlayItem inlayItem : itemList) {
			GemCfg gemCfg = ItemCfgHelper.getGemCfg(inlayItem.getModelId());
			if (gemCfg != null) {
				if (tempMap.containsKey(gemCfg.getGemType())) {
					tempMap.remove(gemCfg.getGemType());
				}
			}

		}

		InlayCfg heroInlayCfg = InlayCfgDAO.getInstance().getConfig(String.valueOf(super.m_pOwner.getModelId()));
		ArrayList<Integer> priorList = new ArrayList<Integer>();
		if (heroInlayCfg != null) {
			String[] array = heroInlayCfg.getPrior().split(",");
			for (int i = 0; i < array.length; i++) {
				try {
					priorList.add(Integer.parseInt(array[i]));
				} catch (NumberFormatException e) {
					GameLog.error("InlayMgr", "#InlayAll()", "一键宝石转换优先列表异常：" + m_pPlayer.getUserId() + "," + super.m_pOwner.getModelId(), e);
				}
			}
		}
		for (int typeId : priorList) {
			if (tempMap.containsKey(typeId)) {
				GemCfg gemCfg = tempMap.get(typeId);

				List<ItemData> list = m_pPlayer.getItemBagMgr().getItemListByCfgId(gemCfg.getId());

				if (InlayGem(list.get(0))) {
					isT = true;
				} else {
					if (!isT) {
						m_pPlayer.NotifyCommonMsg("没有更多位置可佩戴");
					}
					return isT;
				}
			}

		}

		if (!isT) {
			m_pPlayer.NotifyCommonMsg("没有更多可佩戴的宝石");
			return false;
		}

		//
		// this.setAllAttMgr();
		// this.syncAllBs();

		return isT;
	}

	/*** 是否可加入 ***/
	public boolean CheckAddSize() {
		int size = inlayItemHolder.getItemList().size();

		if (size >= 6) {
			return false;
		}

		return true;

	}

	/*** 是否可加入 ***/
	public boolean CheckAddType(int itemId) {

		GemCfg gemCfg = ItemCfgHelper.getGemCfg(itemId);
		if (gemCfg == null) {
			return false;
		}

		boolean canAdd = true;
		List<InlayItem> itemList = inlayItemHolder.getItemList();
		for (InlayItem inlayItem : itemList) {
			GemCfg gemCfgTmp = ItemCfgHelper.getGemCfg(inlayItem.getModelId());
			if (gemCfgTmp != null && gemCfgTmp.getGemType() == gemCfg.getGemType()) {
				canAdd = false;
				break;
			}
		}
		return canAdd;

	}

	/*** 得到位置 ***/
	private int getSolt() {

		List<Integer> slotList = new ArrayList<Integer>();
		List<InlayItem> itemList = inlayItemHolder.getItemList();
		for (InlayItem inlayItem : itemList) {
			slotList.add(inlayItem.getSlotId());
		}

		int targetSlot = -1;

		for (int i = 0; i < 6; i++) {

			if (!slotList.contains(i) && InlayItemHelper.isOpen(m_pOwner.getModelId(), i, m_pOwner.getLevel())) {
				targetSlot = i;
				break;
			}

		}

		return targetSlot;

	}

	//
	// public void syncAllBs() {
	// String bsSt="";
	// for (int i=1;i<7;i++)
	// {
	// int solt=0;
	// if(!inlayData.getInlayMap().containsKey(i))
	// {
	// inlayData.getInlayMap().put(i, 0);
	// }
	// solt=inlayData.getInlayMap().get(i);
	//
	// if(i==1)
	// {
	// bsSt=solt+"";
	// }else
	// {
	// bsSt+="_"+solt;
	// }
	//
	//
	// }
	// m_pOwner.SetCommonAttrString(eAttrIdDef.ROLE_INLAY, bsSt);
	//
	// }

	/**
	 * 添加机器人的宝石
	 * 
	 * @param gemList
	 */
	public void addRobotGem(List<Integer> gemList) {
		if (gemList == null || gemList.isEmpty()) {
			return;
		}

		String heroId = m_pOwner.getUUId();
		for (int i = 0, size = gemList.size(); i < size; i++) {
			InlayItem item = new InlayItem();
			int templateId = gemList.get(i).intValue();
			item.setId(InlayItemHelper.getItemId(heroId, templateId));
			item.setModelId(templateId);
			item.setOwnerId(heroId);
			item.setSlotId(i);
			inlayItemHolder.addItem(this.m_pPlayer, item);
		}
	}

	/**
	 * gm命令镶嵌宝石 改方法只能被gm调用
	 * 
	 * @param itemData
	 */
	public void gmInlayGem(ItemData itemData) {
		int inlaySlot = getSolt();
		if (inlaySlot < 0) {
			return;
		}
		String ownerId = m_pOwner.getUUId();
		InlayItem inlayItem = InlayItemHelper.toInlayItem(ownerId, itemData, inlaySlot);
		inlayItemHolder.addItem(m_pPlayer, inlayItem);
	}

	/**
	 * 获取身上已经镶嵌的宝石模版Id列表
	 * 
	 * @return
	 */
	public List<String> getInlayGemList() {
		List<InlayItem> itemList = inlayItemHolder.getItemList();

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