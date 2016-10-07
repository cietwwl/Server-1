package com.rw.trace.parser;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.skill.pojo.SkillItem;
import com.alibaba.fastjson.JSONObject;

public class SkillItemParser implements DataValueParser<SkillItem> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public SkillItem copy(SkillItem entity) {
        SkillItem skillItemCopy = new SkillItem();
        skillItemCopy.setId(entity.getId());
        skillItemCopy.setOwnerId(entity.getOwnerId());
        skillItemCopy.setSkillId(entity.getSkillId());
        skillItemCopy.setLevel(entity.getLevel());
        skillItemCopy.setOrder(entity.getOrder());
        return skillItemCopy;
    }

    @Override
    public JSONObject recordAndUpdate(SkillItem entity1, SkillItem entity2) {
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
        String skillId1 = entity1.getSkillId();
        String skillId2 = entity2.getSkillId();
        if (!writer.equals(skillId1, skillId2)) {
            entity1.setSkillId(skillId2);
            jsonMap = writer.write(jsonMap, "skillId", skillId2);
        }
        int level1 = entity1.getLevel();
        int level2 = entity2.getLevel();
        if (level1 != level2) {
            entity1.setLevel(level2);
            jsonMap = writer.write(jsonMap, "level", level2);
        }
        int order1 = entity1.getOrder();
        int order2 = entity2.getOrder();
        if (order1 != order2) {
            entity1.setOrder(order2);
            jsonMap = writer.write(jsonMap, "order", order2);
        }
        return jsonMap;
    }

    @Override
    public boolean hasChanged(SkillItem entity1, SkillItem entity2) {
        if (!writer.equals(entity1.getId(), entity2.getId())) {
            return true;
        }
        if (!writer.equals(entity1.getOwnerId(), entity2.getOwnerId())) {
            return true;
        }
        if (!writer.equals(entity1.getSkillId(), entity2.getSkillId())) {
            return true;
        }
        if (entity1.getLevel() != entity2.getLevel()) {
            return true;
        }
        if (entity1.getOrder() != entity2.getOrder()) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(SkillItem entity) {
        JSONObject json = new JSONObject(5);
        json.put("id", entity.getId());
        json.put("ownerId", entity.getOwnerId());
        json.put("skillId", entity.getSkillId());
        json.put("level", entity.getLevel());
        json.put("order", entity.getOrder());
        return json;
    }

}