package com.rw.trace;

import java.util.HashMap;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.item.pojo.ItemData;

public class ItemDataParser implements DataValueParser<ItemData> {

	private JsonValueWriter writer = JsonValueWriter.getInstance();

	@Override
	public ItemData copy(ItemData entity) {
		ItemData newData = new ItemData();
		newData.setCount(entity.getCount());
		newData.setId(entity.getId());
		newData.setModelId(entity.getModelId());
		newData.setUserId(entity.getUserId());
		newData.setAllExtendAttr(writer.copyObject(entity.getAllExtendAttr()));
		return newData;
	}

	@Override
	public JSONObject recordAndUpdate(ItemData entity1, ItemData entity2) {
		JSONObject map = null;
		int count1 = entity1.getCount();
		int count2 = entity2.getCount();
		if (count1 != count2) {
			entity1.setCount(count2);
			map = writer.write(map, "count", count2);
		}
		int modelId1 = entity1.getModelId();
		int modelId2 = entity2.getModelId();
		if (modelId1 != modelId2) {
			entity1.setCount(modelId2);
			map = writer.write(map, "modelId", modelId2);
		}
		String id1 = entity1.getId();
		String id2 = entity2.getId();
		if (!StringUtils.equals(id1, id2)) {
			entity1.setId(id2);
			map = writer.write(map, "id", id2);
		}
		String userId1 = entity1.getUserId();
		String userId2 = entity2.getUserId();
		if (!StringUtils.equals(userId1, userId2)) {
			entity1.setUserId(userId2);
			map = writer.write(map, "userId", userId2);
		}

		HashMap<Integer, String> allExtendAttr1 = entity1.getAllExtendAttr();
		HashMap<Integer, String> allExtendAttr2 = entity2.getAllExtendAttr();
		Pair<HashMap<Integer, String>, JSONObject> pair = writer.checkObject(map, "allExtendAttr", allExtendAttr1, allExtendAttr2);
		if (pair != null) {
			allExtendAttr1 = pair.getT1();
			entity1.setAllExtendAttr(allExtendAttr1);
			map = pair.getT2();
		} else {
			map = writer.compareSetDiff(map, "allExtendAttr", allExtendAttr1, allExtendAttr2);
		}
		return map;
	}

	@Override
	public JSONObject toJson(ItemData entity) {
		JSONObject json = new JSONObject(8);
		json.put("count", entity.getCount());
		json.put("modelId", entity.getModelId());
		json.put("id", entity.getId());
		json.put("userId", entity.getUserId());
		Object allExtendAttrJson = writer.toJSON(entity.getAllExtendAttr());
		if (allExtendAttrJson != null) {
			json.put("allExtendAttr", allExtendAttrJson);
		}
		return json;
	}

}
