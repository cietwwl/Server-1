package com.rw.trace.support;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.playerdata.MapAnimationState;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.alibaba.fastjson.JSONObject;

public class MapAnimationStateParser implements DataValueParser<MapAnimationState> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public MapAnimationState copy(MapAnimationState entity) {
        MapAnimationState mapAnimationStateCopy = new MapAnimationState();
        mapAnimationStateCopy.setNormalMapId(entity.getNormalMapId());
        mapAnimationStateCopy.setNormalAnimState(entity.getNormalAnimState());
        mapAnimationStateCopy.setEliteMapId(entity.getEliteMapId());
        mapAnimationStateCopy.setEliteAnimState(entity.getEliteAnimState());
        return mapAnimationStateCopy;
    }

    @Override
    public JSONObject recordAndUpdate(MapAnimationState entity1, MapAnimationState entity2) {
        JSONObject jsonMap = null;
        int normalMapId1 = entity1.getNormalMapId();
        int normalMapId2 = entity2.getNormalMapId();
        if (normalMapId1 != normalMapId2) {
            entity1.setNormalMapId(normalMapId2);
            jsonMap = writer.write(jsonMap, "normalMapId", normalMapId2);
        }
        int normalAnimState1 = entity1.getNormalAnimState();
        int normalAnimState2 = entity2.getNormalAnimState();
        if (normalAnimState1 != normalAnimState2) {
            entity1.setNormalAnimState(normalAnimState2);
            jsonMap = writer.write(jsonMap, "normalAnimState", normalAnimState2);
        }
        int eliteMapId1 = entity1.getEliteMapId();
        int eliteMapId2 = entity2.getEliteMapId();
        if (eliteMapId1 != eliteMapId2) {
            entity1.setEliteMapId(eliteMapId2);
            jsonMap = writer.write(jsonMap, "eliteMapId", eliteMapId2);
        }
        int eliteAnimState1 = entity1.getEliteAnimState();
        int eliteAnimState2 = entity2.getEliteAnimState();
        if (eliteAnimState1 != eliteAnimState2) {
            entity1.setEliteAnimState(eliteAnimState2);
            jsonMap = writer.write(jsonMap, "eliteAnimState", eliteAnimState2);
        }
        return jsonMap;
    }

    @Override
    public boolean hasChanged(MapAnimationState entity1, MapAnimationState entity2) {
        if (entity1.getNormalMapId() != entity2.getNormalMapId()) {
            return true;
        }
        if (entity1.getNormalAnimState() != entity2.getNormalAnimState()) {
            return true;
        }
        if (entity1.getEliteMapId() != entity2.getEliteMapId()) {
            return true;
        }
        if (entity1.getEliteAnimState() != entity2.getEliteAnimState()) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(MapAnimationState entity) {
        JSONObject json = new JSONObject(6);
        json.put("normalMapId", entity.getNormalMapId());
        json.put("normalAnimState", entity.getNormalAnimState());
        json.put("eliteMapId", entity.getEliteMapId());
        json.put("eliteAnimState", entity.getEliteAnimState());
        return json;
    }

}