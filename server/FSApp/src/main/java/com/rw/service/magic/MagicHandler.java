package com.rw.service.magic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.bm.GFOnlineListenerPlayerChange;
import com.playerdata.teambattle.bm.TBListenerPlayerChange;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rw.service.item.checkWare.CheckMagicWare;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.item.MagicCfgDAO;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.MagicCfg;
import com.rwbase.dao.item.pojo.itembase.IUseItem;
import com.rwbase.dao.magicweapon.MagicExpCfgDAO;
import com.rwbase.dao.magicweapon.MagicSmeltMaterialCfgDAO;
import com.rwbase.dao.magicweapon.MagicSmeltRateCfgDAO;
import com.rwbase.dao.magicweapon.pojo.MagicExpCfg;
import com.rwbase.dao.magicweapon.pojo.MagicSmeltMaterialCfg;
import com.rwbase.dao.magicweapon.pojo.MagicSmeltRateCfg;
import com.rwproto.ItemBagProtos.EItemAttributeType;
import com.rwproto.ItemBagProtos.EItemTypeDef;
import com.rwproto.MagicServiceProtos.MagicInheritReqMsg;
import com.rwproto.MagicServiceProtos.MsgMagicRequest;
import com.rwproto.MagicServiceProtos.MsgMagicResponse;
import com.rwproto.MagicServiceProtos.MsgMagicResponse.Builder;
import com.rwproto.MagicServiceProtos.eMagicResultType;
import com.rwproto.MagicServiceProtos.eMagicType;

public class MagicHandler {

	private static MagicHandler instance = new MagicHandler();

	/** 法宝等级对应的经验 */

	protected MagicHandler() {
	}

	public static MagicHandler getInstance() {
		return instance;
	}

	/**
	 * 装备法宝
	 * 
	 * @param player
	 * @param msgMagicRequest
	 * @return
	 */
	public ByteString wearMagicWeapon(Player player, MsgMagicRequest msgMagicRequest) {
		MsgMagicResponse.Builder msgMagicResponse = MsgMagicResponse.newBuilder();
		msgMagicResponse.setMagicType(msgMagicRequest.getMagicType());
		String magicWeaponSlotId = msgMagicRequest.getId();

		if (!player.getMagicMgr().wearMagic(magicWeaponSlotId)) {
			return setReturnResponse(msgMagicResponse, null);
		}

		msgMagicResponse.setEMagicResultType(eMagicResultType.SUCCESS);
		return msgMagicResponse.build().toByteString();
	}

	/**
	 * 强化法宝（升级）（消耗一定的材料进行法宝升级）
	 * 
	 * @param player
	 * @param msgMagicRequest
	 * @return
	 */
	public ByteString forgeMagicWeapon(Player player, MsgMagicRequest msgMagicRequest) {
		MsgMagicResponse.Builder msgMagicResponse = MsgMagicResponse.newBuilder();
		msgMagicResponse.setMagicType(msgMagicRequest.getMagicType());

		String userId = player.getUserId();
		ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();

		ItemData itemData = itemBagMgr.findBySlotId(userId, msgMagicRequest.getId());
		if (itemData == null) {
			return setReturnResponse(msgMagicResponse, "找不到法宝！");
		}

		int maxMagicLevel = MagicExpCfgDAO.getInstance().getMaxMagicLevel();

		int currentLevel = Integer.parseInt(itemData.getExtendAttr(EItemAttributeType.Magic_Level_VALUE));
		int nextLevel = currentLevel + 1;
		if (currentLevel >= maxMagicLevel) {
			return setReturnResponse(msgMagicResponse, "当亲法宝已经达到最高等级");
		}

		if (currentLevel > player.getLevel()) {
			return setReturnResponse(msgMagicResponse, "法宝等级超过了玩家等级！");
		}

		MagicExpCfg magicCfg = MagicExpCfgDAO.getInstance().getMagicCfgByLevel(currentLevel);
		HashMap<Integer, Integer> consumeItemMap = magicCfg.getConsumeMap();

		Map<Integer, ItemData> modelFirstItemDataMap = itemBagMgr.getModelFirstItemDataMap(userId);
		List<IUseItem> useItemList = new ArrayList<IUseItem>(consumeItemMap.size());
		String checkResult = checkEnoughMaterial(consumeItemMap, player, modelFirstItemDataMap, useItemList, itemData);
		if (checkResult != null) {
			return setReturnResponse(msgMagicResponse, checkResult);
		}

		if (!itemBagMgr.useLikeBoxItem(player, useItemList, null, null)) {
			return setReturnResponse(msgMagicResponse, "法宝升级失败，消耗升级材料失败！");
		}

		itemData.setExtendAttr(EItemAttributeType.Magic_Level_VALUE, String.valueOf(nextLevel));

		int state = getItemMagicState(itemData);
		if (state == 1) {
			player.getMagicMgr().updateMagic();
		}
		UserEventMgr.getInstance().StrengthenMagicVitality(player, nextLevel);
		itemBagMgr.updateItem(itemData);
		List<ItemData> updateItems = new ArrayList<ItemData>(1);
		updateItems.add(itemData);
		itemBagMgr.syncItemData(player, updateItems);

		player.getFresherActivityMgr().doCheck(eActivityType.A_MagicLv);

		// 通知角色日常任务 by Alex
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.MAGIC_STRENGTH, 1);

