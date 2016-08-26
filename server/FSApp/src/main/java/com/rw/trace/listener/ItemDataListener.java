package com.rw.trace.listener;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.trace.MapItemChangedEvent;
import com.rw.fsutil.dao.cache.trace.MapItemChangedListener;
import com.rwbase.dao.item.pojo.ItemData;

/**
 * {@link MapItemChangedListener}示例文件
 * @author Jamaz
 *
 */
public class ItemDataListener implements MapItemChangedListener<ItemData> {

	@Override
	public void notifyDataChanged(MapItemChangedEvent<ItemData> event) {
		List<Pair<String, ItemData>> addList = event.getAddList();
		if (addList != null) {
			for (Pair<String, ItemData> data : addList) {
				System.out.println("新增道具：" + data.getT2().getModelId() + "," + data.getT2().getCount());
			}
		}
		List<Pair<String, ItemData>> delList = event.getRemoveList();
		if (delList != null) {
			for (Pair<String, ItemData> data : delList) {
				System.out.println("删除道具：" + data.getT2().getModelId() + "," + data.getT2().getCount());
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
			}
		}
	}

}