package com.rw.trace.parser;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.equipment.EquipItem;
import com.alibaba.fastjson.JSONObject;
import com.rwproto.ItemBagProtos.EItemTypeDef;

public class EquipItemParser implements DataValueParser<EquipItem> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public EquipItem copy(EquipItem entity) {
        EquipItem newData_ = new EquipItem();
        newData_.setId(entity.getId());
        newData_.setOwnerId(entity.getOwnerId());
        newData_.setEquipIndex(entity.getEquipIndex());
        newData_.setModelId(entity.getModelId());
        newData_.setLevel(entity.getLevel());
        newData_.setExp(entity.getExp());
        return newData_;
    }

    @Override
    public JSONObject recordAndUpdate(EquipItem entity1, EquipItem entity2) {
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
    public JSONObject toJson(EquipItem entity) {
        JSONObject json = new JSONObject(7);
        json.put("id", entity.getId());
        json.put("ownerId", entity.getOwnerId());
        json.put("equipIndex", entity.getEquipIndex());
        json.put("modelId", entity.getModelId());
        json.put("level", entity.getLevel());
        json.put("exp", entity.getExp());
        return json;
    }

}