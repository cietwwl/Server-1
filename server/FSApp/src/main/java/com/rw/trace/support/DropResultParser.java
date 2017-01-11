package com.rw.trace.support;

import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import java.util.List;
import com.rw.service.dropitem.DropResult;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.fsutil.common.Pair;
import com.alibaba.fastjson.JSONObject;

public class DropResultParser implements DataValueParser<DropResult> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public DropResult copy(DropResult entity) {
        DropResult dropResultCopy = new DropResult();
        dropResultCopy.setItemInfos(writer.copyObject(entity.getItemInfos()));
        dropResultCopy.setCreateTimeMillis(entity.getCreateTimeMillis());
        dropResultCopy.setFirstDrop(entity.isFirstDrop());
        return dropResultCopy;
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
    public boolean hasChanged(DropResult entity1, DropResult entity2) {
        if (writer.hasChanged(entity1.getItemInfos(), entity2.getItemInfos())) {
            return true;
        }
        if (entity1.getCreateTimeMillis() != entity2.getCreateTimeMillis()) {
            return true;
        }
        if (entity1.isFirstDrop() != entity2.isFirstDrop()) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(DropResult entity) {
        JSONObject json = new JSONObject(5);
        Object itemInfosJson = writer.toJSON(entity.getItemInfos());
        if (itemInfosJson != null) {
            json.put("itemInfos", itemInfosJson);
        }
        json.put("createTimeMillis", entity.getCreateTimeMillis());
        json.put("firstDrop", entity.isFirstDrop());
        return json;
    }

}