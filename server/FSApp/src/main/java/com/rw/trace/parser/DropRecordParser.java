package com.rw.trace.parser;

import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import java.util.concurrent.ConcurrentHashMap;
import com.rw.service.dropitem.DropResult;
import com.alibaba.fastjson.JSONObject;
import com.rwbase.dao.dropitem.DropRecord;

public class DropRecordParser implements DataValueParser<DropRecord> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public DropRecord copy(DropRecord entity) {
        DropRecord newData_ = new DropRecord();
        newData_.setUserId(entity.getUserId());
        newData_.setFirstDropMap(writer.copyObject(entity.getFirstDropMap()));
        newData_.setDropMissTimesMap(writer.copyObject(entity.getDropMissTimesMap()));
        newData_.setPretreatMap(writer.copyObject(entity.getPretreatMap()));
        return newData_;
    }

    @Override
    public JSONObject recordAndUpdate(DropRecord entity1, DropRecord entity2) {
        JSONObject jsonMap = null;
        String userId1 = entity1.getUserId();
        String userId2 = entity2.getUserId();
        if (!writer.equals(userId1, userId2)) {
            entity1.setUserId(userId2);
            jsonMap = writer.write(jsonMap, "userId", userId2);
        }
        ConcurrentHashMap<Integer, Integer> firstDropMap1 = entity1.getFirstDropMap();
        ConcurrentHashMap<Integer, Integer> firstDropMap2 = entity2.getFirstDropMap();
        Pair<ConcurrentHashMap<Integer, Integer>, JSONObject> firstDropMapPair = writer.checkObject(jsonMap, "firstDropMap", firstDropMap1, firstDropMap2);
        if (firstDropMapPair != null) {
            firstDropMap1 = firstDropMapPair.getT1();
            entity1.setFirstDropMap(firstDropMap1);
            jsonMap = firstDropMapPair.getT2();
        } else {
            jsonMap = writer.compareSetDiff(jsonMap, "firstDropMap", firstDropMap1, firstDropMap2);
        }

        ConcurrentHashMap<Integer, Integer> dropMissTimesMap1 = entity1.getDropMissTimesMap();
        ConcurrentHashMap<Integer, Integer> dropMissTimesMap2 = entity2.getDropMissTimesMap();
        Pair<ConcurrentHashMap<Integer, Integer>, JSONObject> dropMissTimesMapPair = writer.checkObject(jsonMap, "dropMissTimesMap", dropMissTimesMap1, dropMissTimesMap2);
        if (dropMissTimesMapPair != null) {
            dropMissTimesMap1 = dropMissTimesMapPair.getT1();
            entity1.setDropMissTimesMap(dropMissTimesMap1);
            jsonMap = dropMissTimesMapPair.getT2();
        } else {
            jsonMap = writer.compareSetDiff(jsonMap, "dropMissTimesMap", dropMissTimesMap1, dropMissTimesMap2);
        }

        ConcurrentHashMap<Integer, DropResult> pretreatMap1 = entity1.getPretreatMap();
        ConcurrentHashMap<Integer, DropResult> pretreatMap2 = entity2.getPretreatMap();
        Pair<ConcurrentHashMap<Integer, DropResult>, JSONObject> pretreatMapPair = writer.checkObject(jsonMap, "pretreatMap", pretreatMap1, pretreatMap2);
        if (pretreatMapPair != null) {
            pretreatMap1 = pretreatMapPair.getT1();
            entity1.setPretreatMap(pretreatMap1);
            jsonMap = pretreatMapPair.getT2();
        } else {
            jsonMap = writer.compareSetDiff(jsonMap, "pretreatMap", pretreatMap1, pretreatMap2);
        }

        return jsonMap;
    }

    @Override
    public JSONObject toJson(DropRecord entity) {
        JSONObject json = new JSONObject(4);
        json.put("userId", entity.getUserId());
        Object firstDropMapJson = writer.toJSON(entity.getFirstDropMap());
        if (firstDropMapJson != null) {
            json.put("firstDropMap", firstDropMapJson);
        }
        Object dropMissTimesMapJson = writer.toJSON(entity.getDropMissTimesMap());
        if (dropMissTimesMapJson != null) {
            json.put("dropMissTimesMap", dropMissTimesMapJson);
        }
        Object pretreatMapJson = writer.toJSON(entity.getPretreatMap());
        if (pretreatMapJson != null) {
            json.put("pretreatMap", pretreatMapJson);
        }
        return json;
    }

}