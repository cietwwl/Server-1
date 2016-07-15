package com.rw.service.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.ItemBagMgr;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.rw.fsutil.common.Pair;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.item.useeffect.IItemUseEffect;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.item.ComposeCfgDAO;
import com.rwbase.dao.item.ItemUseEffectCfgDAO;
import com.rwbase.dao.item.MagicCfgDAO;
import com.rwbase.dao.item.SpecialItemCfgDAO;
import com.rwbase.dao.item.pojo.ComposeCfg;
import com.rwbase.dao.item.pojo.ConsumeCfg;
import com.rwbase.dao.item.pojo.ItemBaseCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.ItemUseEffectTemplate;
import com.rwbase.dao.item.pojo.MagicCfg;
import com.rwbase.dao.item.pojo.SpecialItemCfg;
import com.rwbase.dao.item.pojo.itembase.IUseItem;
import com.rwbase.dao.item.pojo.itembase.UseItem;
import com.rwproto.ItemBagProtos.BuyItemInfo;
import com.rwproto.ItemBagProtos.ConsumeTypeDef;
import com.rwproto.ItemBagProtos.EItemAttributeType;
import com.rwproto.ItemBagProtos.EItemBagEventType;
import com.rwproto.ItemBagProtos.EItemTypeDef;
import com.rwproto.ItemBagProtos.MsgItemBagResponse;
import com.rwproto.ItemBagProtos.ResponseInfo;
import com.rwproto.ItemBagProtos.TagCompose;
import com.rwproto.ItemBagProtos.TagItemData;
import com.rwproto.ItemBagProtos.UseItemInfo;
import com.rwproto.MsgDef.Command;

public class ItemBagHandler {

	private static final int MaxBuyCountLimit = 99;
	private static ItemBagHandler m_instance = null;

	public static ItemBagHandler getInstance() {
		if (m_instance == null) {
			m_instance = new ItemBagHandler();
		}
		return m_instance;
	}

	// public void PlayerOnLogin(Player player) {
	// player.onLogin();
	// }

	public ByteString sellItemItemData(Player player, List<TagItemData> sellItemList) {
		MsgItemBagResponse.Builder response = MsgItemBagResponse.newBuilder();
		response.setEventType(EItemBagEventType.ItemBag_Sell);

		if (sellItemList == null || sellItemList.isEmpty()) {
			response.setRspInfo(fillResponseInfo(false, "出售物品不能为空"));
			return response.build().toByteString();
		}

		List<IUseItem> useList = new ArrayList<IUseItem>();
		List<String> idList = new ArrayList<String>();

		int totalSellCoin = 0;// 总共出售可以获得的价格
		for (int i = 0, size = sellItemList.size(); i < size; i++) {
			TagItemData data = sellItemList.get(i);
			String dbId = data.getDbId();
			if (idList.contains(dbId)) {
				response.setRspInfo(fillResponseInfo(false, "同一物品不能多次出售"));
				return response.build().toByteString();
			}

			ItemData itemData = player.getItemBagMgr().findBySlotId(dbId);
			if (itemData == null) {
				response.setRspInfo(fillResponseInfo(false, "道具不存在"));
				return response.build().toByteString();
			}

			int templateId = itemData.getModelId();
			ItemBaseCfg baseCfg = ItemCfgHelper.GetConfig(templateId);
			if (baseCfg == null) {
				response.setRspInfo(fillResponseInfo(false, "道具模版不存在"));
				return response.build().toByteString();
			}

			int count = Math.abs(data.getCount());

			totalSellCoin += baseCfg.getSellPrice() * count;

			IUseItem use = new UseItem(dbId, count);
			useList.add(use);
		}

		boolean success = player.getItemBagMgr().useLikeBoxItem(useList, null);
		if (success) {
			player.getUserGameDataMgr().addCoin(totalSellCoin);
		} else {
			GameLog.error("背包模块", player.getUserId(), "出售的过程当中出现了错误，导致出售失败", null);
			response.setRspInfo(fillResponseInfo(false, "道具出售失败"));
		}
		return response.build().toByteString();
	}

