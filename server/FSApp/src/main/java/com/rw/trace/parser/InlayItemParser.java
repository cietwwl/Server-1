package com.rw.trace.parser;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.inlay.InlayItem;
import com.alibaba.fastjson.JSONObject;

public class InlayItemParser implements DataValueParser<InlayItem> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public InlayItem copy(InlayItem entity) {
        InlayItem inlayItemCopy = new InlayItem();
        inlayItemCopy.setId(entity.getId());
        inlayItemCopy.setOwnerId(entity.getOwnerId());
        inlayItemCopy.setSlotId(entity.getSlotId());
        inlayItemCopy.setModelId(entity.getModelId());
        return inlayItemCopy;
    }

    @Override
    public JSONObject recordAndUpdate(InlayItem entity1, InlayItem entity2) {
        JSONObject jsonMap = null;
        Integer id1 = entity1.getId();
        Integer id2 = entity2.getId();
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
    public boolean hasChanged(InlayItem entity1, InlayItem entity2) {
        if (!writer.equals(entity1.getId(), entity2.getId())) {
            return true;
        }
        if (!writer.equals(entity1.getOwnerId(), entity2.getOwnerId())) {
            return true;
        }
        if (entity1.getSlotId() != entity2.getSlotId()) {
            return true;
        }
        if (entity1.getModelId() != entity2.getModelId()) {
            return true;
        }
        return false;
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