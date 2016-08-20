package com.rw.trace.parser;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.alibaba.fastjson.JSONObject;
import com.rwbase.dao.skill.pojo.Skill;

public class SkillParser implements DataValueParser<Skill> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public Skill copy(Skill entity) {
        Skill newData_ = new Skill();
        newData_.setId(entity.getId());
        newData_.setOwnerId(entity.getOwnerId());
        newData_.setSkillId(entity.getSkillId());
        newData_.setLevel(entity.getLevel());
        newData_.setOrder(entity.getOrder());
        return newData_;
    }

    @Override
    public JSONObject recordAndUpdate(Skill entity1, Skill entity2) {
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
    public JSONObject toJson(Skill entity) {
        JSONObject json = new JSONObject(5);
        json.put("id", entity.getId());
        json.put("ownerId", entity.getOwnerId());
        json.put("skillId", entity.getSkillId());
        json.put("level", entity.getLevel());
        json.put("order", entity.getOrder());
        return json;
    }

}