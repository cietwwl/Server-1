package com.rw.trace;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.ChangedRecord;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.item.pojo.ItemData;

public class ItemDataParser implements DataValueParser<ItemData> {

	private JsonValueWriter valueWriter = JsonValueWriter.getInstance();

	@Override
	public ItemData copy(ItemData entity) {
		ItemData newData = new ItemData();
		newData.setCount(entity.getCount());
		newData.setId(entity.getId());
		newData.setModelId(entity.getModelId());
		newData.setUserId(entity.getUserId());
		HashMap<Integer, String> map = entity.getAllExtendAttr();
		HashMap<Integer, String> newMap = new HashMap<Integer, String>(map.size());
		for (Map.Entry<Integer, String> entry : map.entrySet()) {
			newMap.put(entry.getKey(), entry.getValue());
		}
		newData.setExtendAttr(newMap);
		return newData;
	}

	@Override
	public Map<String, ChangedRecord> compareDiff(ItemData entity1, ItemData entity2) {
		HashMap<String, ChangedRecord> map = null;
		map = valueWriter.write(map, "count", entity1.getCount(), entity2.getCount());
		map = valueWriter.write(map, "modelId", entity1.getModelId(), entity2.getModelId());
		map = valueWriter.write(map, "id", entity1.getId(), entity2.getId());
		map = valueWriter.write(map, "userId", entity1.getUserId(), entity2.getUserId());
		map = valueWriter.write(map, "allExtendAttr", entity1.getAllExtendAttr(), entity2.getAllExtendAttr());
		return map;
	}

	@Override
	public JSONObject toJson(ItemData entity) {
		JSONObject json = new JSONObject(8);
		json.put("count", entity.getCount());
		json.put("modelId", entity.getModelId());
		json.put("id", entity.getId());
		json.put("userId", entity.getUserId());
		HashMap<Integer, String> allExtendAttr = entity.getAllExtendAttr();
		if (allExtendAttr != null && !allExtendAttr.isEmpty()) {
			JSONObject allExtendAttrJson = new JSONObject();
			for (Map.Entry<Integer, String> entry : allExtendAttr.entrySet()) {
				allExtendAttrJson.put(valueWriter.getIntString(entry.getKey()), entry.getValue());
			}
			json.put("allExtendAttr", allExtendAttrJson);
		}
		return json;
	}
}
