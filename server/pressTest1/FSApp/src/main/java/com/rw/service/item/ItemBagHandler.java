package com.rw.service.item;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.ItemBagMgr;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.rwbase.common.enu.eConsumeTypeDef;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.item.ComposeCfgDAO;
import com.rwbase.dao.item.pojo.ComposeCfg;
import com.rwbase.dao.item.pojo.ConsumeCfg;
import com.rwbase.dao.item.pojo.ItemBaseCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.itembase.IUseItem;
import com.rwbase.dao.item.pojo.itembase.UseItem;
import com.rwbase.dao.power.RoleUpgradeCfgDAO;
import com.rwbase.dao.power.pojo.RoleUpgradeCfg;
import com.rwbase.dao.vip.PrivilegeCfgDAO;
import com.rwproto.ItemBagProtos.EItemBagEventType;
import com.rwproto.ItemBagProtos.MsgItemBagResponse;
import com.rwproto.ItemBagProtos.ResponseInfo;
import com.rwproto.ItemBagProtos.TagCompose;
import com.rwproto.ItemBagProtos.TagItemData;
import com.rwproto.ItemBagProtos.UseItemInfo;
import com.rwproto.MsgDef.Command;

public class ItemBagHandler {

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
			return response.build().toByteString();
		}

		List<IUseItem> useList = new ArrayList<IUseItem>();
		List<String> idList = new ArrayList<String>();

		int totalSellCoin = 0;// 总共出售可以获得的价格
		for (int i = 0, size = sellItemList.size(); i < size; i++) {
			TagItemData data = sellItemList.get(i);
			String dbId = data.getDbId();
			if (idList.contains(dbId)) {
				return response.build().toByteString();
			}

			ItemData itemData = player.getItemBagMgr().findBySlotId(dbId);
			if (itemData == null) {
				return response.build().toByteString();
			}

			int templateId = itemData.getModelId();
			ItemBaseCfg baseCfg = ItemCfgHelper.GetConfig(templateId);
			if (baseCfg == null) {
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

		int consumeType = itemBaseCfg.getConsumeType();
		if (consumeType != eConsumeTypeDef.PowerConsume.getOrder() && consumeType != eConsumeTypeDef.VipExpConsume.getOrder()) {
			GameLog.error("背包道具使用", player.getUserId(), String.format("模版Id为[%s]的道具不能被使用", itemTemplateId));
			rsp.setRspInfo(fillResponseInfo(false, "该道具不能使用"));
			return rsp.build().toByteString();
		}

		ConsumeCfg consumeCfg = ItemCfgHelper.getConsumeCfg(itemTemplateId);
		if (consumeCfg == null) {
			GameLog.error("背包道具使用", player.getUserId(), String.format("模版Id为[%s]的道具ConsumeCfg模版查找不到", itemTemplateId));
			rsp.setRspInfo(fillResponseInfo(false, "道具不存在"));
			return rsp.build().toByteString();
		}

		RoleUpgradeCfg roleUpgradeCfg = RoleUpgradeCfgDAO.getInstance().getCfg(player.getLevel());
		if (roleUpgradeCfg == null) {
			GameLog.error("背包道具使用", player.getUserId(), String.format("角色的等级为[%s]的RoleUpgradeCfg模版找不到", player.getLevel()));
			rsp.setRspInfo(fillResponseInfo(false, "数据异常"));
			return rsp.build().toByteString();
		}

		int addValue = consumeCfg.getValue();
		// 体力判断
		if (consumeType == eConsumeTypeDef.PowerConsume.getOrder()) {
			int mostPower = roleUpgradeCfg.getMostPower();
			if (player.getUserGameDataMgr().getPower() >= mostPower) {// 超过上限
				rsp.setRspInfo(fillResponseInfo(false, "体力已达上限"));
				return rsp.build().toByteString();
			}
		} else if (consumeType == eConsumeTypeDef.VipExpConsume.getOrder()) {
			if (PrivilegeCfgDAO.getInstance().getCfg(addValue) == null) {
				GameLog.error("背包道具使用", player.getUserId(), String.format("使用VIP卡的等级是[%s]并无PrivilegeCfg配置", addValue));
				rsp.setRspInfo(fillResponseInfo(false, "当前没有VIP" + addValue));
				return rsp.build().toByteString();
			}

			int curVipLevel = player.getVip();
			if (addValue <= curVipLevel) {
				rsp.setRspInfo(fillResponseInfo(false, "您已经是尊贵的VIP" + curVipLevel + "用户"));
				return rsp.build().toByteString();
			}
		}

		// 使用道具
		if (!itemBagMgr.useItemBySlotId(id, count)) {
			rsp.setRspInfo(fillResponseInfo(false, "使用失败"));
			return rsp.build().toByteString();
		}

		if (consumeType == eConsumeTypeDef.PowerConsume.getOrder()) {
			player.addPower(addValue);
		} else {
			player.setVip(addValue);
		}
		return rsp.build().toByteString();
	}

	/**
	 * 填充响应消息的内容
	 * 
	 * @param success
	 * @param tipMsg
	 * @return
	 */
	private ResponseInfo.Builder fillResponseInfo(boolean success, String tipMsg) {
		ResponseInfo.Builder rspInfo = ResponseInfo.newBuilder();
		rspInfo.setSuccess(success);
		rspInfo.setTipMsg(tipMsg);
		return rspInfo;
	}
	
	public ByteString buyItem(Player player, List<TagCompose> composeList) {
		MsgItemBagResponse.Builder response = MsgItemBagResponse.newBuilder();
		response.setEventType(EItemBagEventType.ItemBag_Buy);
		String msg = "";
		TagCompose tag  = composeList.get(0);
		ItemBaseCfg itemBaseCfg = ItemCfgHelper.GetConfig(tag.getMateId());
		ConsumeCfg cfg = ItemCfgHelper.getConsumeCfg(tag.getMateId());
		if(itemBaseCfg.getCost() > 0){
			int cost = itemBaseCfg.getCost() * tag.getComposeCount();
			if(cost <= player.getReward(eSpecialItemId.Gold)){
				player.getUserGameDataMgr().addGold(-cost);
				player.getItemBagMgr().addItem(tag.getMateId(), tag.getComposeCount());
				response.setRspInfo(fillResponseInfo(true, "购买成功"));
			}else{
				response.setRspInfo(fillResponseInfo(false, "钻石不足"));
				return response.build().toByteString();
			}
		}else{
			response.setRspInfo(fillResponseInfo(false, "不可购买。"));
			return response.build().toByteString();
		}
		return response.build().toByteString();
	}
}