	// public ByteString SellItemItemData(Player player, int nItemId, int nCount, String nSlotId) {
	// MsgItemBagResponse.Builder response = MsgItemBagResponse.newBuilder();
	// response.setEventType(EItemBagEventType.ItemBag_Sell);
	//
	// if (player.getItemBagMgr().findBySlotId(nSlotId) == null)
	// return response.build().toByteString();
	// if (player.getItemBagMgr().getItemCountByModelId(nItemId) < nCount)
	// return response.build().toByteString();
	//
	// if (nCount < 0) {// 小于0不能通过
	// return response.build().toByteString();
	// }
	//
	// EItemTypeDef itemType = ItemCfgHelper.getItemType(nItemId);
	// player.getItemBagMgr().useItemBySlotId(nSlotId, nCount);
	// switch (itemType) {
	// case HeroEquip: {
	// // player.getItemBagMgr().subItemBySlotId(nSlotId, nCount);
	// HeroEquipCfg heroEquipCfg = ItemCfgHelper.getHeroEquipCfg(nItemId);
	// int gold = heroEquipCfg.getSellPrice() * nCount;
	// player.getUserGameDataMgr().addCoin(gold);
	// break;
	// }
	// case Fashion: {
	// break;
	// }
	//
	// case Piece: {
	// // player.getItemBagMgr().subItemBySlotId(nSlotId, nCount);
	// PieceCfg pieceCfg = ItemCfgHelper.getPieceCfg(nItemId);
	// int gold = pieceCfg.getSellPrice() * nCount;
	// player.getUserGameDataMgr().addCoin(gold);
	// break;
	// }
	// case Magic_Piece:
	// case Magic: {
	// // player.getItemBagMgr().subItemBySlotId(nSlotId, nCount);
	// MagicCfg magicCfg = ItemCfgHelper.getMagicCfg(nItemId);
	// int gold = magicCfg.getSellPrice() * nCount;
	// player.getUserGameDataMgr().addCoin(gold);
	// break;
	// }
	//
	// case Gem: {
	// // player.getItemBagMgr().subItemBySlotId(nSlotId, nCount);
	// GemCfg gemCfg = ItemCfgHelper.getGemCfg(nItemId);
	// int gold = gemCfg.getSellPrice() * nCount;
	// player.getUserGameDataMgr().addCoin(gold);
	// break;
	// }
	// case Consume: {
	// // player.getItemBagMgr().subItemBySlotId(nSlotId, nCount);
	// ConsumeCfg consumeCfg = ItemCfgHelper.getConsumeCfg(nItemId);
	// int gold = consumeCfg.getSellPrice() * nCount;
	// player.getUserGameDataMgr().addCoin(gold);
	// break;
	// }
	// case SoulStone: {
	// // player.getItemBagMgr().subItemBySlotId(nSlotId, nCount);
	// SoulStoneCfg soulStoneCfg = ItemCfgHelper.getSoulStoneCfg(nItemId);
	// int gold = soulStoneCfg.getSellPrice() * nCount;
	// player.getUserGameDataMgr().addCoin(gold);
	// break;
	// }
	// }
	//
	// return response.build().toByteString();
	// }

