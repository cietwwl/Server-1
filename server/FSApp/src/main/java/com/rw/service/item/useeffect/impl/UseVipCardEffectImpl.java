package com.rw.service.item.useeffect.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.readonly.ItemDataIF;
import com.rw.service.item.ItemBagHandler;
import com.rw.service.item.useeffect.IItemUseEffect;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.item.ItemUseEffectCfgDAO;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.ItemUseEffectTemplate;
import com.rwbase.dao.item.pojo.itembase.IUseItem;
import com.rwbase.dao.item.pojo.itembase.UseItem;
import com.rwbase.dao.vip.PrivilegeCfgDAO;
import com.rwproto.ItemBagProtos.MsgItemBagResponse;

/*
 * @author HC
 * @date 2016年5月18日 上午10:31:33
 * @Description 使用VIP卡的效果
 */
public class UseVipCardEffectImpl implements IItemUseEffect {

	@Override
	public ByteString useItem(Player player, ItemDataIF itemData, int useCount, MsgItemBagResponse.Builder rsp) {
		int modelId = itemData.getModelId();
		ItemUseEffectTemplate tmp = ItemUseEffectCfgDAO.getCfgDAO().getUseEffectTemplateByModelId(modelId);

		int addVip = tmp.getDirectResult();

		String userId = player.getUserId();
		if (PrivilegeCfgDAO.getInstance().getCfg(addVip) == null) {
			GameLog.error("背包道具使用", userId, String.format("使用VIP卡的等级是[%s]并无PrivilegeCfg配置", addVip));
			rsp.setRspInfo(ItemBagHandler.fillResponseInfo(false, "当前没有VIP" + addVip));
			return rsp.build().toByteString();
		}

		int curVipLevel = player.getVip();
		if (addVip <= curVipLevel) {
			rsp.setRspInfo(ItemBagHandler.fillResponseInfo(false, "您已经是尊贵的VIP" + curVipLevel + "用户"));
			return rsp.build().toByteString();
		}

		ItemBagMgr itemBagMgr = ItemBagMgr.getInstance();

		List<IUseItem> useItemList = new ArrayList<IUseItem>();
		useItemList.add(new UseItem(itemData.getId(), useCount));

		Map<Integer, Integer> combineUseMap = tmp.getCombineUseMap();
		if (combineUseMap != null && !combineUseMap.isEmpty()) {

			Map<Integer, ItemData> modelFirstItemDataMap = itemBagMgr.getModelFirstItemDataMap(userId);

			for (Entry<Integer, Integer> e : combineUseMap.entrySet()) {
				int key = e.getKey();
				Integer value = e.getValue();
				if (key < eSpecialItemId.eSpecial_End.getValue()) {
					itemBagMgr.addItem(player, key, -value * useCount);
				} else {
					ItemData item = modelFirstItemDataMap.get(key);
					if (item == null) {
						GameLog.error("使用Vip卡道具", "Vip卡模版Id：" + modelId, "在背包中找不到要使用的物品");
						rsp.setRspInfo(ItemBagHandler.fillResponseInfo(false, "使用失败"));
						return rsp.build().toByteString();
					}
					useItemList.add(new UseItem(item.getId(), value * useCount));
				}
			}
		}

		// 使用道具
		if (!itemBagMgr.useLikeBoxItem(player, useItemList, null)) {
			rsp.setRspInfo(ItemBagHandler.fillResponseInfo(false, "使用失败"));
			return rsp.build().toByteString();
		}

		player.setVip(addVip);

		return rsp.build().toByteString();
	}
}