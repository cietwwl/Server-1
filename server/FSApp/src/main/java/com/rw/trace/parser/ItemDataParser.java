package com.rw.trace.parser;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.item.pojo.ItemData;
import com.rw.fsutil.common.Pair;
import java.util.HashMap;
import com.alibaba.fastjson.JSONObject;

public class ItemDataParser implements DataValueParser<ItemData> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public ItemData copy(ItemData entity) {
        ItemData itemDataCopy = new ItemData();
        itemDataCopy.setId(entity.getId());
        itemDataCopy.setModelId(entity.getModelId());
        itemDataCopy.setCount(entity.getCount());
        itemDataCopy.setUserId(entity.getUserId());
        itemDataCopy.setAllExtendAttr(writer.copyObject(entity.getAllExtendAttr()));
        return itemDataCopy;
    }

    @Override
    public JSONObject recordAndUpdate(ItemData entity1, ItemData entity2) {
        JSONObject jsonMap = null;
        String id1 = entity1.getId();
        String id2 = entity2.getId();
        if (!writer.equals(id1, id2)) {
            entity1.setId(id2);
            jsonMap = writer.write(jsonMap, "id", id2);
        }
        int modelId1 = entity1.getModelId();
        int modelId2 = entity2.getModelId();
        if (modelId1 != modelId2) {
            entity1.setModelId(modelId2);
            jsonMap = writer.write(jsonMap, "modelId", modelId2);
        }
        int count1 = entity1.getCount();
        int count2 = entity2.getCount();
        if (count1 != count2) {
            entity1.setCount(count2);
            jsonMap = writer.write(jsonMap, "count", count2);
        }
        String userId1 = entity1.getUserId();
        String userId2 = entity2.getUserId();
        if (!writer.equals(userId1, userId2)) {
            entity1.setUserId(userId2);
            jsonMap = writer.write(jsonMap, "userId", userId2);
        }
        HashMap<Integer, String> allExtendAttr1 = entity1.getAllExtendAttr();
        HashMap<Integer, String> allExtendAttr2 = entity2.getAllExtendAttr();
        Pair<HashMap<Integer, String>, JSONObject> allExtendAttrPair = writer.checkObject(jsonMap, "allExtendAttr", allExtendAttr1, allExtendAttr2);
        if (allExtendAttrPair != null) {
            allExtendAttr1 = allExtendAttrPair.getT1();
            entity1.setAllExtendAttr(allExtendAttr1);
            jsonMap = allExtendAttrPair.getT2();
        } else {
            jsonMap = writer.compareSetDiff(jsonMap, "allExtendAttr", allExtendAttr1, allExtendAttr2);
        }

        return jsonMap;
    }

    @Override
    public boolean hasChanged(ItemData entity1, ItemData entity2) {
        if (!writer.equals(entity1.getId(), entity2.getId())) {
            return true;
        }
        if (entity1.getModelId() != entity2.getModelId()) {
            return true;
        }
        if (entity1.getCount() != entity2.getCount()) {
            return true;
        }
        if (!writer.equals(entity1.getUserId(), entity2.getUserId())) {
            return true;
        }
        if (writer.hasChanged(entity1.getAllExtendAttr(), entity2.getAllExtendAttr())) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(ItemData entity) {
        JSONObject json = new JSONObject(5);
        json.put("id", entity.getId());
        json.put("modelId", entity.getModelId());
        json.put("count", entity.getCount());
        json.put("userId", entity.getUserId());
        Object allExtendAttrJson = writer.toJSON(entity.getAllExtendAttr());
        if (allExtendAttrJson != null) {
            json.put("allExtendAttr", allExtendAttrJson);
        }
        return json;
    }

}