	// public void ComposeItem(Player player, int nItemId, int nSlotId, int count) {
	// ItemData data = player.getItemBagMgr().findBySlotId(nSlotId);
	// if (data == null)
	// return;
	// eItemTypeDef def = ItemCfgHelper.getItemType(nItemId);
	// int cost = 0;
	// int needCount = 0;
	// int composeId = 0;
	// switch (def) {
	// case Piece:
	// PieceCfg pieceCfg = ItemCfgHelper.getPieceCfg(nItemId);
	// cost = pieceCfg.getComposeCostCoin();
	// needCount = pieceCfg.getComposeNeedNum() * count;
	// composeId = pieceCfg.getComposeItemID();
	// break;
	// case Gem:
	// GemCfg gemCfg = ItemCfgHelper.getGemCfg(nItemId);
	// cost = gemCfg.getComposeCost();
	// needCount = gemCfg.getComposeNeedNum() * count;
	// composeId = gemCfg.getComposeItemID();
	// break;
	// case Magic_Piece:
	// MagicCfg magicCfg = ItemCfgHelper.getMagicCfg(nItemId);
	// cost = magicCfg.getComposeCostCoin();
	// needCount = magicCfg.getComposeNeedNum() * count;
	// composeId = magicCfg.getComposeItemID();
	// break;
	// default:
	// break;
	// }
	//
	// if (data.getCount() < needCount)
	// return;
	// if (player.getCoin() < cost)
	// return;
	// // player.getItemBagMgr().subItemBySlotId(nSlotId, needCount);
	// // TODO @modify 2015-08-10 HC 修改物品数量，调用方法修改为useItemBySlotId
	// player.getItemBagMgr().useItemBySlotId(nSlotId, needCount);
	// player.addCoin(-cost);
	// player.getItemBagMgr().addItem(composeId, count);
	//
	// MsgItemBagResponse.Builder response = MsgItemBagResponse.newBuilder();
	// response.setEventType(EItemBagEventType.ItemBag_Compose);
	// player.SendMsg(Command.MSG_ItemBag, response.build().toByteString());
	// }

