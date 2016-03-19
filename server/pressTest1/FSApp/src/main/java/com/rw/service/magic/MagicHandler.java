package com.rw.service.magic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.Weight;
import com.google.protobuf.ByteString;
import com.playerdata.ItemBagMgr;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.item.ConsumeCfgDAO;
import com.rwbase.dao.item.MagicCfgDAO;
import com.rwbase.dao.item.pojo.ConsumeCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.MagicCfg;
import com.rwbase.dao.item.pojo.itembase.INewItem;
import com.rwbase.dao.item.pojo.itembase.IUseItem;
import com.rwbase.dao.item.pojo.itembase.NewItem;
import com.rwbase.dao.item.pojo.itembase.UseItem;
import com.rwbase.dao.magicweapon.MagicExpCfgDAO;
import com.rwbase.dao.magicweapon.MagicSmeltCfgDAO;
import com.rwbase.dao.magicweapon.pojo.MagicExpCfg;
import com.rwbase.dao.magicweapon.pojo.MagicSmeltCfg;
import com.rwproto.ItemBagProtos.EItemAttributeType;
import com.rwproto.MagicServiceProtos.MagicItemData;
import com.rwproto.MagicServiceProtos.MsgMagicRequest;
import com.rwproto.MagicServiceProtos.MsgMagicResponse;
import com.rwproto.MagicServiceProtos.eMagicResultType;

public class MagicHandler {

	private static MagicHandler instance;
	/** 法宝等级对应的经验 */
	private static Comparator<MagicExpCfg> magicExpComparator = new Comparator<MagicExpCfg>() {
		public int compare(MagicExpCfg o1, MagicExpCfg o2) {
			int l1 = o1.getLevel();
			int l2 = o2.getLevel();
			return l1 - l2;
			// if (o1.getLevel() < o2.getLevel())
			// return -1;
			// if (o1.getLevel() > o2.getLevel())
			// return 1;
			// return 0;
		}
	};

	private MagicHandler() {
	}

	public static MagicHandler getInstance() {
		if (instance == null) {
			instance = new MagicHandler();
		}
		return instance;
	}

	public ByteString wearMagicWeapon(Player player, MsgMagicRequest msgMagicRequest) {
		MsgMagicResponse.Builder msgMagicResponse = MsgMagicResponse.newBuilder();
		msgMagicResponse.setMagicType(msgMagicRequest.getMagicType());
		String magicWeaponSlotId = msgMagicRequest.getId();

		if (!player.getMagicMgr().wearMagic(magicWeaponSlotId)) {
			msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
			return msgMagicResponse.build().toByteString();
		}

		msgMagicResponse.setEMagicResultType(eMagicResultType.SUCCESS);
		return msgMagicResponse.build().toByteString();
	}

