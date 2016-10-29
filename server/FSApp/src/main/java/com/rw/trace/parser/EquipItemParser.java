package com.rw.trace.parser;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rwbase.dao.equipment.EquipItem;
import com.rwproto.ItemBagProtos.EItemTypeDef;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.alibaba.fastjson.JSONObject;

public class EquipItemParser implements DataValueParser<EquipItem> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public EquipItem copy(EquipItem entity) {
        EquipItem equipItemCopy = new EquipItem();
        equipItemCopy.setId(entity.getId());
        equipItemCopy.setOwnerId(entity.getOwnerId());
        equipItemCopy.setEquipIndex(entity.getEquipIndex());
        equipItemCopy.setType(writer.copyObject(entity.getType()));
        equipItemCopy.setModelId(entity.getModelId());
        equipItemCopy.setLevel(entity.getLevel());
        equipItemCopy.setExp(entity.getExp());
        return equipItemCopy;
    }

    @Override
    public JSONObject recordAndUpdate(EquipItem entity1, EquipItem entity2) {
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
        int equipIndex1 = entity1.getEquipIndex();
        int equipIndex2 = entity2.getEquipIndex();
        if (equipIndex1 != equipIndex2) {
            entity1.setEquipIndex(equipIndex2);
            jsonMap = writer.write(jsonMap, "equipIndex", equipIndex2);
        }
        EItemTypeDef type1 = entity1.getType();
        EItemTypeDef type2 = entity2.getType();
        if (type1 != type2) {
            entity1.setType(type2);
            jsonMap = writer.write(jsonMap, "type", type2);
        }
        int modelId1 = entity1.getModelId();
        int modelId2 = entity2.getModelId();
        if (modelId1 != modelId2) {
            entity1.setModelId(modelId2);
            jsonMap = writer.write(jsonMap, "modelId", modelId2);
        }
        int level1 = entity1.getLevel();
        int level2 = entity2.getLevel();
        if (level1 != level2) {
            entity1.setLevel(level2);
            jsonMap = writer.write(jsonMap, "level", level2);
        }
        int exp1 = entity1.getExp();
        int exp2 = entity2.getExp();
        if (exp1 != exp2) {
            entity1.setExp(exp2);
            jsonMap = writer.write(jsonMap, "exp", exp2);
        }
        return jsonMap;
    }

    @Override
    public boolean hasChanged(EquipItem entity1, EquipItem entity2) {
        if (!writer.equals(entity1.getId(), entity2.getId())) {
            return true;
        }
        if (!writer.equals(entity1.getOwnerId(), entity2.getOwnerId())) {
            return true;
        }
        if (entity1.getEquipIndex() != entity2.getEquipIndex()) {
            return true;
        }
        if (entity1.getType() != entity2.getType()) {
            return true;
        }
        if (entity1.getModelId() != entity2.getModelId()) {
            return true;
        }
        if (entity1.getLevel() != entity2.getLevel()) {
            return true;
        }
        if (entity1.getExp() != entity2.getExp()) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(EquipItem entity) {
        JSONObject json = new JSONObject(10);
        json.put("id", entity.getId());
        json.put("ownerId", entity.getOwnerId());
        json.put("equipIndex", entity.getEquipIndex());
        Object typeJson = writer.toJSON(entity.getType());
        if (typeJson != null) {
            json.put("type", typeJson);
        }
        json.put("modelId", entity.getModelId());
        json.put("level", entity.getLevel());
        json.put("exp", entity.getExp());
        return json;
    }

}