package com.rw.trace.parser;

import com.rw.fsutil.dao.cache.record.JsonValueWriter;
import java.util.Map;
import com.rwbase.dao.email.EmailItem;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rwbase.dao.email.TableEmail;
import com.rw.fsutil.common.Pair;
import com.alibaba.fastjson.JSONObject;

public class TableEmailParser implements DataValueParser<TableEmail> {

    private JsonValueWriter writer = JsonValueWriter.getInstance();

    @Override
    public TableEmail copy(TableEmail entity) {
        TableEmail tableEmailCopy = new TableEmail();
        tableEmailCopy.setUserId(entity.getUserId());
        tableEmailCopy.setEmailList(writer.copyObject(entity.getEmailList()));
        return tableEmailCopy;
    }

    @Override
    public JSONObject recordAndUpdate(TableEmail entity1, TableEmail entity2) {
        JSONObject jsonMap = null;
        String userId1 = entity1.getUserId();
        String userId2 = entity2.getUserId();
        if (!writer.equals(userId1, userId2)) {
            entity1.setUserId(userId2);
            jsonMap = writer.write(jsonMap, "userId", userId2);
        }
        Map<String, EmailItem> emailList1 = entity1.getEmailList();
        Map<String, EmailItem> emailList2 = entity2.getEmailList();
        Pair<Map<String, EmailItem>, JSONObject> emailListPair = writer.checkObject(jsonMap, "emailList", emailList1, emailList2);
        if (emailListPair != null) {
            emailList1 = emailListPair.getT1();
            entity1.setEmailList(emailList1);
            jsonMap = emailListPair.getT2();
        } else {
            jsonMap = writer.compareSetDiff(jsonMap, "emailList", emailList1, emailList2);
        }

        return jsonMap;
    }

    @Override
    public boolean hasChanged(TableEmail entity1, TableEmail entity2) {
        if (!writer.equals(entity1.getUserId(), entity2.getUserId())) {
            return true;
        }
        if (writer.hasChanged(entity1.getEmailList(), entity2.getEmailList())) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJson(TableEmail entity) {
        JSONObject json = new JSONObject(3);
        json.put("userId", entity.getUserId());
        Object emailListJson = writer.toJSON(entity.getEmailList());
        if (emailListJson != null) {
            json.put("emailList", emailListJson);
        }
        return json;
    }

}