	/**
	 * 强化法宝
	 * 
	 * @param player
	 * @param msgMagicRequest
	 * @return
	 */
	public ByteString forgeMagicWeapon(Player player, MsgMagicRequest msgMagicRequest) {
		MsgMagicResponse.Builder msgMagicResponse = MsgMagicResponse.newBuilder();
		msgMagicResponse.setMagicType(msgMagicRequest.getMagicType());
		final int state = msgMagicRequest.getState();

		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		ItemData itemData = itemBagMgr.findBySlotId(msgMagicRequest.getId());
		if (itemData == null) {
			msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
			return msgMagicResponse.build().toByteString();
		}

		List<MagicItemData> list = msgMagicRequest.getMagicItemDataList();
		if (list.isEmpty()) {
			msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
			return msgMagicResponse.build().toByteString();
		}

		// 法宝模版
		MagicCfg magicCfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(String.valueOf(itemData.getModelId()));
		if (magicCfg == null) {
			msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
			return msgMagicResponse.build().toByteString();
		}

		// 看看有没有材料的资源Id
		String[] trainItemIdArr = magicCfg.getTrainItemId().split(",");
		if (trainItemIdArr.length <= 0) {
			msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
			return msgMagicResponse.build().toByteString();
		}

		List<MagicExpCfg> listCfg = MagicExpCfgDAO.getInstance().getAllCfg();
		Collections.sort(listCfg, magicExpComparator);

		// int newLevel = Integer.parseInt(itemData.getExtendAttr(EItemAttributeType.Magic_Level_VALUE));// 当前的等级
		int oldLevel = Integer.parseInt(itemData.getExtendAttr(EItemAttributeType.Magic_Level_VALUE));// 旧等级
		int oldExp = Integer.parseInt(itemData.getExtendAttr(EItemAttributeType.Magic_Exp_VALUE));
		MagicExpCfg curExpCfg = listCfg.get(oldLevel - 1);

		if (oldLevel > player.getLevel()) {
			msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
			return msgMagicResponse.build().toByteString();
		} else if (oldLevel == player.getLevel()) {// 如果等级相同
			if (oldExp >= curExpCfg.getExp()) {
				msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
				return msgMagicResponse.build().toByteString();
			}
		}

		// 要使用的物品
		List<IUseItem> useItemList = new ArrayList<IUseItem>();
		int addExp = 0;
		for (MagicItemData item : list) {
			ItemData magicMaterial = itemBagMgr.findBySlotId(item.getId());
			if (magicMaterial == null) {
				msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
				return msgMagicResponse.build().toByteString();
			}

			ConsumeCfg cfg = (ConsumeCfg) ConsumeCfgDAO.getInstance().getCfgById(String.valueOf(magicMaterial.getModelId()));
			if (cfg == null) {
				msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
				return msgMagicResponse.build().toByteString();
			}

			addExp += cfg.getMagicForgeExp() * item.getCount();

			IUseItem useItem = new UseItem(item.getId(), item.getCount());
			useItemList.add(useItem);
		}

		if (addExp <= 0) {
			msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
			return msgMagicResponse.build().toByteString();
		}

		// 循环检查数据
		int newExp = oldExp;
		int newLevel = oldLevel;
		while (addExp > 0) {
			int maxExp = curExpCfg.getExp();
			if (newExp + addExp >= maxExp) {// 超过了
				curExpCfg = listCfg.get(newLevel);// 下一级
				if (curExpCfg == null) {// 已经是最大等级了
					newExp = maxExp;
					addExp = 0;
					break;
				}

				if (newLevel + 1 > player.getLevel()) {// 超过了角色等级
					newExp = maxExp;
					addExp = 0;
					break;
				}

				newLevel++;
				addExp -= (maxExp - newExp);// 剩下的经验
				newExp = 0;
			} else {
				newExp += addExp;
				addExp -= addExp;
			}
		}

		// int newExp = Integer.parseInt(itemData.getExtendAttr(EItemAttributeType.Magic_Exp_VALUE)) + addExp;// 当前增加上的总经验
		//
		// int totalExp = 0;
		// MagicExpCfg cfg1, cfg2;
		// cfg1 = listCfg.get(newLevel - 1);// 原本的模版
		// int length = listCfg.size();
		// for (int i = newLevel - 1; i < length; i++) {
		// cfg1 = listCfg.get(i);
		// if (i + 1 < length) {
		// cfg2 = listCfg.get(i + 1);
		// totalExp += cfg1.getExp();
		// } else {
		// cfg2 = null;
		// }
		// newLevel = cfg1.getLevel();
		// if (newExp < totalExp) {
		// break;
		// }
		// if (cfg2 != null) {
		// if (newExp >= totalExp && newExp < cfg2.getExp() + totalExp) {
		// newLevel = cfg2.getLevel();
		// newExp = newExp - totalExp;
		// break;
		// }
		// }
		// }
		//
		// if (newLevel == player.getLevel()) {
		// cfg1 = listCfg.get(newLevel - 1);
		// if (newExp > cfg1.getExp()) {
		// newExp = cfg1.getExp();
		// }
		// } else if (newLevel > player.getLevel()) {
		// newLevel = player.getLevel();
		// cfg1 = listCfg.get(newLevel - 1);
		// if (newExp > 0) {
		// newExp = cfg1.getExp();
		// }
		// }

		if (!itemBagMgr.useLikeBoxItem(useItemList, null)) {
			msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
			return msgMagicResponse.build().toByteString();
		}

		itemData.setExtendAttr(EItemAttributeType.Magic_Exp_VALUE, "" + newExp);
		itemData.setExtendAttr(EItemAttributeType.Magic_Level_VALUE, "" + newLevel);

		if (state == 1) {
			player.getMagicMgr().updateMagic();
		} 
//		else {
			// 刷新数据
		itemBagMgr.updateItem(itemData);
		List<ItemData> updateItems = new ArrayList<ItemData>(1);
		updateItems.add(itemData);
		itemBagMgr.syncItemData(updateItems);
//		}

		// if (oldLevel < newLevel) {
		// player.getAttrMgr().refreshTotoalMagicLevel();
		// player.getAttrMgr().CalcFighting();
		// }
		//player.getFresherActivityMgr().doCheck(eActivityType.A_MagicLv);
		msgMagicResponse.setEMagicResultType(eMagicResultType.SUCCESS);
		return msgMagicResponse.build().toByteString();
	}

