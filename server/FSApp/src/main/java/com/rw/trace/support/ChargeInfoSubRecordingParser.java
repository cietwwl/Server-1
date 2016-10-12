package com.rw.trace.support;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.playerdata.charge.dao.ChargeInfoSubRecording;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.alibaba.fastjson.JSONObject;

public class ChargeInfoSubRecordingParser implements DataValueParser<ChargeInfoSubRecording> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public ChargeInfoSubRecording copy(ChargeInfoSubRecording entity) {
        ChargeInfoSubRecording chargeInfoSubRecordingCopy = new ChargeInfoSubRecording();
        chargeInfoSubRecordingCopy.setId(entity.getId());
        chargeInfoSubRecordingCopy.setCount(entity.getCount());
        return chargeInfoSubRecordingCopy;
    }

    @Override
    public JSONObject recordAndUpdate(ChargeInfoSubRecording entity1, ChargeInfoSubRecording entity2) {
        JSONObject jsonMap = null;
        String id1 = entity1.getId();
        String id2 = entity2.getId();
        if (!writer.equals(id1, id2)) {
            entity1.setId(id2);
            jsonMap = writer.write(jsonMap, "id", id2);
        }
        int count1 = entity1.getCount();
        int count2 = entity2.getCount();
        if (count1 != count2) {
            entity1.setCount(count2);
            jsonMap = writer.write(jsonMap, "count", count2);
        }
        return jsonMap;
    }

    @Override
    public boolean hasChanged(ChargeInfoSubRecording entity1, ChargeInfoSubRecording entity2) {
        if (!writer.equals(entity1.getId(), entity2.getId())) {
            return true;
        }
        if (entity1.getCount() != entity2.getCount()) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(ChargeInfoSubRecording entity) {
        JSONObject json = new JSONObject(2);
        json.put("id", entity.getId());
        json.put("count", entity.getCount());
        return json;
    }

}