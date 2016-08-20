package com.rw.trace.parser;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.alibaba.fastjson.JSONObject;

public class RoleBaseInfoParser implements DataValueParser<RoleBaseInfo> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public RoleBaseInfo copy(RoleBaseInfo entity) {
        RoleBaseInfo newData_ = new RoleBaseInfo();
        newData_.setId(entity.getId());
        newData_.setCareerType(entity.getCareerType());
        newData_.setTemplateId(entity.getTemplateId());
        newData_.setModeId(entity.getModeId());
        newData_.setLevel(entity.getLevel());
        newData_.setStarLevel(entity.getStarLevel());
        newData_.setQualityId(entity.getQualityId());
        newData_.setExp(entity.getExp());
        return newData_;
    }

    @Override
    public JSONObject recordAndUpdate(RoleBaseInfo entity1, RoleBaseInfo entity2) {
        JSONObject jsonMap = null;
        String id1 = entity1.getId();
        String id2 = entity2.getId();
        if (!writer.equals(id1, id2)) {
            entity1.setId(id2);
            jsonMap = writer.write(jsonMap, "id", id2);
        }
        int careerType1 = entity1.getCareerType();
        int careerType2 = entity2.getCareerType();
        if (careerType1 != careerType2) {
            entity1.setCareerType(careerType2);
            jsonMap = writer.write(jsonMap, "careerType", careerType2);
        }
        String templateId1 = entity1.getTemplateId();
        String templateId2 = entity2.getTemplateId();
        if (!writer.equals(templateId1, templateId2)) {
            entity1.setTemplateId(templateId2);
            jsonMap = writer.write(jsonMap, "templateId", templateId2);
        }
        int modeId1 = entity1.getModeId();
        int modeId2 = entity2.getModeId();
        if (modeId1 != modeId2) {
            entity1.setModeId(modeId2);
            jsonMap = writer.write(jsonMap, "modeId", modeId2);
        }
        int level1 = entity1.getLevel();
        int level2 = entity2.getLevel();
        if (level1 != level2) {
            entity1.setLevel(level2);
            jsonMap = writer.write(jsonMap, "level", level2);
        }
        int starLevel1 = entity1.getStarLevel();
        int starLevel2 = entity2.getStarLevel();
        if (starLevel1 != starLevel2) {
            entity1.setStarLevel(starLevel2);
            jsonMap = writer.write(jsonMap, "starLevel", starLevel2);
        }
        String qualityId1 = entity1.getQualityId();
        String qualityId2 = entity2.getQualityId();
        if (!writer.equals(qualityId1, qualityId2)) {
            entity1.setQualityId(qualityId2);
            jsonMap = writer.write(jsonMap, "qualityId", qualityId2);
        }
        long exp1 = entity1.getExp();
        long exp2 = entity2.getExp();
        if (exp1 != exp2) {
            entity1.setExp(exp2);
            jsonMap = writer.write(jsonMap, "exp", exp2);
        }
        return jsonMap;
    }

    @Override
    public JSONObject toJson(RoleBaseInfo entity) {
        JSONObject json = new JSONObject(8);
        json.put("id", entity.getId());
        json.put("careerType", entity.getCareerType());
        json.put("templateId", entity.getTemplateId());
        json.put("modeId", entity.getModeId());
        json.put("level", entity.getLevel());
        json.put("starLevel", entity.getStarLevel());
        json.put("qualityId", entity.getQualityId());
        json.put("exp", entity.getExp());
        return json;
    }

}