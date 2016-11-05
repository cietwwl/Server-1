package com.rw.service.redpoint.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.common.RefInt;
import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.common.enu.eConsumeTypeDef;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.item.ConsumeCfgDAO;
import com.rwbase.dao.item.ItemUseEffectCfgDAO;
import com.rwbase.dao.item.pojo.ConsumeCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.ItemUseEffectTemplate;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwproto.ItemBagProtos.EItemTypeDef;

/**
 * @Author HC
 * @date 2016年11月5日 下午4:32:04
 * @desc 背包红点
 **/

public class ItemBagCollector implements RedPointCollector {
	private ItemUseEffectCfgDAO useEffectDAO = ItemUseEffectCfgDAO.getCfgDAO();
	private ConsumeCfgDAO consumeCfgDAO = ConsumeCfgDAO.getInstance();

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		List<ItemData> itemList = player.getItemBagMgr().getItemListByType(EItemTypeDef.Consume);
		if (itemList.isEmpty()) {
			return;
		}

		int playerLevel = player.getLevel();

		Map<Integer, RefInt> modelCountMap = player.getItemBagMgr().getModelCountMap();

		List<String> boxIdList = new ArrayList<String>();

		for (int i = 0, size = itemList.size(); i < size; i++) {
			ItemData itemData = itemList.get(i);
			int modelId = itemData.getModelId();

			ConsumeCfg consumeCfg = consumeCfgDAO.getCfgById(String.valueOf(modelId));
			if (consumeCfg == null) {
				continue;
			}

			if (consumeCfg.getConsumeType() != eConsumeTypeDef.treasureBox.getOrder()) {
				continue;
			}

			if (consumeCfg.getUseLevel() > playerLevel) {
				continue;
			}

			if (canUse(player, modelId, modelCountMap)) {
				boxIdList.add(itemData.getId());
			}
		}

		if (!boxIdList.isEmpty()) {
			map.put(RedPointType.ITEM_BAG_CONSUME_BTN, boxIdList);
		}
	}

	@Override
	public eOpenLevelType getOpenType() {
		return null;
	}

	/**
	 * 是否可以使用
	 * 
	 * @param player
	 * @param modelId
	 * @param modelCountMap
	 * @return
	 */
	private boolean canUse(Player player, int modelId, Map<Integer, RefInt> modelCountMap) {
		ItemUseEffectTemplate tmp = useEffectDAO.getUseEffectTemplateByModelId(modelId);

		Map<Integer, Integer> combineUseMap = tmp.getCombineUseMap();
		if (combineUseMap != null && !combineUseMap.isEmpty()) {
			for (Entry<Integer, Integer> e : combineUseMap.entrySet()) {
				int key = e.getKey();
				Integer value = e.getValue();

				if (value <= 0) {
					continue;
				}

				if (key < eSpecialItemId.eSpecial_End.getValue()) {
					long count = player.getReward(eSpecialItemId.getDef(key));
					if (value > count) {
						return false;
					}
				} else {
					int count = modelCountMap.get(key).value;
					if (value > count) {
						return false;
					}
				}
			}
		}

		return true;
	}
}