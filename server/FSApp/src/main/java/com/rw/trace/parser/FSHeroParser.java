package com.rw.trace.parser;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.playerdata.hero.core.FSHero;
import com.alibaba.fastjson.JSONObject;

public class FSHeroParser implements DataValueParser<FSHero> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public FSHero copy(FSHero entity) {
        FSHero fSHeroCopy = new FSHero();
        fSHeroCopy.setTemplateId(entity.getTemplateId());
        fSHeroCopy.setExp(entity.getExp());
        fSHeroCopy.setLevel(entity.getLevel());
        fSHeroCopy.setStarLevel(entity.getStarLevel());
        fSHeroCopy.setQualityId(entity.getQualityId());
        fSHeroCopy.setCareerType(entity.getCareerType());
        return fSHeroCopy;
    }

    @Override
    public JSONObject recordAndUpdate(FSHero entity1, FSHero entity2) {
        JSONObject jsonMap = null;
        String templateId1 = entity1.getTemplateId();
        String templateId2 = entity2.getTemplateId();
        if (!writer.equals(templateId1, templateId2)) {
            entity1.setTemplateId(templateId2);
            jsonMap = writer.write(jsonMap, "templateId", templateId2);
        }
        long exp1 = entity1.getExp();
        long exp2 = entity2.getExp();
        if (exp1 != exp2) {
            entity1.setExp(exp2);
            jsonMap = writer.write(jsonMap, "exp", exp2);
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
        int careerType1 = entity1.getCareerType();
        int careerType2 = entity2.getCareerType();
        if (careerType1 != careerType2) {
            entity1.setCareerType(careerType2);
            jsonMap = writer.write(jsonMap, "careerType", careerType2);
        }
        return jsonMap;
    }

    @Override
    public boolean hasChanged(FSHero entity1, FSHero entity2) {
        if (!writer.equals(entity1.getTemplateId(), entity2.getTemplateId())) {
            return true;
        }
        if (entity1.getExp() != entity2.getExp()) {
            return true;
        }
        if (entity1.getLevel() != entity2.getLevel()) {
            return true;
        }
        if (entity1.getStarLevel() != entity2.getStarLevel()) {
            return true;
        }
        if (!writer.equals(entity1.getQualityId(), entity2.getQualityId())) {
            return true;
        }
        if (entity1.getCareerType() != entity2.getCareerType()) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(FSHero entity) {
        JSONObject json = new JSONObject(9);
        json.put("templateId", entity.getTemplateId());
        json.put("exp", entity.getExp());
        json.put("level", entity.getLevel());
        json.put("starLevel", entity.getStarLevel());
        json.put("qualityId", entity.getQualityId());
        json.put("careerType", entity.getCareerType());
        return json;
    }

}