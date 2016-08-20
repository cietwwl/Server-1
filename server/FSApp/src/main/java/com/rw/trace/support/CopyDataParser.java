package com.rw.trace.support;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rwbase.dao.copypve.pojo.CopyData;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.alibaba.fastjson.JSONObject;

public class CopyDataParser implements DataValueParser<CopyData> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public CopyData copy(CopyData entity) {
        CopyData newData_ = new CopyData();
        newData_.setInfoId(entity.getInfoId());
        newData_.setCopyType(entity.getCopyType());
        newData_.setCopyCount(entity.getCopyCount());
        newData_.setResetCount(entity.getResetCount());
        newData_.setLastFreeResetTime(entity.getLastFreeResetTime());
        newData_.setLastChallengeTime(entity.getLastChallengeTime());
        return newData_;
    }

    @Override
    public JSONObject recordAndUpdate(CopyData entity1, CopyData entity2) {
        JSONObject jsonMap = null;
        int infoId1 = entity1.getInfoId();
        int infoId2 = entity2.getInfoId();
        if (infoId1 != infoId2) {
            entity1.setInfoId(infoId2);
            jsonMap = writer.write(jsonMap, "infoId", infoId2);
        }
        int copyType1 = entity1.getCopyType();
        int copyType2 = entity2.getCopyType();
        if (copyType1 != copyType2) {
            entity1.setCopyType(copyType2);
            jsonMap = writer.write(jsonMap, "copyType", copyType2);
        }
        int copyCount1 = entity1.getCopyCount();
        int copyCount2 = entity2.getCopyCount();
        if (copyCount1 != copyCount2) {
            entity1.setCopyCount(copyCount2);
            jsonMap = writer.write(jsonMap, "copyCount", copyCount2);
        }
        int resetCount1 = entity1.getResetCount();
        int resetCount2 = entity2.getResetCount();
        if (resetCount1 != resetCount2) {
            entity1.setResetCount(resetCount2);
            jsonMap = writer.write(jsonMap, "resetCount", resetCount2);
        }
        long lastFreeResetTime1 = entity1.getLastFreeResetTime();
        long lastFreeResetTime2 = entity2.getLastFreeResetTime();
        if (lastFreeResetTime1 != lastFreeResetTime2) {
            entity1.setLastFreeResetTime(lastFreeResetTime2);
            jsonMap = writer.write(jsonMap, "lastFreeResetTime", lastFreeResetTime2);
        }
        long lastChallengeTime1 = entity1.getLastChallengeTime();
        long lastChallengeTime2 = entity2.getLastChallengeTime();
        if (lastChallengeTime1 != lastChallengeTime2) {
            entity1.setLastChallengeTime(lastChallengeTime2);
            jsonMap = writer.write(jsonMap, "lastChallengeTime", lastChallengeTime2);
        }
        return jsonMap;
    }

    @Override
    public JSONObject toJson(CopyData entity) {
        JSONObject json = new JSONObject(6);
        json.put("infoId", entity.getInfoId());
        json.put("copyType", entity.getCopyType());
        json.put("copyCount", entity.getCopyCount());
        json.put("resetCount", entity.getResetCount());
        json.put("lastFreeResetTime", entity.getLastFreeResetTime());
        json.put("lastChallengeTime", entity.getLastChallengeTime());
        return json;
    }

}