package com.rw.trace.parser;

import java.util.List;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import com.rwbase.dao.copypve.pojo.CopyData;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.copypve.pojo.TableCopyData;
import com.alibaba.fastjson.JSONObject;

public class TableCopyDataParser implements DataValueParser<TableCopyData> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public TableCopyData copy(TableCopyData entity) {
        TableCopyData newData_ = new TableCopyData();
        newData_.setUserId(entity.getUserId());
        newData_.setCopyList(writer.copyObject(entity.getCopyList()));
        return newData_;
    }

    @Override
    public JSONObject recordAndUpdate(TableCopyData entity1, TableCopyData entity2) {
        JSONObject jsonMap = null;
        String userId1 = entity1.getUserId();
        String userId2 = entity2.getUserId();
        if (!writer.equals(userId1, userId2)) {
            entity1.setUserId(userId2);
            jsonMap = writer.write(jsonMap, "userId", userId2);
        }
        List<CopyData> copyList1 = entity1.getCopyList();
        List<CopyData> copyList2 = entity2.getCopyList();
        Pair<List<CopyData>, JSONObject> copyListPair = writer.checkObject(jsonMap, "copyList", copyList1, copyList2);
        if (copyListPair != null) {
            copyList1 = copyListPair.getT1();
            entity1.setCopyList(copyList1);
            jsonMap = copyListPair.getT2();
        } else {
            jsonMap = writer.compareSetDiff(jsonMap, "copyList", copyList1, copyList2);
        }

        return jsonMap;
    }

    @Override
    public JSONObject toJson(TableCopyData entity) {
        JSONObject json = new JSONObject(2);
        json.put("userId", entity.getUserId());
        Object copyListJson = writer.toJSON(entity.getCopyList());
        if (copyListJson != null) {
            json.put("copyList", copyListJson);
        }
        return json;
    }

}