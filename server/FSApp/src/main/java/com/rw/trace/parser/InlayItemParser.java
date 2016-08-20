package com.rw.trace.parser;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.alibaba.fastjson.JSONObject;
import com.rwbase.dao.inlay.InlayItem;

public class InlayItemParser implements DataValueParser<InlayItem> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public InlayItem copy(InlayItem entity) {
        InlayItem newData_ = new InlayItem();
        newData_.setId(entity.getId());
        newData_.setOwnerId(entity.getOwnerId());
        newData_.setSlotId(entity.getSlotId());
        newData_.setModelId(entity.getModelId());
        return newData_;
    }

    @Override
    public JSONObject recordAndUpdate(InlayItem entity1, InlayItem entity2) {
        JSONObject jsonMap = null;
        String id1 = entity1.getId();
        String id2 = entity2.getId();
        if (!writer.equals(id1, id2)) {
            entity1.setId(id2);
            jsonMap = writer.write(jsonMap, "id", id2);
        }
        String ownerId1 = entity1.getOwnerId();
        String ownerId2 = entity2.getOwnerId();
        if (!writer.equals(ownerId1, ownerId2)) {
            entity1.setOwnerId(ownerId2);
            jsonMap = writer.write(jsonMap, "ownerId", ownerId2);
        }
        int slotId1 = entity1.getSlotId();
        int slotId2 = entity2.getSlotId();
        if (slotId1 != slotId2) {
            entity1.setSlotId(slotId2);
            jsonMap = writer.write(jsonMap, "slotId", slotId2);
        }
        int modelId1 = entity1.getModelId();
        int modelId2 = entity2.getModelId();
        if (modelId1 != modelId2) {
            entity1.setModelId(modelId2);
            jsonMap = writer.write(jsonMap, "modelId", modelId2);
        }
        return jsonMap;
    }

    @Override
    public JSONObject toJson(InlayItem entity) {
        JSONObject json = new JSONObject(4);
        json.put("id", entity.getId());
        json.put("ownerId", entity.getOwnerId());
        json.put("slotId", entity.getSlotId());
        json.put("modelId", entity.getModelId());
        return json;
    }

}