		// 通知法宝神器羁绊
		player.getMe_FetterMgr().notifyMagicChange(player);

		msgMagicResponse.setEMagicResultType(eMagicResultType.SUCCESS);
		return msgMagicResponse.build().toByteString();
	}

	/**
	 * 封装返回操作失败
	 * 
	 * @param msgMagicResponse
	 * @param tips
	 * @return
	 */
	private ByteString setReturnResponse(MsgMagicResponse.Builder msgMagicResponse, String tips) {
		if (!StringUtils.isBlank(tips))
			msgMagicResponse.setResultTip(tips);
		msgMagicResponse.setEMagicResultType(eMagicResultType.FAIL);
		return msgMagicResponse.build().toByteString();
	}

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

		String userId = player.getUserId();
		ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();

		ItemData itemData = itemBagMgr.findBySlotId(userId, msgMagicRequest.getId());

		if (itemData == null) {
			return setReturnResponse(msgMagicResponse, "找不到法宝！");
		}

		int aptitudeValue = itemData.getMagicAptitude();

		if (aptitudeValue >= MagicSmeltMaterialCfgDAO.getInstance().getMaxAptiude()) {
			return setReturnResponse(msgMagicResponse, "当前法宝已到最高资质不需要熔炼！");
		}

		MagicSmeltMaterialCfg magicSmeltMaterialCfg = MagicSmeltMaterialCfgDAO.getInstance().getMagicSmeltMaterialCfgByAptitude(aptitudeValue);

		HashMap<Integer, Integer> needMaterialMap = magicSmeltMaterialCfg.getNeedMaterialMap();

		Map<Integer, ItemData> modelFirstItemDataMap = itemBagMgr.getModelFirstItemDataMap(userId);
		List<IUseItem> useItemList = new ArrayList<IUseItem>(needMaterialMap.size());

		String checkResult = checkEnoughMaterial(needMaterialMap, player, modelFirstItemDataMap, useItemList, itemData);
		if (checkResult != null) {
			return setReturnResponse(msgMagicResponse, checkResult);
		}

		MagicSmeltRateCfg magicSmelt = MagicSmeltRateCfgDAO.getInstance().magicSmelt(aptitudeValue);

		if (magicSmelt == null) {
			return setReturnResponse(msgMagicResponse, "法宝进化异常！");
		}

		int aptitudeChange = magicSmelt.getAptitudeChange();

		int smeltAptitude = aptitudeValue + aptitudeChange;

		// 防止资质变0
		if (smeltAptitude == 0) {
			smeltAptitude = 1;
		}

		// 消耗物品
		boolean success = itemBagMgr.useLikeBoxItem(player, useItemList, null);
		if (!success) {
			return setReturnResponse(msgMagicResponse, "消耗法宝进化失败！");
		}

		itemData.setExtendAttr(EItemAttributeType.Magic_Aptitude_VALUE, String.valueOf(smeltAptitude));
		msgMagicResponse.setEMagicResultType(eMagicResultType.SUCCESS);

		int state = getItemMagicState(itemData);
		if (state == 1) {
			player.getMagicMgr().updateMagic();
		}
		// 同步数据
		itemBagMgr.updateItem(itemData);
		List<ItemData> updateItems = new ArrayList<ItemData>(1);
		updateItems.add(itemData);
		itemBagMgr.syncItemData(player, updateItems);

		// 通知法宝神器羁绊
		player.getMe_FetterMgr().notifyMagicChange(player);
		return msgMagicResponse.build().toByteString();
	}

	/**
	 * 法宝进化
	 * 
	 * @param player
	 * @param msgMagicRequest
	 * @return
	 */
	public ByteString upgradeMagicWeapon(Player player, MsgMagicRequest msgMagicRequest) {
		MsgMagicResponse.Builder response = MsgMagicResponse.newBuilder();
		response.setMagicType(msgMagicRequest.getMagicType());

		String userId = player.getUserId();
		final ItemBagMgr bagMgr = ItemBagMgr.getInstance();
		final String magicStoreId = msgMagicRequest.getId();
		final ItemData item = bagMgr.findBySlotId(userId, magicStoreId);
		if (item == null) {
			fillResponseInfo(response, false, "找不到物品！");
			return response.build().toByteString();
		}

		if (EItemTypeDef.Magic != item.getType()) {
			fillResponseInfo(response, false, "不是法宝，不能升级！");
			return response.build().toByteString();
		}

		// 法宝配置
		final int itemModelId = item.getModelId();
		final MagicCfg magicCfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(String.valueOf(itemModelId));
		if (magicCfg == null) {
			fillResponseInfo(response, false, "无法找到法宝配置！");
			return response.build().toByteString();
		}

		// 配置是否可进化
		final String upgradeToModel = magicCfg.getUpMagic();
		final MagicCfg upToCfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(String.valueOf(upgradeToModel));
		if (upToCfg == null) {
			fillResponseInfo(response, false, "这个法宝不能进化！");
			return response.build().toByteString();
		}

		final int uplevel = upToCfg.getUplevel();
		if (uplevel <= 0) {
			fillResponseInfo(response, false, "这个法宝不能进化！");
			return response.build().toByteString();
		}

		// 检查等级
		int lvl = item.getMagicAdvanceLevel();

		if (lvl > uplevel) {
			fillResponseInfo(response, false, "数据异常，这个法宝不能进化！");
			return response.build().toByteString();
		}

		// 检查消耗的货币
		final int cost = magicCfg.getUpMagicCost();
		if (cost <= 0) {
			fillResponseInfo(response, false, "法宝进化配置的货币数量无效！");
			return response.build().toByteString();
		}

		final eSpecialItemId currencyType = eSpecialItemId.getDef(magicCfg.getUpMagicMoneyType());
		if (currencyType == null) {
			fillResponseInfo(response, false, "法宝进化配置的货币类型无效！");
			return response.build().toByteString();
		}

		String goods = magicCfg.getGoods();
		HashMap<Integer, Integer> consumeItemMap = new HashMap<Integer, Integer>();
		String[] split1 = goods.split(",");
		for (String value : split1) {
			String[] split2 = value.split("_");
			if (split2.length != 2) {
				GameLog.error("法宝", "强化", "配置进化材料错误：magicCfg id:" + magicCfg.getId());
				continue;
			}
			int modelId = Integer.parseInt(split2[0]);
			int count = Integer.parseInt(split2[1]);
			consumeItemMap.put(modelId, count);
		}
		ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();
		Map<Integer, ItemData> modelFirstItemDataMap = bagMgr.getModelFirstItemDataMap(userId);
		List<IUseItem> useItemList = new ArrayList<IUseItem>(consumeItemMap.size());

		String checkResult = checkEnoughMaterial(consumeItemMap, player, modelFirstItemDataMap, useItemList, item);
		if (checkResult != null) {
			fillResponseInfo(response, false, checkResult);
			return response.build().toByteString();
		}

		// 扣金币和扣材料
		long curValue = player.getReward(currencyType);
		if (cost > curValue) {
			fillResponseInfo(response, false, "货币不足！");
			return response.build().toByteString();
		}

		Map<Integer, Integer> modifyMoneyMap = new HashMap<Integer, Integer>(1);
		modifyMoneyMap.put(currencyType.getValue(), -cost);

		if (!bagMgr.useLikeBoxItem(player, useItemList, null, modifyMoneyMap)) {
			fillResponseInfo(response, false, "进化失败！");
			return response.build().toByteString();
		}

		// 换modelID
		item.setModelId(upToCfg.getId());
		bagMgr.updateItem(item);

		// 通知背包模块属性被更改
		final List<ItemData> updateItems = new ArrayList<ItemData>(1);
		updateItems.add(item);
		bagMgr.syncItemData(player, updateItems);

		int state = getItemMagicState(item);
		if (state == 1) {
			player.getMagicMgr().updateMagic();
		}

		response.setNewMagicModelId(upToCfg.getId());

		GFOnlineListenerPlayerChange.defenderChangeHandler(player);
		TBListenerPlayerChange.heroChangeHandler(player);

		// 通知法宝神器羁绊
		player.getMe_FetterMgr().notifyMagicChange(player);

		fillResponseInfo(response, true, "进化成功！");
		return response.build().toByteString();
	}

	private String checkEnoughMaterial(HashMap<Integer, Integer> consumeItemMap, Player player, Map<Integer, ItemData> modelFirstItemDataMap, List<IUseItem> useItemList, ItemData currentMagic) {
		ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();
		for (Iterator<Entry<Integer, Integer>> iterator = consumeItemMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, Integer> entry = iterator.next();
			int modelId = entry.getKey();
			int count = entry.getValue();

			List<IUseItem> items = itemBagMgr.checkEnoughItem(player, modelId, count, currentMagic, new CheckMagicWare());

			if (items == null) {
				return "法宝进化材料不足！";
			} else {
				useItemList.addAll(items);
			}
		}
		return null;
	}

	private int getItemMagicState(final ItemData item) {
		int state = 0;// 默认可以认为不是穿戴在身上的
		try {
			String stateStr = item.getExtendAttr(EItemAttributeType.Magic_State_VALUE);
			if (!StringUtils.isBlank(stateStr)) {
				state = Integer.parseInt(stateStr);
			}
		} catch (Exception ex) {
		}
		return state;
	}

	private void fillResponseInfo(Builder response, boolean isSuccess, String tip) {
		if (!StringUtils.isBlank(tip))
			response.setResultTip(tip);
		response.setEMagicResultType(isSuccess ? eMagicResultType.SUCCESS : eMagicResultType.FAIL);
	}

	/**
	 * 法宝继承
	 * 
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString magicInheritHandler(Player player, MsgMagicRequest req) {
		MagicInheritReqMsg inheritReqMsg = req.getInheritReqMsg();
		MsgMagicResponse.Builder rsp = MsgMagicResponse.newBuilder();
		rsp.setMagicType(eMagicType.Magic_Inherit);

		String id = inheritReqMsg.getId();// 被继承的法宝Id
		String toId = inheritReqMsg.getToId();// 请求继承的法宝Id

		if (id.equals(toId)) {// 如果两个法宝传的Id是一个法宝，直接返回
			fillResponseInfo(rsp, false, "不能使用同一个法宝继承");
			return rsp.build().toByteString();
		}

		ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();
		String userId = player.getUserId();
		ItemData magic = itemBagMgr.findBySlotId(userId, id);// 被继承的法宝
		if (magic == null) {
			fillResponseInfo(rsp, false, "被继承的法宝不存在");
			return rsp.build().toByteString();
		}

		ItemData toMagic = itemBagMgr.findBySlotId(userId, toId);// 请求继承的法宝
		if (toMagic == null) {
			fillResponseInfo(rsp, false, "继承的法宝不存在");
			return rsp.build().toByteString();
		}

		int magicLevel = Integer.parseInt(magic.getExtendAttr(EItemAttributeType.Magic_Level_VALUE));
		int toMagicLevel = Integer.parseInt(toMagic.getExtendAttr(EItemAttributeType.Magic_Level_VALUE));
		if (magicLevel <= toMagicLevel) {
			return setReturnResponse(rsp, "继承的法宝必须比被继承法宝等级低！");
		}

		int currencyType = -1;
		int totalCost = 0;

		int inheritExp = 0;
		MagicExpCfg magicCfg = MagicExpCfgDAO.getInstance().getMagicCfgByLevel(magicLevel);
		currencyType = magicCfg.getMoneyType();
		totalCost = magicCfg.getCost();
		List<MagicExpCfg> inheritList = MagicExpCfgDAO.getInstance().getInheritList(toMagicLevel, magicLevel);
		for (MagicExpCfg magicExpCfg : inheritList) {

			int exp = magicExpCfg.getExp();
			inheritExp += exp;
		}
		eSpecialItemId specialItemId = eSpecialItemId.getDef(currencyType);
		// 扣金币和扣材料
		long curValue = player.getReward(specialItemId);
		if (totalCost > curValue) {
			return setReturnResponse(rsp, "货币不足！");
		}

		MagicExpCfg cfg = MagicExpCfgDAO.getInstance().getInheritCfg(toMagicLevel, inheritExp);
		int tempLevel = cfg.getLevel();

		// 规定法宝继承后的等级不能比超过被继承法宝原来的等级
		if (tempLevel > magicLevel) {
			tempLevel = magicLevel;
		}

		if (tempLevel == toMagicLevel) {
			return setReturnResponse(rsp, "继承的法宝升级不了被继承法宝！");
		}

		if (!itemBagMgr.addItem(player, currencyType, -totalCost)) {
			return setReturnResponse(rsp, "货币不足！");
		}

		toMagic.setExtendAttr(EItemAttributeType.Magic_Level_VALUE, String.valueOf(tempLevel));
		magic.setExtendAttr(EItemAttributeType.Magic_Level_VALUE, String.valueOf(1));

		if (getItemMagicState(toMagic) == 1 || getItemMagicState(magic) == 1) {
			player.getMagicMgr().updateMagic();
		}

		itemBagMgr.updateItem(magic);
		itemBagMgr.updateItem(toMagic);
		List<ItemData> updateItems = new ArrayList<ItemData>(2);
		updateItems.add(magic);
		updateItems.add(toMagic);
		itemBagMgr.syncItemData(player, updateItems);
		
		player.getMe_FetterMgr().notifyMagicChange(player); // 法宝继承通知羁绊变化

		rsp.setEMagicResultType(eMagicResultType.SUCCESS);
		return rsp.build().toByteString();
	}
}