	/**
	 * 合成物品
	 * 
	 * @modify 2015-08-25 HC 修改了各个不通过条件，直接return，而不发送响应消息的错误
	 * 
	 * @param player
	 * @param mateId
	 * @param composeCount
	 */
	public void ComposeItem(Player player, int mateId, int composeCount) {
		// ItemData data = player.getItemBagMgr().find(mateId);
		// if (data == null) {
		// return;
		// }
		// TODO @modify 2015-08-25 HC 应答消息，必须要有一个响应消息，不能每个不符合的条件直接return，而没有任何响应消息，客户端会一直等待响应卡死
		MsgItemBagResponse.Builder response = MsgItemBagResponse.newBuilder();
		response.setEventType(EItemBagEventType.ItemBag_Compose);

		List<ItemData> itemList = player.getItemBagMgr().getItemListByCfgId(mateId);
		if (itemList.isEmpty()) {
			player.SendMsg(Command.MSG_ItemBag, response.build().toByteString());
			return;
		}

		ComposeCfg cfg = ComposeCfgDAO.getInstance().GetItemComposeCfg(mateId);
		if (cfg == null) {
			player.SendMsg(Command.MSG_ItemBag, response.build().toByteString());
			return;
		}

		if (player.getUserGameDataMgr().getCoin() < cfg.getCost()) {
			player.SendMsg(Command.MSG_ItemBag, response.build().toByteString());
			return;
		}

		int needCount = cfg.getMate1Count() * composeCount;
		if (player.getItemBagMgr().getItemCountByModelId(mateId) < needCount) {
			player.SendMsg(Command.MSG_ItemBag, response.build().toByteString());
			return;
		}

		// player.getItemBagMgr().subItem(mateId, needCount);
		// TODO @modify 2015-08-11 HC
		player.getItemBagMgr().useItemByCfgId(mateId, needCount);
		player.getUserGameDataMgr().addCoin(-cfg.getCost());
		player.getItemBagMgr().addItem(cfg.getId(), composeCount);

		//检查一下是不是宝石
		if(ItemCfgHelper.getItemType(mateId)  == EItemTypeDef.Gem){
			player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.JEWEREY_COMPOSE, 1);
		}
		
		
		// MsgItemBagResponse.Builder response = MsgItemBagResponse.newBuilder();
		// response.setEventType(EItemBagEventType.ItemBag_Compose);
		player.SendMsg(Command.MSG_ItemBag, response.build().toByteString());
	}

	/**
	 * 使用物品
	 * 
	 * @param player
	 * @param useItemInfo
	 * @return
	 */
	public ByteString useItem(Player player, UseItemInfo useItemInfo) {
		MsgItemBagResponse.Builder rsp = MsgItemBagResponse.newBuilder();
		rsp.setEventType(EItemBagEventType.UseItem);

		if (useItemInfo == null) {
			GameLog.error("背包道具使用", player.getUserId(), String.format("客户端传递到服务器端的UseItemInfo是Null"));
			rsp.setRspInfo(fillResponseInfo(false, "使用失败"));
			return rsp.build().toByteString();
		}

		String id = useItemInfo.getDbId();
		int count = useItemInfo.getCount();

		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		ItemData itemData = itemBagMgr.findBySlotId(id);
		if (itemData == null) {
			rsp.setRspInfo(fillResponseInfo(false, "道具不存在"));
			return rsp.build().toByteString();
		}

		if (count <= 0) {
			rsp.setRspInfo(fillResponseInfo(false, "至少使用数量为1"));
			return rsp.build().toByteString();
		}

		int itemCount = itemData.getCount();
		if (count > itemCount) {
			rsp.setRspInfo(fillResponseInfo(false, "使用数量超过道具上限"));
			return rsp.build().toByteString();
		}

		int itemTemplateId = itemData.getModelId();
		ItemBaseCfg itemBaseCfg = ItemCfgHelper.GetConfig(itemTemplateId);
		if (itemBaseCfg == null) {
			GameLog.error("背包道具使用", player.getUserId(), String.format("模版Id为[%s]的道具的模版查找不到", itemTemplateId));
			rsp.setRspInfo(fillResponseInfo(false, "道具不存在"));
			return rsp.build().toByteString();
		}

		// int consumeType = itemBaseCfg.getConsumeType();
		// if (consumeType != eConsumeTypeDef.PowerConsume.getOrder() && consumeType != eConsumeTypeDef.VipExpConsume.getOrder()) {
		// GameLog.error("背包道具使用", player.getUserId(), String.format("模版Id为[%s]的道具不能被使用", itemTemplateId));
		// rsp.setRspInfo(fillResponseInfo(false, "该道具不能使用"));
		// return rsp.build().toByteString();
		// }

		ConsumeCfg consumeCfg = ItemCfgHelper.getConsumeCfg(itemTemplateId);
		if (consumeCfg == null) {
			GameLog.error("背包道具使用", player.getUserId(), String.format("模版Id为[%s]的道具ConsumeCfg模版查找不到", itemTemplateId));
			rsp.setRspInfo(fillResponseInfo(false, "道具不存在"));
			return rsp.build().toByteString();
		}

		// RoleUpgradeCfg roleUpgradeCfg = RoleUpgradeCfgDAO.getInstance().getCfg(player.getLevel());
		// if (roleUpgradeCfg == null) {
		// GameLog.error("背包道具使用", player.getUserId(), String.format("角色的等级为[%s]的RoleUpgradeCfg模版找不到", player.getLevel()));
		// rsp.setRspInfo(fillResponseInfo(false, "数据异常"));
		// return rsp.build().toByteString();
		// }
		//
		// int addValue = consumeCfg.getValue();
		// // 体力判断
		// if (consumeType == eConsumeTypeDef.PowerConsume.getOrder()) {
		// int mostPower = roleUpgradeCfg.getMostPower();
		// if (player.getUserGameDataMgr().getPower() >= mostPower) {// 超过上限
		// rsp.setRspInfo(fillResponseInfo(false, "体力已达上限"));
		// return rsp.build().toByteString();
		// }
		// } else if (consumeType == eConsumeTypeDef.VipExpConsume.getOrder()) {
		// if (PrivilegeCfgDAO.getInstance().getCfg(addValue) == null) {
		// GameLog.error("背包道具使用", player.getUserId(), String.format("使用VIP卡的等级是[%s]并无PrivilegeCfg配置", addValue));
		// rsp.setRspInfo(fillResponseInfo(false, "当前没有VIP" + addValue));
		// return rsp.build().toByteString();
		// }
		//
		// int curVipLevel = player.getVip();
		// if (addValue <= curVipLevel) {
		// rsp.setRspInfo(fillResponseInfo(false, "您已经是尊贵的VIP" + curVipLevel + "用户"));
		// return rsp.build().toByteString();
		// }
		// }
		//
		// // 使用道具
		// if (!itemBagMgr.useItemBySlotId(id, count)) {
		// rsp.setRspInfo(fillResponseInfo(false, "使用失败"));
		// return rsp.build().toByteString();
		// }
		//
		// if (consumeType == eConsumeTypeDef.PowerConsume.getOrder()) {
		// player.addPower(addValue);
		// } else {
		// player.setVip(addValue);
		// }

		ItemUseEffectCfgDAO cfgDAO = ItemUseEffectCfgDAO.getCfgDAO();
		ItemUseEffectTemplate tmp = cfgDAO.getUseEffectTemplateByModelId(itemTemplateId);
		if (tmp == null) {
			GameLog.error("背包道具使用", player.getUserId(), String.format("模版Id为[%s]的道具对应的ItemUseEffectTemplate模版找不到", itemTemplateId));
			rsp.setRspInfo(fillResponseInfo(false, "该道具不能使用"));
			return rsp.build().toByteString();
		}

		Map<Integer, Integer> combineUseMap = tmp.getCombineUseMap();
		if (combineUseMap != null && !combineUseMap.isEmpty()) {// 有要结合的数据
			SpecialItemCfgDAO specialCfgDAO = SpecialItemCfgDAO.getDAO();
			for (Entry<Integer, Integer> e : combineUseMap.entrySet()) {
				int key = e.getKey();
				int needCount = e.getValue() * count;

				String resourceName = null;
				long value = 0;
				if (key <= eSpecialItemId.eSpecial_End.getValue()) {
					value = player.getReward(eSpecialItemId.getDef(key));
					SpecialItemCfg cfg = specialCfgDAO.getCfgById(String.valueOf(key));
					if (cfg != null) {
						resourceName = cfg.getName();
					}
				} else {
					value = itemBagMgr.getItemCountByModelId(key);
					ItemBaseCfg needItemCfg = ItemCfgHelper.GetConfig(itemTemplateId);
					if (needItemCfg != null) {
						resourceName = needItemCfg.getName();
					}
				}

				if (StringUtils.isEmpty(resourceName)) {
					GameLog.error("背包道具使用", player.getUserId(), String.format("模版Id为[%s]的道具结合使用的资源[%s]不能找到配置表", itemTemplateId, key));
					rsp.setRspInfo(fillResponseInfo(false, "该道具不能使用"));
					return rsp.build().toByteString();
				}

				if (needCount > value) {
					GameLog.error("背包道具使用", player.getUserId(), String.format("模版Id为[%s]的道具对应的ItemUseEffectTemplate模版找不到", itemTemplateId));
					rsp.setRspInfo(fillResponseInfo(false, resourceName + "数量不足"));
					return rsp.build().toByteString();
				}
			}
		}

		IItemUseEffect useEffectClass = cfgDAO.getItemUseEffectByModelId(itemTemplateId);
		if (useEffectClass == null) {
			GameLog.error("背包道具使用", player.getUserId(), String.format("模版Id为[%s]的道具对应的使用处理类IItemUseEffect找不到", itemTemplateId));
			rsp.setRspInfo(fillResponseInfo(false, "该道具不能使用"));
			return rsp.build().toByteString();
		}

		return useEffectClass.useItem(player, itemData, count, rsp);
	}

	/**
	 * 填充响应消息的内容
	 * 
	 * @param success
	 * @param tipMsg
	 * @return
	 */
	public static ResponseInfo.Builder fillResponseInfo(boolean success, String tipMsg) {
		ResponseInfo.Builder rspInfo = ResponseInfo.newBuilder();
		rspInfo.setSuccess(success);
		rspInfo.setTipMsg(tipMsg);
		return rspInfo;
	}

	public ByteString buyItem(Player player, List<TagCompose> composeList) {
		MsgItemBagResponse.Builder response = MsgItemBagResponse.newBuilder();
		response.setEventType(EItemBagEventType.ItemBag_Buy);
		// String msg = "";
		TagCompose tag = composeList.get(0);
		ItemBaseCfg itemBaseCfg = ItemCfgHelper.GetConfig(tag.getMateId());
		// ConsumeCfg cfg = ItemCfgHelper.getConsumeCfg(tag.getMateId());
		if (itemBaseCfg.getCost() > 0) {
			int cost = itemBaseCfg.getCost() * tag.getComposeCount();
			if (cost <= player.getReward(eSpecialItemId.Gold)) {
				player.getUserGameDataMgr().addGold(-cost);
				player.getItemBagMgr().addItem(tag.getMateId(), tag.getComposeCount());
				response.setRspInfo(fillResponseInfo(true, "购买成功"));
			} else {
				response.setRspInfo(fillResponseInfo(false, "钻石不足"));
				return response.build().toByteString();
			}
		} else {
			response.setRspInfo(fillResponseInfo(false, "不可购买。"));
			return response.build().toByteString();
		}
		return response.build().toByteString();
	}

	/**
	 * 购买法宝强化材料
	 * 
	 * @param player
	 * @param buyItemInfo
	 * @return
	 */
	public ByteString buyMagicForgeMaterial(Player player, BuyItemInfo buyItemInfo) {
		MsgItemBagResponse.Builder response = MsgItemBagResponse.newBuilder();
		response.setEventType(EItemBagEventType.ItemBag_Buy);

		do {// do-while-break 模拟goto
			final int buyCount = buyItemInfo.getCount();
			if (buyCount <= 0) {
				response.setRspInfo(fillResponseInfo(false, "多买几个材料吧！"));
				break;
			}

			if (buyCount > MaxBuyCountLimit) {
				response.setRspInfo(fillResponseInfo(false, "购买材料数量超过上限！"));
				break;
			}

			int consumeMatModelId = -1;
			try {
				consumeMatModelId = Integer.parseInt(buyItemInfo.getModelId());
			} catch (NumberFormatException ex) {
				response.setRspInfo(fillResponseInfo(false, "材料模板ID无效！"));
				break;
			}

			final ConsumeCfg cfg = ItemCfgHelper.getConsumeCfg(consumeMatModelId);
			if (cfg == null) {
				response.setRspInfo(fillResponseInfo(false, "找不到材料配置！"));
				break;
			}

			if (cfg.getConsumeType() != ConsumeTypeDef.Consume_EquipEhanceMat_VALUE) {
				response.setRspInfo(fillResponseInfo(false, "不是强化材料！"));
				break;
			}

			final int unitCost = cfg.getCost();
			if (unitCost <= 0) {
				response.setRspInfo(fillResponseInfo(false, "这个材料不能购买！"));
				break;
			}

			final int totalCost = unitCost * buyCount;
			final eSpecialItemId currencyType = eSpecialItemId.getDef(cfg.getMoneyType());
			if (currencyType == null) {
				response.setRspInfo(fillResponseInfo(false, "货币类型配置无效！"));
				break;
			}

			if (!player.getUserGameDataMgr().deductCurrency(currencyType, totalCost)) {
				response.setRspInfo(fillResponseInfo(false, "货币不足！"));
				break;
			}

			player.getItemBagMgr().addItem(consumeMatModelId, buyCount);
			response.setRspInfo(fillResponseInfo(true, "购买成功"));
			break;

		} while (true);

		return response.build().toByteString();
	}

	/**
	 * 分解法宝或法宝碎片
	 * 
	 * @param player
	 * @param useItemInfo
	 * @return
	 */
	public ByteString decomposeMagicItem(Player player, UseItemInfo useItemInfo) {
		MsgItemBagResponse.Builder response = MsgItemBagResponse.newBuilder();
		response.setEventType(EItemBagEventType.ItemBag_MagicWeapon_Decompose);

		do {// do-while-break 模拟goto
			final ItemBagMgr bagMgr = player.getItemBagMgr();
			final ItemData item = bagMgr.findBySlotId(useItemInfo.getDbId());
			if (item == null) {
				response.setRspInfo(fillResponseInfo(false, "找不到物品！"));
				break;
			}

			final EItemTypeDef itemType = item.getType();
			if (itemType != EItemTypeDef.Magic && itemType != EItemTypeDef.Magic_Piece) {
				response.setRspInfo(fillResponseInfo(false, "不是法宝或者法宝碎片！"));
				break;
			}

			final int modelId = item.getModelId();
			final String modelIdStr = String.valueOf(modelId);
			if (itemType == EItemTypeDef.Magic_Piece) {
				final int useCount = useItemInfo.getCount();
				if (useCount > item.getCount()) {
					response.setRspInfo(fillResponseInfo(false, "分解数量太大！"));
					break;
				}

				if (useCount < 1) {
					response.setRspInfo(fillResponseInfo(false, "请指定分解数量！"));
					break;
				}

				final MagicCfg cfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(modelIdStr);
				if (cfg == null) {
					response.setRspInfo(fillResponseInfo(false, "找不到法宝碎片配置！"));
					break;
				}

				// 移除物品
				if (!bagMgr.useItemByCfgId(modelId, useCount)) {
					response.setRspInfo(fillResponseInfo(false, "无法使用法宝碎片！"));
					GameLog.error("背包", "法宝", "使用法宝碎片失败！");
					break;
				}

				final List<Pair<Integer, Integer>> lst = cfg.getDecomposeGoodList();
				for (Pair<Integer, Integer> pair : lst) {
					final boolean addItemResult = bagMgr.addItem(pair.getT1().intValue(), pair.getT2().intValue() * useCount);
					if (!addItemResult) {
						GameLog.error("背包", "法宝", "添加背包物品失败！物品ID：" + pair.getT1());
					}
				}

				response.setRspInfo(fillResponseInfo(true, "分解成功"));
				break;
			}

			if (itemType == EItemTypeDef.Magic) {
				final MagicCfg cfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(modelIdStr);
				if (cfg == null) {
					response.setRspInfo(fillResponseInfo(false, "找不到法宝配置！"));
					break;
				}

				final String expStr = item.getExtendAttr(EItemAttributeType.Magic_Exp_VALUE);
				int totalExp = -1;
				try {
					totalExp = Integer.parseInt(expStr);
					if (totalExp < 0) {
						response.setRspInfo(fillResponseInfo(false, "无法获取法宝经验值！"));
						break;
					}
				} catch (Exception ex) {
					response.setRspInfo(fillResponseInfo(false, "无法获取法宝经验值！"));
					break;
				}

				final float coeff = cfg.getCoefficient();
				if (coeff <= 0) {
					response.setRspInfo(fillResponseInfo(false, "配置系数有误！"));
					break;
				}

				final int addgoodId = cfg.getConvertedGoodModelId();
				final ConsumeCfg addGoodCfg = ItemCfgHelper.getConsumeCfg(addgoodId);
				if (addGoodCfg == null) {
					response.setRspInfo(fillResponseInfo(false, "无法获取消耗品！"));
					break;
				}

				final int unitExp = addGoodCfg.getValue();
				if (unitExp <= 0) {
					response.setRspInfo(fillResponseInfo(false, "无法获取消耗品经验值！"));
					break;
				}

				final int addedCount = (int) (totalExp * coeff / (float) unitExp);
				final int useCount = 1;

				// 移除物品
				if (!bagMgr.useItemByCfgId(modelId, useCount)) {
					response.setRspInfo(fillResponseInfo(false, "无法使用法宝！"));
					GameLog.error("背包", "法宝", "使用法宝失败！");
					break;
				}

				final List<Pair<Integer, Integer>> lst = cfg.getDecomposeGoodList();
				for (Pair<Integer, Integer> pair : lst) {
					boolean addItemResult = bagMgr.addItem(pair.getT1().intValue(), pair.getT2().intValue());
					if (!addItemResult) {
						GameLog.error("背包", "法宝", "分解法宝时添加材料失败！材料ID:" + pair.getT1().intValue());
					}
				}

				if (addedCount > 0) {
					boolean addItemResult = bagMgr.addItem(addgoodId, addedCount);
					if (!addItemResult) {
						GameLog.error("背包", "法宝", "分解法宝兑换经验时失败！无法添加物品ID：" + addgoodId);
					}
				}

				response.setRspInfo(fillResponseInfo(true, "分解成功"));
				break;
			}

			response.setRspInfo(fillResponseInfo(false, "分解失败，未知错误"));
			break;
		} while (true);

		return response.build().toByteString();
	}
}