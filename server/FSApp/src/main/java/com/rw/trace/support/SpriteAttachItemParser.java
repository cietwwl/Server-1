package com.rw.trace.support;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rwbase.dao.spriteattach.SpriteAttachItem;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.alibaba.fastjson.JSONObject;

public class SpriteAttachItemParser implements DataValueParser<SpriteAttachItem> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public SpriteAttachItem copy(SpriteAttachItem entity) {
        SpriteAttachItem spriteAttachItemCopy = new SpriteAttachItem();
        spriteAttachItemCopy.setSpriteAttachId(entity.getSpriteAttachId());
        spriteAttachItemCopy.setLevel(entity.getLevel());
        spriteAttachItemCopy.setExp(entity.getExp());
        spriteAttachItemCopy.setIndex(entity.getIndex());
        return spriteAttachItemCopy;
    }

    @Override
    public JSONObject recordAndUpdate(SpriteAttachItem entity1, SpriteAttachItem entity2) {
        JSONObject jsonMap = null;
        int spriteAttachId1 = entity1.getSpriteAttachId();
        int spriteAttachId2 = entity2.getSpriteAttachId();
        if (spriteAttachId1 != spriteAttachId2) {
            entity1.setSpriteAttachId(spriteAttachId2);
            jsonMap = writer.write(jsonMap, "spriteAttachId", spriteAttachId2);
        }
        int level1 = entity1.getLevel();
        int level2 = entity2.getLevel();
        if (level1 != level2) {
            entity1.setLevel(level2);
            jsonMap = writer.write(jsonMap, "level", level2);
        }
        long exp1 = entity1.getExp();
        long exp2 = entity2.getExp();
        if (exp1 != exp2) {
            entity1.setExp(exp2);
            jsonMap = writer.write(jsonMap, "exp", exp2);
        }
        int index1 = entity1.getIndex();
        int index2 = entity2.getIndex();
        if (index1 != index2) {
            entity1.setIndex(index2);
            jsonMap = writer.write(jsonMap, "index", index2);
        }
        return jsonMap;
    }

    @Override
    public boolean hasChanged(SpriteAttachItem entity1, SpriteAttachItem entity2) {
        if (entity1.getSpriteAttachId() != entity2.getSpriteAttachId()) {
            return true;
        }
        if (entity1.getLevel() != entity2.getLevel()) {
            return true;
        }
        if (entity1.getExp() != entity2.getExp()) {
            return true;
        }
        if (entity1.getIndex() != entity2.getIndex()) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(SpriteAttachItem entity) {
        JSONObject json = new JSONObject(6);
        json.put("spriteAttachId", entity.getSpriteAttachId());
        json.put("level", entity.getLevel());
        json.put("exp", entity.getExp());
        json.put("index", entity.getIndex());
        return json;
    }

}