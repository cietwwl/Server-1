package com.rw.trace.support;

import com.rwbase.dao.copy.pojo.ItemInfo;
import java.util.List;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import java.util.Map;
import com.rw.service.dropitem.DropResult;
import com.alibaba.fastjson.JSONObject;
import com.rwbase.dao.dropitem.DropAdjustmentState;

public class DropResultParser implements DataValueParser<DropResult> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public DropResult copy(DropResult entity) {
        DropResult newData_ = new DropResult();
        newData_.setItemInfos(writer.copyObject(entity.getItemInfos()));
        newData_.setDropRuleMap(writer.copyObject(entity.getDropRuleMap()));
        newData_.setCreateTimeMillis(entity.getCreateTimeMillis());
        newData_.setFirstDrop(entity.isFirstDrop());
        return newData_;
    }

    @Override
    public JSONObject recordAndUpdate(DropResult entity1, DropResult entity2) {
        JSONObject jsonMap = null;
        List<ItemInfo> itemInfos1 = entity1.getItemInfos();
        List<ItemInfo> itemInfos2 = entity2.getItemInfos();
        Pair<List<ItemInfo>, JSONObject> itemInfosPair = writer.checkObject(jsonMap, "itemInfos", itemInfos1, itemInfos2);
        if (itemInfosPair != null) {
            itemInfos1 = itemInfosPair.getT1();
            entity1.setItemInfos(itemInfos1);
            jsonMap = itemInfosPair.getT2();
        } else {
            jsonMap = writer.compareSetDiff(jsonMap, "itemInfos", itemInfos1, itemInfos2);
        }

        Map<Integer, DropAdjustmentState> dropRuleMap1 = entity1.getDropRuleMap();
        Map<Integer, DropAdjustmentState> dropRuleMap2 = entity2.getDropRuleMap();
        Pair<Map<Integer, DropAdjustmentState>, JSONObject> dropRuleMapPair = writer.checkObject(jsonMap, "dropRuleMap", dropRuleMap1, dropRuleMap2);
        if (dropRuleMapPair != null) {
            dropRuleMap1 = dropRuleMapPair.getT1();
            entity1.setDropRuleMap(dropRuleMap1);
            jsonMap = dropRuleMapPair.getT2();
        } else {
            jsonMap = writer.compareSetDiff(jsonMap, "dropRuleMap", dropRuleMap1, dropRuleMap2);
        }

        long createTimeMillis1 = entity1.getCreateTimeMillis();
        long createTimeMillis2 = entity2.getCreateTimeMillis();
        if (createTimeMillis1 != createTimeMillis2) {
            entity1.setCreateTimeMillis(createTimeMillis2);
            jsonMap = writer.write(jsonMap, "createTimeMillis", createTimeMillis2);
        }
        boolean firstDrop1 = entity1.isFirstDrop();
        boolean firstDrop2 = entity2.isFirstDrop();
        if (firstDrop1 != firstDrop2) {
            entity1.setFirstDrop(firstDrop2);
            jsonMap = writer.write(jsonMap, "firstDrop", firstDrop2);
        }
        return jsonMap;
    }

    @Override
    public JSONObject toJson(DropResult entity) {
        JSONObject json = new JSONObject(4);
        Object itemInfosJson = writer.toJSON(entity.getItemInfos());
        if (itemInfosJson != null) {
            json.put("itemInfos", itemInfosJson);
        }
        Object dropRuleMapJson = writer.toJSON(entity.getDropRuleMap());
        if (dropRuleMapJson != null) {
            json.put("dropRuleMap", dropRuleMapJson);
        }
        json.put("createTimeMillis", entity.getCreateTimeMillis());
        json.put("firstDrop", entity.isFirstDrop());
        return json;
    }

}