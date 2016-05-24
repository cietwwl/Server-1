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
import com.rwbase.dao.item.pojo.ItemUseEffectTemplate;
import com.rwbase.dao.item.pojo.itembase.IUseItem;
import com.rwbase.dao.item.pojo.itembase.UseItem;
import com.rwbase.dao.power.RoleUpgradeCfgDAO;
import com.rwbase.dao.power.pojo.RoleUpgradeCfg;
import com.rwproto.ItemBagProtos.MsgItemBagResponse;

/*
 * @author HC
 * @date 2016年5月18日 上午10:31:52
 * @Description 使用体力卡的使用效果
 */
public class UsePowerCardEffectImpl implements IItemUseEffect {

	@Override
	public ByteString useItem(Player player, ItemDataIF itemData, int useCount, MsgItemBagResponse.Builder rsp) {
		RoleUpgradeCfg roleUpgradeCfg = RoleUpgradeCfgDAO.getInstance().getCfg(player.getLevel());
		if (roleUpgradeCfg == null) {
			GameLog.error("背包道具使用", player.getUserId(), String.format("角色的等级为[%s]的RoleUpgradeCfg模版找不到", player.getLevel()));
			rsp.setRspInfo(ItemBagHandler.fillResponseInfo(false, "数据异常"));
			return rsp.build().toByteString();
		}

		int modelId = itemData.getModelId();
		ItemUseEffectTemplate tmp = ItemUseEffectCfgDAO.getCfgDAO().getUseEffectTemplateByModelId(modelId);

		int addPower = tmp.getDirectResult();

		int mostPower = roleUpgradeCfg.getMostPower();
		if (player.getUserGameDataMgr().getPower() >= mostPower) {// 超过上限
			rsp.setRspInfo(ItemBagHandler.fillResponseInfo(false, "体力已达上限"));
			return rsp.build().toByteString();
		}

		ItemBagMgr itemBagMgr = player.getItemBagMgr();

		List<IUseItem> useItemList = new ArrayList<IUseItem>();
		useItemList.add(new UseItem(itemData.getId(), useCount));

		Map<Integer, Integer> combineUseMap = tmp.getCombineUseMap();
		if (combineUseMap != null && !combineUseMap.isEmpty()) {
			for (Entry<Integer, Integer> e : combineUseMap.entrySet()) {
				int key = e.getKey();
				Integer value = e.getValue();
				if (key < eSpecialItemId.eSpecial_End.getValue()) {
					itemBagMgr.addItem(key, -value * useCount);
				} else {
					useItemList.add(new UseItem(itemBagMgr.getFirstItemByModelId(modelId).getId(), value * useCount));
				}
			}
		}

		// 使用道具
		if (!itemBagMgr.useLikeBoxItem(useItemList, null)) {
			rsp.setRspInfo(ItemBagHandler.fillResponseInfo(false, "使用失败"));
			return rsp.build().toByteString();
		}

		player.addPower(addPower);

		return rsp.build().toByteString();
	}
}