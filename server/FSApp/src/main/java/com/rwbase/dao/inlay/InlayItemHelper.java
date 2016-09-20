package com.rwbase.dao.inlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.role.InlayCfgDAO;
import com.rwbase.dao.role.pojo.InlayCfg;

public class InlayItemHelper {

	public static String getItemId(String ownerId, int modelId) {
		return ownerId + "_" + modelId;
	}

	public static InlayItem toInlayItem(String ownerIdP, ItemData itemData, int inlaySlot) {
		InlayItem inlayItem = new InlayItem();
		int modelId = itemData.getModelId();
		String inlayItemId = InlayItemHelper.getItemId(ownerIdP, modelId);
		inlayItem.setId(inlayItemId);
		inlayItem.setOwnerId(ownerIdP);
		inlayItem.setModelId(modelId);
		inlayItem.setSlotId(inlaySlot);

		return inlayItem;
	}

	final static private Map<Integer, Integer> openLvMap = new HashMap<Integer, Integer>();
	private static final Map<Integer, Integer> openCountOfLevel = new LinkedHashMap<Integer, Integer>();
	static {
		openLvMap.put(0, 10);
		openLvMap.put(1, 20);
		openLvMap.put(2, 30);
		openLvMap.put(3, 40);
		openLvMap.put(4, 50);
		openLvMap.put(5, 60);
		
		List<Integer> list = new ArrayList<Integer>(openLvMap.values());
		Collections.sort(list);
		
		for(int i = 0; i < list.size(); i++) {
			openCountOfLevel.put(list.get(i), 0);
		}
		
		for (Iterator<Map.Entry<Integer, Integer>> itr = openLvMap.entrySet().iterator(); itr.hasNext();) {
			Map.Entry<Integer, Integer> entry = itr.next();
			openCountOfLevel.put(entry.getValue(), entry.getKey() + 1);
		}
	}

	// public static AttrData getInlayAttrData(List<InlayItem> inlayList, String heroModelId) {
	// AttrData attrData = new AttrData();
	// Map<Integer, Integer> levelNumMap = new HashMap<Integer, Integer>();
	// for (InlayItem inlayItem : inlayList) {
	// Integer modelId = inlayItem.getModelId();
	// GemCfg cfg = ItemCfgHelper.getGemCfg(modelId);
	// if (cfg == null) {
	// continue;
	// }
	//
	// AttrData dataTmp = AttrData.fromObject(cfg);
	// attrData.plus(dataTmp);
	//
	// int level = cfg.getLevel();
	// Integer hasValue = levelNumMap.get(level);
	// if (hasValue == null) {
	// levelNumMap.put(level, 1);
	// } else {
	// levelNumMap.put(level, hasValue + 1);
	// }
	// }
	//
	// InlayCfg heroInlayCfg = InlayCfgDAO.getInstance().getConfig(heroModelId);
	// if (heroInlayCfg == null) {
	// return attrData;
	// }
	//
	// addHeroInlayExtraAttrValue(attrData, levelNumMap, heroInlayCfg.getExtraLv1(), heroInlayCfg.getExtraNum1(), heroInlayCfg.getExtraValue1());
	// addHeroInlayExtraAttrValue(attrData, levelNumMap, heroInlayCfg.getExtraLv2(), heroInlayCfg.getExtraNum2(), heroInlayCfg.getExtraValue2());
	// addHeroInlayExtraAttrValue(attrData, levelNumMap, heroInlayCfg.getExtraLv3(), heroInlayCfg.getExtraNum3(), heroInlayCfg.getExtraValue3());
	//
	// return attrData;
	// }

	// public static AttrData getPercentInlayAttrData(List<InlayItem> inlayList) {
	// AttrData attrData = new AttrData();
	// for (InlayItem inlayItem : inlayList) {
	// Integer modelId = inlayItem.getModelId();
	// GemCfg cfg = ItemCfgHelper.getGemCfg(modelId);
	// if (cfg != null) {
	// AttrData dataTmp = AttrData.fromPercentObjectToAttrData(cfg);
	// attrData.plus(dataTmp);
	// }
	// }
	// return attrData;
	// }
	//
	// private static void addHeroInlayExtraAttrValue(AttrData attrData, Map<Integer, Integer> levelNumMap, String extraLvs, int extraNum, String
	// extraValues) {
	// String[] lvArr = extraLvs.split(",");
	// String[] extraValue = extraValues.split(",");
	// String valueArr = null;
	// for (int j = 0; j < lvArr.length; j++) {
	// int lv = new Integer(lvArr[j]);
	// if (levelNumMap.containsKey(lv)) {
	// if (levelNumMap.get(lv) >= extraNum) {
	// // 如果有两个lv的值满足条件，取大的那个值
	// valueArr = extraValue[j];
	// }
	// }
	// }
	// if (valueArr != null) {
	// AttrData dataTmp = AttrData.fromCfgStr(valueArr);
	// attrData.plus(dataTmp);
	// }
	// }

	public static boolean isOpen(int modelId, int i, int level) {
		InlayCfg cfg = InlayCfgDAO.getInstance().getConfig(String.valueOf(modelId));
		if (cfg == null) {
			return level >= openLvMap.get(i);
		}
		int openLevel = Integer.valueOf(cfg.getOpenLv().split(",")[i]);
		return level >= openLevel;
	}
	
	public static int getOpenCount(int lv) {
		Map.Entry<Integer, Integer> entry;
		int count = 0;
		for(Iterator<Map.Entry<Integer, Integer>> itr = openCountOfLevel.entrySet().iterator(); itr.hasNext();) {
			entry = itr.next();
			if(entry.getKey() > lv) {
				break;
			} else {
				count = entry.getValue();
			}
		}
		return count;
	}

}
