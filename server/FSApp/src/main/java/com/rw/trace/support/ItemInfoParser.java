package com.rw.trace.support;

import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.alibaba.fastjson.JSONObject;

public class ItemInfoParser implements DataValueParser<ItemInfo> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public ItemInfo copy(ItemInfo entity) {
        ItemInfo newData_ = new ItemInfo();
        newData_.setItemID(entity.getItemID());
        newData_.setItemNum(entity.getItemNum());
        return newData_;
    }

    @Override
    public JSONObject recordAndUpdate(ItemInfo entity1, ItemInfo entity2) {
        JSONObject jsonMap = null;
        int itemID1 = entity1.getItemID();
        int itemID2 = entity2.getItemID();
        if (itemID1 != itemID2) {
            entity1.setItemID(itemID2);
            jsonMap = writer.write(jsonMap, "itemID", itemID2);
        }
        int itemNum1 = entity1.getItemNum();
        int itemNum2 = entity2.getItemNum();
        if (itemNum1 != itemNum2) {
            entity1.setItemNum(itemNum2);
            jsonMap = writer.write(jsonMap, "itemNum", itemNum2);
        }
        return jsonMap;
    }

    @Override
    public JSONObject toJson(ItemInfo entity) {
        JSONObject json = new JSONObject(2);
        json.put("itemID", entity.getItemID());
        json.put("itemNum", entity.getItemNum());
        return json;
    }

}