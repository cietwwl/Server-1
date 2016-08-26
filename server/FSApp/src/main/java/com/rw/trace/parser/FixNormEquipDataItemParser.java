package com.rw.trace.parser;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.alibaba.fastjson.JSONObject;

public class FixNormEquipDataItemParser implements DataValueParser<FixNormEquipDataItem> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public FixNormEquipDataItem copy(FixNormEquipDataItem entity) {
        FixNormEquipDataItem fixNormEquipDataItemCopy = new FixNormEquipDataItem();
        fixNormEquipDataItemCopy.setId(entity.getId());
        fixNormEquipDataItemCopy.setOwnerId(entity.getOwnerId());
        fixNormEquipDataItemCopy.setCfgId(entity.getCfgId());
        fixNormEquipDataItemCopy.setLevel(entity.getLevel());
        fixNormEquipDataItemCopy.setQuality(entity.getQuality());
        fixNormEquipDataItemCopy.setStar(entity.getStar());
        fixNormEquipDataItemCopy.setSlot(entity.getSlot());
        return fixNormEquipDataItemCopy;
    }

    @Override
    public JSONObject recordAndUpdate(FixNormEquipDataItem entity1, FixNormEquipDataItem entity2) {
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
        String cfgId1 = entity1.getCfgId();
        String cfgId2 = entity2.getCfgId();
        if (!writer.equals(cfgId1, cfgId2)) {
            entity1.setCfgId(cfgId2);
            jsonMap = writer.write(jsonMap, "cfgId", cfgId2);
        }
        int level1 = entity1.getLevel();
        int level2 = entity2.getLevel();
        if (level1 != level2) {
            entity1.setLevel(level2);
            jsonMap = writer.write(jsonMap, "level", level2);
        }
        int quality1 = entity1.getQuality();
        int quality2 = entity2.getQuality();
        if (quality1 != quality2) {
            entity1.setQuality(quality2);
            jsonMap = writer.write(jsonMap, "quality", quality2);
        }
        int star1 = entity1.getStar();
        int star2 = entity2.getStar();
        if (star1 != star2) {
            entity1.setStar(star2);
            jsonMap = writer.write(jsonMap, "star", star2);
        }
        int slot1 = entity1.getSlot();
        int slot2 = entity2.getSlot();
        if (slot1 != slot2) {
            entity1.setSlot(slot2);
            jsonMap = writer.write(jsonMap, "slot", slot2);
        }
        return jsonMap;
    }

    @Override
    public boolean hasChanged(FixNormEquipDataItem entity1, FixNormEquipDataItem entity2) {
        if (!writer.equals(entity1.getId(), entity2.getId())) {
            return true;
        }
        if (!writer.equals(entity1.getOwnerId(), entity2.getOwnerId())) {
            return true;
        }
        if (!writer.equals(entity1.getCfgId(), entity2.getCfgId())) {
            return true;
        }
        if (entity1.getLevel() != entity2.getLevel()) {
            return true;
        }
        if (entity1.getQuality() != entity2.getQuality()) {
            return true;
        }
        if (entity1.getStar() != entity2.getStar()) {
            return true;
        }
        if (entity1.getSlot() != entity2.getSlot()) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(FixNormEquipDataItem entity) {
        JSONObject json = new JSONObject(7);
        json.put("id", entity.getId());
        json.put("ownerId", entity.getOwnerId());
        json.put("cfgId", entity.getCfgId());
        json.put("level", entity.getLevel());
        json.put("quality", entity.getQuality());
        json.put("star", entity.getStar());
        json.put("slot", entity.getSlot());
        return json;
    }

}