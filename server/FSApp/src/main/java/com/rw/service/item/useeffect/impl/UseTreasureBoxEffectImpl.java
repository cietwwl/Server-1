package com.rw.service.item.useeffect.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.readonly.ItemDataIF;
import com.rw.fsutil.common.DataAccessTimeoutException;
import com.rw.service.dropitem.DropItemManager;
import com.rw.service.item.ItemBagHandler;
import com.rw.service.item.useeffect.IItemUseEffect;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.item.ItemUseEffectCfgDAO;
import com.rwbase.dao.item.pojo.ItemUseEffectTemplate;
import com.rwbase.dao.item.pojo.itembase.INewItem;
import com.rwbase.dao.item.pojo.itembase.IUseItem;
import com.rwbase.dao.item.pojo.itembase.UseItem;
import com.rwproto.ItemBagProtos.MsgItemBagResponse.Builder;
import com.rwproto.ItemBagProtos.RewardInfo;

/*
 * @author HC
 * @date 2016年5月18日 上午10:32:30
 * @Description 使用宝箱的效果实现
 */
public class UseTreasureBoxEffectImpl implements IItemUseEffect {

	@Override
	public ByteString useItem(Player player, ItemDataIF itemData, int useCount, Builder rsp) {
		int modelId = itemData.getModelId();
		ItemUseEffectTemplate tmp = ItemUseEffectCfgDAO.getCfgDAO().getUseEffectTemplateByModelId(modelId);

		// 使用的道具
		ItemBagMgr itemBagMgr = player.getItemBagMgr();

		HashMap<Integer, Integer> useMoneyMap = new HashMap<Integer, Integer>();
		List<INewItem> newItemList = new ArrayList<INewItem>();
		HashMap<Integer, Integer> rewardInfoMap = new HashMap<Integer, Integer>();

		List<IUseItem> useItemList = new ArrayList<IUseItem>();
		useItemList.add(new UseItem(itemData.getId(), useCount));

		Map<Integer, Integer> combineUseMap = tmp.getCombineUseMap();
		if (combineUseMap != null && !combineUseMap.isEmpty()) {
			for (Entry<Integer, Integer> e : combineUseMap.entrySet()) {
				int key = e.getKey();
				Integer value = e.getValue();

				if (value <= 0) {
					continue;
				}

				if (key < eSpecialItemId.eSpecial_End.getValue()) {
					Integer hasValue = useMoneyMap.get(key);
					if (hasValue == null) {
						useMoneyMap.put(key, value * useCount);
					} else {
						useMoneyMap.put(key, hasValue + (value * useCount));
					}
				} else {
					useItemList.add(new UseItem(itemBagMgr.getFirstItemByModelId(key).getId(), value * useCount));
				}
			}
		}

		// 产生的掉落物品
		try {
			for (int i = 0; i < useCount; i++) {
				List<ItemInfo> pretreatDrop = DropItemManager.getInstance().pretreatDrop(player, tmp.getDropList(), -100, false);
				if (pretreatDrop.isEmpty()) {
					GameLog.error("使用宝箱类道具", "宝箱模版Id：" + modelId, "宝箱产出掉落的时候出现了错误情况：无法产出任何掉落");
					rsp.setRspInfo(ItemBagHandler.fillResponseInfo(false, "无法产出掉落物品"));
					return rsp.build().toByteString();
				}

				for (int j = 0, size = pretreatDrop.size(); j < size; j++) {
					ItemInfo itemInfo = pretreatDrop.get(j);
					if (itemInfo == null) {
						continue;
					}

					int itemNum = itemInfo.getItemNum();

					if (itemNum <= 0) {
						continue;
					}

					int itemId = itemInfo.getItemID();
					Integer hasValue = rewardInfoMap.get(itemId);
					if (hasValue == null) {
						rewardInfoMap.put(itemId, itemNum);
					} else {
						rewardInfoMap.put(itemId, itemNum + hasValue);
					}
				}
			}
		} catch (DataAccessTimeoutException e) {
			GameLog.error("使用宝箱类道具", "宝箱模版Id：" + modelId, "宝箱产出掉落的时候出现了异常情况", e);
			rsp.setRspInfo(ItemBagHandler.fillResponseInfo(false, "无法产出掉落物品"));
			return rsp.build().toByteString();
		}

		// 使用道具
		if (!itemBagMgr.useLikeBoxItem(useItemList, newItemList, useMoneyMap)) {
			rsp.setRspInfo(ItemBagHandler.fillResponseInfo(false, "使用失败"));
			return rsp.build().toByteString();
		}

		// 填充消息
		for (Entry<Integer, Integer> e : rewardInfoMap.entrySet()) {
			RewardInfo.Builder rewardInfo = RewardInfo.newBuilder();
			rewardInfo.setRewardId(String.valueOf(e.getKey()));
			rewardInfo.setRewardCount(e.getValue());
			rsp.addRewardInfo(rewardInfo);
		}

		return rsp.build().toByteString();
	}
}