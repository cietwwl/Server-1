package com.rw.trace.listener;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.trace.DataEventRecorder;
import com.rw.fsutil.dao.cache.trace.MapItemChangedEvent;
import com.rw.fsutil.dao.cache.trace.MapItemChangedListener;
import com.rw.service.log.BILogMgr;
import com.rwbase.dao.item.pojo.ItemData;

public class ItemDataListener implements MapItemChangedListener<ItemData> {

	@Override
	public void notifyDataChanged(MapItemChangedEvent<ItemData> event) {
		List<Pair<String,ItemData>> addList = event.getAddList();
		
		StringBuilder sbAdd = new StringBuilder();
		StringBuilder sbDel = new StringBuilder();
		
		if (addList != null) {
			for (Pair<String, ItemData> pair : addList) {
				ItemData data = pair.getT2();
				sbAdd.append(data.getModelId()).append(":").append(data.getCount()).append("&");
			}
		}
		List<Pair<String, ItemData>> delList = event.getRemoveList();
		if (delList != null) {
			for (Pair<String, ItemData> pair : delList) {
				ItemData data = pair.getT2();
				sbDel.append(data.getModelId()).append(":").append(data.getCount()).append("&");
			}
		}
		Map<String, Pair<ItemData, ItemData>> changedMap = event.getChangedMap();
		for (Pair<ItemData, ItemData> pair : changedMap.values()) {
			ItemData oldItem = pair.getT1();
			ItemData newItem = pair.getT2();
			int modelId1 = oldItem.getModelId();
			int count1 = oldItem.getCount();
			int modelId2 = newItem.getModelId();
			int count2 = newItem.getCount();
			if (modelId1 != modelId2 || count1 != count2) {
				System.out.println("更新道具：" + modelId1 + "=" + count1 + "," + modelId2 + "=" + count2);
				if (modelId1 != modelId2) {
					sbDel.append(modelId1).append(":").append(count1).append("&");
					sbAdd.append(modelId2).append(":").append(count2).append("&");
				} else {
					if (count1 != count2) {
						if (count1 > count2) {
							sbDel.append(modelId1).append(":").append(count1 - count2).append("&");
						} else {
							sbAdd.append(modelId2).append(":").append(count2 - count1).append("&");
						}
					}
				}
			}
		}
		
		List<Object> list = (List<Object>)DataEventRecorder.getParam();
		
		String strAdd = "";
		String strDel = "";
		if (sbAdd.toString().length() > 0)
			strAdd = sbAdd.substring(0, sbAdd.lastIndexOf("&"));
		if (sbDel.toString().length() > 0)
			strDel = sbDel.substring(0, sbDel.lastIndexOf("&"));
		if (strAdd.length() > 0 || strDel.length() > 0) {
			BILogMgr.getInstance().logItemChanged(list, strAdd.toString(), strDel.toString());
		}
	}
}
