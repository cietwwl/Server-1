package com.rw.trace.parser;

import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.item.pojo.ItemData;
import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;

public class ItemDataParser implements DataValueParser<ItemData> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public ItemData copy(ItemData entity) {
        ItemData newData_ = new ItemData();
        newData_.setId(entity.getId());
        newData_.setModelId(entity.getModelId());
        newData_.setCount(entity.getCount());
        newData_.setUserId(entity.getUserId());
        newData_.setAllExtendAttr(writer.copyObject(entity.getAllExtendAttr()));
        return newData_;
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