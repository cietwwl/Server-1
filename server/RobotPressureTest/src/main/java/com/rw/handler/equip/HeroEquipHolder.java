package com.rw.handler.equip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.dataSyn.SynDataListHolder;
import com.rwproto.DataSynProtos.MsgDataSyn;

/*
 * @author HC
 * @date 2016年4月1日 下午2:40:01
 * @Description 
 */
public class HeroEquipHolder {
	private Map<String, Map<Integer, EquipItem>> equipItemMap = new HashMap<String, Map<Integer, EquipItem>>();

	private SynDataListHolder<EquipItem> listHolder = new SynDataListHolder<EquipItem>(EquipItem.class);

	public void syn(MsgDataSyn msgDataSyn) {
		listHolder.Syn(msgDataSyn);

		// 更新数据
		List<EquipItem> itemList = listHolder.getItemList();
		for (int i = 0, size = itemList.size(); i < size; i++) {
			EquipItem equipItem = itemList.get(i);
			String ownerId = equipItem.getOwnerId();

			Map<Integer, EquipItem> map = equipItemMap.get(ownerId);
			if (map == null) {
				map = new HashMap<Integer, EquipItem>();
				equipItemMap.put(ownerId, map);
			}

			map.put(equipItem.getEquipIndex(), equipItem);
		}
	}

	/**
	 * 获取身上对应的装备
	 * 
	 * @param userId
	 * @param equipIndex
	 * @return
	 */
	public EquipItem getEquipItem(String userId, int equipIndex) {
		Map<Integer, EquipItem> map = equipItemMap.get(userId);
		if (map == null || map.isEmpty()) {
			return null;
		}

		return map.get(equipIndex);
	}

	/**
	 * 获取角色穿戴的装备
	 * 
	 * @param userId
	 * @return
	 */
	public Map<Integer, EquipItem> getHeroEquipItem(String userId) {
		return equipItemMap.get(userId);
	}
}