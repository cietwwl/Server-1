package com.rw.trace.parser;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import java.util.List;
import com.rwbase.dao.fetters.pojo.MagicEquipFetterRecord;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.fsutil.common.Pair;
import com.alibaba.fastjson.JSONObject;

public class MagicEquipFetterRecordParser implements DataValueParser<MagicEquipFetterRecord> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public MagicEquipFetterRecord copy(MagicEquipFetterRecord entity) {
        MagicEquipFetterRecord magicEquipFetterRecordCopy = new MagicEquipFetterRecord();
        magicEquipFetterRecordCopy.setId(entity.getId());
        magicEquipFetterRecordCopy.setUserId(entity.getUserId());
        magicEquipFetterRecordCopy.setFixEquipFetters(writer.copyObject(entity.getFixEquipFetters()));
        magicEquipFetterRecordCopy.setMagicFetters(writer.copyObject(entity.getMagicFetters()));
        return magicEquipFetterRecordCopy;
    }

    @Override
    public JSONObject recordAndUpdate(MagicEquipFetterRecord entity1, MagicEquipFetterRecord entity2) {
        JSONObject jsonMap = null;
        String id1 = entity1.getId();
        String id2 = entity2.getId();
        if (!writer.equals(id1, id2)) {
            entity1.setId(id2);
            jsonMap = writer.write(jsonMap, "id", id2);
        }
        String userId1 = entity1.getUserId();
        String userId2 = entity2.getUserId();
        if (!writer.equals(userId1, userId2)) {
            entity1.setUserId(userId2);
            jsonMap = writer.write(jsonMap, "userId", userId2);
        }
        List<Integer> fixEquipFetters1 = entity1.getFixEquipFetters();
        List<Integer> fixEquipFetters2 = entity2.getFixEquipFetters();
        Pair<List<Integer>, JSONObject> fixEquipFettersPair = writer.checkObject(jsonMap, "fixEquipFetters", fixEquipFetters1, fixEquipFetters2);
        if (fixEquipFettersPair != null) {
            fixEquipFetters1 = fixEquipFettersPair.getT1();
            entity1.setFixEquipFetters(fixEquipFetters1);
            jsonMap = fixEquipFettersPair.getT2();
        } else {
            jsonMap = writer.compareSetDiff(jsonMap, "fixEquipFetters", fixEquipFetters1, fixEquipFetters2);
        }

        List<Integer> magicFetters1 = entity1.getMagicFetters();
        List<Integer> magicFetters2 = entity2.getMagicFetters();
        Pair<List<Integer>, JSONObject> magicFettersPair = writer.checkObject(jsonMap, "magicFetters", magicFetters1, magicFetters2);
        if (magicFettersPair != null) {
            magicFetters1 = magicFettersPair.getT1();
            entity1.setMagicFetters(magicFetters1);
            jsonMap = magicFettersPair.getT2();
        } else {
            jsonMap = writer.compareSetDiff(jsonMap, "magicFetters", magicFetters1, magicFetters2);
        }

        return jsonMap;
    }

    @Override
    public boolean hasChanged(MagicEquipFetterRecord entity1, MagicEquipFetterRecord entity2) {
        if (!writer.equals(entity1.getId(), entity2.getId())) {
            return true;
        }
        if (!writer.equals(entity1.getUserId(), entity2.getUserId())) {
            return true;
        }
        if (writer.hasChanged(entity1.getFixEquipFetters(), entity2.getFixEquipFetters())) {
            return true;
        }
        if (writer.hasChanged(entity1.getMagicFetters(), entity2.getMagicFetters())) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(MagicEquipFetterRecord entity) {
        JSONObject json = new JSONObject(6);
        json.put("id", entity.getId());
        json.put("userId", entity.getUserId());
        Object fixEquipFettersJson = writer.toJSON(entity.getFixEquipFetters());
        if (fixEquipFettersJson != null) {
            json.put("fixEquipFetters", fixEquipFettersJson);
        }
        Object magicFettersJson = writer.toJSON(entity.getMagicFetters());
        if (magicFettersJson != null) {
            json.put("magicFetters", magicFettersJson);
        }
        return json;
    }

}