	// public class MagicCfgQualityComparator implements Comparator<MagicCfg> {
	// public int compare(MagicCfg o1, MagicCfg o2) {
	// if (o1.getId() < o2.getId())
	// return -1;
	// if (o1.getId() > o2.getId())
	// return 1;
	// return 0;
	// }
	// }

	/**
	 * 熔炼法宝
	 * 
	 * @param player
	 * @param msgMagicRequest
	 * @return
	 */
	public ByteString smeltMagicWeapon(Player player, MsgMagicRequest msgMagicRequest) {
		MsgMagicResponse.Builder msgMagicResponse = MsgMagicResponse.newBuilder();
		msgMagicResponse.setMagicType(msgMagicRequest.getMagicType());
		List<MagicItemData> list = msgMagicRequest.getMagicItemDataList();

		int size = list.size();

		// 检查是否有重复的Id
		List<IUseItem> useItemList = new ArrayList<IUseItem>(list.size());// 使用的物品
		List<INewItem> newItemList = new ArrayList<INewItem>();// 新创建的物品

		int materialQuality = 0;
		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		for (int i = 0; i < size; i++) {
			MagicItemData data = list.get(i);
			ItemData itemData = itemBagMgr.findBySlotId(data.getId());
			if (itemData == null) {
				msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
				return msgMagicResponse.build().toByteString();
			}

			if ("1".equalsIgnoreCase(itemData.getExtendAttr(EItemAttributeType.Magic_State_VALUE))) {
				msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
				return msgMagicResponse.build().toByteString();
			}

			MagicCfg cfg = ItemCfgHelper.getMagicCfg(itemData.getModelId());
			if (cfg == null) {
				msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
				return msgMagicResponse.build().toByteString();
			}

			int quality = cfg.getQuality();
			if (i == 0) {
				materialQuality = quality;
			} else if (materialQuality != quality) {// 品质不相同
				msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
				return msgMagicResponse.build().toByteString();
			}

			// 数量不能小于0
			if (data.getCount() <= 0) {
				msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
				return msgMagicResponse.build().toByteString();
			}

			// 超出拥有的数量
			if (data.getCount() > itemData.getCount()) {
				msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
				return msgMagicResponse.build().toByteString();
			}

			IUseItem useItem = new UseItem(data.getId(), data.getCount());
			useItemList.add(useItem);
		}

		int quality = materialQuality + 1;// 熔炼要产生新的法宝品质
		MagicSmeltCfg smeltCfg = (MagicSmeltCfg) MagicSmeltCfgDAO.getInstance().getCfgById(String.valueOf(quality));
		if (smeltCfg == null) {
			msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
			return msgMagicResponse.build().toByteString();
		}

		// 需求材料的个数
		if (size < smeltCfg.getNum() || size <= 0) {
			msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
			return msgMagicResponse.build().toByteString();
		}

		// cost money
		long playerCoin = player.getUserGameDataMgr().getCoin();
		int cost = smeltCfg.getCost();
		if (playerCoin < cost) {
			msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
			return msgMagicResponse.build().toByteString();
		}

		// 概率Map
		Map<Integer, Integer> proMap = new HashMap<Integer, Integer>();
		List<MagicCfg> listCfg = MagicCfgDAO.getInstance().getAllCfg();
		for (MagicCfg cfg : listCfg) {
			if (cfg.getQuality() == quality) {
				proMap.put(cfg.getId(), cfg.getSmeltperc());
			}
		}

		Weight<Integer> weightMap = new Weight<Integer>(proMap);// 权重Map
		Integer ranResult = weightMap.getRanResult();
		if (ranResult == null) {
			msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
			return msgMagicResponse.build().toByteString();
		}

		int resultId = ranResult.intValue();
		INewItem newItem = new NewItem(resultId, 1, null);
		newItemList.add(newItem);

		// 扣钱
		if (player.getUserGameDataMgr().addCoin(-cost) == -1) {
			msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
			return msgMagicResponse.build().toByteString();
		}

		// 消耗物品
		boolean success = itemBagMgr.useLikeBoxItem(useItemList, newItemList);
		if (!success) {
			msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
			return msgMagicResponse.build().toByteString();
		}

		msgMagicResponse.setNewMagicModelId(resultId);
		msgMagicResponse.setEMagicResultType(eMagicResultType.SUCCESS);
		return msgMagicResponse.build().toByteString();
	}
}